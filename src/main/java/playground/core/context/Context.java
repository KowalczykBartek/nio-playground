package playground.core.context;

import playground.core.WorkerThread;
import playground.core.functional.Promise;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * Expose interface for user that allows him comfortably use WorkerThread instance.
 */
public class Context implements WritableContext {

    private final SelectionKey selectionKey;
    private final WorkerThread workerThread;

    public Context(final WorkerThread workerThread, final SelectionKey selectionKey) {
        this.workerThread = workerThread;
        this.selectionKey = selectionKey;
    }

    @Override
    public Promise write(final Context context, final ByteBuffer data) {
        return workerThread.write(selectionKey, data);
    }
}
