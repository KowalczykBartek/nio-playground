package example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import playground.core.EventHandler;
import playground.core.ServerLoop;

import java.io.IOException;

/**
 * Just an example.
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(final String... args) throws IOException {

        /**
         * Perform write on {@param context}.
         */
        final EventHandler handler = (context, data) -> {

            final int remaining = data.remaining();
            final byte[] rawData = new byte[remaining];
            data.get(rawData);
            final String request = new String(rawData);

            LOG.info("Received request, as string {}", request);

            data.flip();
            context.write(context, data)//
                    .setCallback(isSuccess -> LOG.info("Hey ! i was called after writing data to wire !"));
        };

        final ServerLoop serverLoop = new ServerLoop(null, 8080, handler);
        serverLoop.start();
    }
}
