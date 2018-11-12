package asinus.core;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Don't look here, just use :D
 */
public class Context {
    private final SelectionKey selectionKey;
    public Context(final SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public SelectionKey getSelectionKey()
    {
        return selectionKey;
    }

    public SocketChannel getSocketChannel()
    {
        return (SocketChannel) selectionKey.channel();
    }
}
