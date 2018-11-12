package asinus.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wrap worker threads as a atomic group instance.
 */
public class WorkersGroup {
    private final Logger LOG = LoggerFactory.getLogger(WorkersGroup.class);

    private final WorkerThread[] workers;
    private AtomicInteger integer = new AtomicInteger();

    public WorkersGroup(int threadsCount) {
        //todo precondition
        this.workers = new WorkerThread[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            try {
                final WorkerThread workerThread = new WorkerThread();
                workerThread.start(); //happens before
                workers[i] = workerThread;
            } catch (IOException ex) {
                LOG.error("Error occurred {}", ex);
            }
        }
    }

    /**
     * Get next WorkerThread in round robin manner.
     *
     * @return WorkerThread thread.
     */
    public WorkerThread next() {
        final int i = integer.incrementAndGet();
        return workers[i % workers.length];
    }
}
