package example;

import asinus.core.ServerLoop;

import java.io.IOException;

/**
 * Just an example.
 */
public class Main {
    public static void main(final String... args) throws IOException {
        final ServerLoop serverLoop = new ServerLoop(null, 8080);
        serverLoop.start();
    }
}
