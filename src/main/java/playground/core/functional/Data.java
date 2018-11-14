package playground.core.functional;

import java.nio.ByteBuffer;

/**
 * Allow to bind promise with ByteBuffer during write() event. Each socket.write will resolve corresponding promise.
 */
public class Data {
    private final ByteBuffer data;
    private final Promise promise;

    public Data(final ByteBuffer data, final Promise promise) {
        this.data = data;
        this.promise = promise;
    }

    public ByteBuffer getData() {
        return data;
    }

    public Promise getPromise() {
        return promise;
    }
}