package example;

import asinus.core.EventHandler;
import asinus.core.ServerLoop;

import java.io.IOException;

/**
 * Just an example.
 */
public class Main {
    public static void main(final String... args) throws IOException {

        /**
         * {@param nioThread} is your loop owner and {@param context} is socket.
         */
        EventHandler handler = (nioThread, context, data) -> {

            final int remaining = data.remaining();
            final byte[] rawData = new byte[remaining];
            data.get(rawData);
            final String request = new String(rawData);

            System.out.println(request);

            data.flip();
            nioThread.write(context, data);
        };

        final ServerLoop serverLoop = new ServerLoop(null, 8080, handler);
        serverLoop.start();
    }
}
