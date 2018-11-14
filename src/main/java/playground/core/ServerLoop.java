package playground.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

/**
 * Wrap ServerSocketChannel and equip with event loop. This thread performs only accept on incoming connection
 * and pass it further to worker-threads.
 */
public class ServerLoop extends Thread {

    private final Logger LOG = LoggerFactory.getLogger(ServerLoop.class);

    private final InetAddress inetAddress;
    private final int port;

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;

    private final WorkersGroup workersGroup;

    public ServerLoop(final InetAddress address, final int port, final EventHandler handler) throws IOException {

        this.inetAddress = address;
        this.port = port;

        this.selector = SelectorProvider.provider().openSelector();

        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(this.inetAddress, this.port);
        this.serverSocketChannel.socket().bind(inetSocketAddress);

        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //WorkersGroup should be initialized outside the ServerLoop.
        this.workersGroup = new WorkersGroup(Runtime.getRuntime().availableProcessors(), handler);
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.selector.select();
                final Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();

                selectedKeys.forEachRemaining(key -> {
                    if (key.isAcceptable()) {
                        final ServerSocketChannel channel = (ServerSocketChannel) key.channel();

                        try {
                            final SocketChannel accept = channel.accept();
                            workersGroup.next().register(accept);
                        } catch (IOException e) {

                        }
                    }
                });

            } catch (IOException ex) {
                LOG.error("Error occurred in main loop {}", ex);
            }
        }
    }

}
