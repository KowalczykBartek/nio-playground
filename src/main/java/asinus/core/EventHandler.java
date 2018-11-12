package asinus.core;

import java.nio.ByteBuffer;

/**
 * Expose something that we can call handler, it is always executed from event-loop that owns socket passed
 * with Context instance.
 */
public interface EventHandler {
    void handle(final WorkerThread workerThread, final Context api, final ByteBuffer data);
}
