package playground.core;

public interface Task {
    /**
     * Perform a task.
     * @param worker
     */
    void perform(final WorkerThread worker);
}
