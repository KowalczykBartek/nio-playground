package asinus.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * WorkerThread responsible for handling READ/WRITE operations on SelectableChannel in non blocking manner.
 * Writes and reads are performed always inside the same thread, so, think about each operation inside this class
 * as being single threaded program :)
 * <p>
 * Yes, if are familiar with Netty internals, you can note that i just copied they idea :)
 */
public class WorkerThread extends Thread {
    private final Logger LOG = LoggerFactory.getLogger(WorkersGroup.class);

    //this field is accessed from other threads, so, make it concurrent.
    private ConcurrentLinkedQueue<Task> pendingTasks = new ConcurrentLinkedQueue();

    private Selector selector;
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    private Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();

    public WorkerThread() throws IOException {
        selector = SelectorProvider.provider().openSelector();
    }

    @Override
    public void run() {
        while (true) {
            try {
                //todo io/task ratio
                while (!pendingTasks.isEmpty()) {
                    pendingTasks.poll().perform(this);
                }

                this.selector.select();
                final Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();

                iterator.forEachRemaining(key -> {

                    if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }

                });

            } catch (IOException ex) {
                LOG.error("Error occurred in main worker thread. {}", ex);

            }
        }
    }

    /**
     * Write as much data as possible from {@param pendingData} and if there will be more stuff to push downstream,
     * SelectionKey will be set to OP_READ on this channel. Before write, SelectionKey.OP_WRITE has to be set.
     *
     * @param key
     */
    private void write(final SelectionKey key) {
        final SocketChannel channel = (SocketChannel) key.channel();
        final Queue<ByteBuffer> bufferQueue = pendingData.get(channel);
        if (bufferQueue != null) {
            while (!bufferQueue.isEmpty()) {
                final ByteBuffer buffer = bufferQueue.peek();
                try {
                    channel.write(buffer);
                    if (buffer.remaining() > 0) {
                        //ok, call me later when you will be ready to get more data.
                        return;
                    }
                    bufferQueue.remove();
                } catch (IOException ex) {
                    LOG.error("Unable to perform write operation {}", ex);
                    try {
                        channel.close();
                    } catch (IOException e) {
                        //todo
                        LOG.error("Unable to perform write operation {}", ex);
                    }
                    key.cancel();
                    pendingData.remove(channel); /* todo */
                }
            }
        }

        //no more data, switch to OP_READ only.
        key.interestOps(SelectionKey.OP_READ);
    }

    /**
     * Read data from the socket.
     *
     * @param key
     */
    private void read(final SelectionKey key) {
        final SocketChannel channel = (SocketChannel) key.channel();
        readBuffer.clear();
        try {
            final int read = channel.read(readBuffer);
            byte[] dataCopy = new byte[read];
            readBuffer.flip();
            readBuffer.get(dataCopy);
//            final ByteBuffer dataToPassFuther = ByteBuffer.wrap(dataCopy);
            System.out.println(new String(dataCopy));

            /*
             * MOCK REDIS RESPONSE OPERATION
             * TODO extract as handler.
             */

            final Queue<ByteBuffer> bufferQueue = pendingData.get(channel);
            if (bufferQueue != null) {
                bufferQueue.add(ByteBuffer.wrap("+PONG\n\r".getBytes()));
                flushRead(key);
            } else {
                final LinkedList<ByteBuffer> newBufferQueue = new LinkedList<>();
                newBufferQueue.add(ByteBuffer.wrap("+PONG\n\r".getBytes()));
                pendingData.put(channel, newBufferQueue);
                flushRead(key);
            }

        } catch (IOException ex) {
            LOG.error("Unable to perform read operation {}", ex);
        }
    }

    private void flushRead(final SelectionKey key) {
        key.interestOps(SelectionKey.OP_WRITE);
        this.selector.wakeup();
    }

    public void register(final SocketChannel channel) {

        pendingTasks.add((worker -> {
            try {
                channel.configureBlocking(false);
                channel.register(worker.selector, SelectionKey.OP_READ);
            } catch (IOException ex) {
                LOG.error("Not able to configure new channel {}", ex);
            }

        }));

        this.selector.wakeup();
    }
}
