package playground.core.functional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Used during asynchronous tasks execution to allow user to register callback when this asynchronous task
 * completes.
 * This is not thread safe, this promise has to be always used in the same thread.
 */
public class Promise {
    private static final Logger LOG = LoggerFactory.getLogger(Promise.class);
    private Consumer<Boolean> callback;

    public void setCallback(final Consumer<Boolean> callback) {
        this.callback = callback;
    }

    public void complete(final boolean success) {
        if (callback == null)
            return;

        try {
            callback.accept(success);
        } catch (final Exception ex) {
            LOG.error("Error occurred during promise handling {}", ex);
        }
    }
}
