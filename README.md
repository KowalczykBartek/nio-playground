[![](https://img.shields.io/badge/unicorn-approved-ff69b4.svg)](https://www.youtube.com/watch?v=9auOCbH5Ns4)
![][license img]

Small (and of course ugly) non blocking server written from scratch, inspired by niotut tutorial but with
(slightly) different threading model. Threading is similar to Netty's workers approach.

## how
Because of the only thing I can do is cloning Netty (because it is cool !), bytes handling is also very similar, just look

```bash
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
```

it has to be passed to tcp server

```bash
final ServerLoop serverLoop = new ServerLoop(null, 8080, handler);
serverLoop.start();
```

## References
http://rox-xmlrpc.sourceforge.net/niotut/

[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg
