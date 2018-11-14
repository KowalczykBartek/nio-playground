package playground.core;

import playground.core.context.Context;

import java.nio.ByteBuffer;

/**
 * Expose something that we can call handler, it is always executed from event-loop that owns socket passed
 * with Context instance.
 */
public interface EventHandler {
    /**
     * Handler called from inside event loop, every time when data is read from socket.
     *
     * @param api
     * @param data
     */
    void handle(final Context api, final ByteBuffer data);
}
