package playground.core.context;

import playground.core.functional.Promise;

import java.nio.ByteBuffer;

public interface WritableContext {
    Promise write(final Context context, final ByteBuffer data);
}
