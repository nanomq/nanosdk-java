# nng-java

An idiomatic Java wrapper around [nng](https://nng.nanomsg.org) using 
[JNA](https://github.com/java-native-access/jna).

## Goals
1. Wrap nng in a way that can be used "naturally" via Java idioms so users can
   quickly spin up sockets, dial/listen, and send/receive data without all the
   pomp and circumstance seen in the C code (because Java has enough pomp and
   circumstance to deal with, _amiright_).
2. Keep the wrapping (via JNA) at just the public API surface allowing it to
   track releases that might have internal (i.e. nni_*) changes only.
3. Use a (somewhat) strong typing using JNA's extensible type system to keep
   usage of the library somewhat safe from memory issues.
4. On the topic of memory issues, provide guardrails around protecting the
   native memory managed by nng from Java's garbage collector.
5. Lastly, provide all the scalability constructs from nng (e.g. Aio) in a way
   that let's Java developers utilize nng's performance and thread safety.
   
> My downline goal is to write my own protocol in an nng extension and use it in
> a Java application...but we'll see if/when I get there.

## Current State
As of _8 Feb 2021_, the core nng primitives are implemented allowing a Java
developer to:

* instantiate and use `Socket`'s that implement the _Scalability Protocols_
* use `Context`'s for multi-threaded, async use of a single `Socket`
* use the `Aio` framework in 3 different ways for async operations:
  1. in blocking (i.e. non-async) manner...the trivial use of a `Context`
  2. in non-blocking manner using the Java `CompletableFuture` API
  3. in an event-driver manner using `AioCallback`s
* allocated `Message`s without worrying too much about memory management in
  some simpler cases
  
> The AIO stuff needs some tire kicking, is a bit messy, and I'm not too happy
> with it yet. In short, the design lets you keep as much state in the JVM as
> possible (for callbacks and the like), but seems overly complicated. (Maybe
> it's just that way because of Java?)

Things **not yet working or implemented**:

* the http client api
* the http server api
* stream wrangling
* TLS support has not been tested

> It's not quite clear how much of the HTTP stuff I want to implement, buf if
> there's anything required for WebSocket support it will be on the list.

## Building
1. I've done nothing (yet) to package nng, so you'll need to build and
   install that yourself. It's really not that hard.
2. A simple `gradlew build` should suffice, using the supplied Gradle
   wrapper scripts.

## Using
### Finding the nng Library
Once you have nng installed somewhere, if it's not installed in a sane default
location, you might need to set one of the following to point JNA to the
correct location:

- Java Property: `jna.library.path`
- Environment variable: `JNA_LIBRARY_PATH`

> Note: The Java Property will override any value set in the environment

### Using the native API
In general, most users should only need to utilize classes in the 
`io.sisu.nng` and `io.sisu.nng.aio` namespaces. The direct calling of the nng
api should be wrapped for you.

For those looking to do low-level nng programming, most of the nng api is
accessible via `io.sisu.nng.Nng`. Everything you need for low-level usage
should be in the `io.sisu.nng.internal` namespace, with a few exceptions.

### The Java NNG API
As part of my Java-fication of nng, I'm currently marrying Sockets to their
protocols explicitly. (Maybe I'll go full on OOP and go overboard here...tbd.)

For detailed examples, check the [demo](demo/) project. The goal is to provide
translations of the existing NNG demos into Java versions to illustrate any
similarities or differences in usage (as well to provide a way to validate if
the Java implementation is working).

A simple [example](src/test/java/io/sisu/nng/Example.java) of how the Java API
works:

```java
package io.sisu.nng;
import io.sisu.nng.reqrep.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class Example {
    public static void main(String[] argv) throws NngException {
        final String url = "inproc://example";
        Socket req = new Req0Socket();
        Socket rep = new Rep0Socket();

        rep.listen(url);
        req.dial(url);

        Message msg = new Message();
        msg.append("hey man".getBytes(StandardCharsets.UTF_8));
        req.sendMessage(msg);

        // After sending, we no longer own the Message
        assert(!msg.isValid());

        Message msg2 = rep.receiveMessage();
        assert(msg2.isValid());

        String msg2Str = Charset.defaultCharset()
                .decode(msg2.getBody()).toString();
        assert("hey man".equalsIgnoreCase(msg2Str));
        System.out.println("Rep socket heard: " + msg2Str);
    }
}
```

And a simple example of using a `Context` for asynchronous operations that is
the Java analog to nng's async 
[server.c](https://github.com/nanomsg/nng/blob/master/demo/async/server.c)
demo code:

```java
package io.sisu.nng.demo.async;

import io.sisu.nng.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Java implementation of the NNG async demo server program.
 *
 * Unlike the C demo, the Java version uses the asynchronous event handler approach provided via
 * the io.sisu.nng.aio.Context class.
 */
public class Server {
    private static final int PARALLEL = 128;
    private final String url;

    public Server(String url) {
        this.url = url;
    }

    public void start() throws Exception {
        try (Socket socket = new Rep0Socket()) {
            // keep Context references to prevent any possible gc
            List<Context> contexts = new ArrayList<>(PARALLEL);

            for (int i = 0; i < PARALLEL; i++) {
                Context ctx = new Context(socket);
                ctx.setRecvHandler((ctxProxy, msg) -> {
                    try {
                        int when = msg.trim32Bits();
                        ctxProxy.sleep(when);
                        ctxProxy.put("reply", msg);
                    } catch (NngException e) {
                        ctxProxy.receive();
                    }
                });
                ctx.setSendHandler((ctxProxy) -> ctxProxy.receive());
                ctx.setWakeHandler((ctxProxy) -> ctxProxy.send((Message) ctxProxy.get("reply")));

                // perform the initial receive operation to start the "event loop"
                ctx.receiveMessage();
            }

            socket.listen(this.url);
            System.out.println("Listening on " + this.url);

            Thread.sleep(1000 * 60 * 20);
        }
    }

    public static void main(String[] argv) {
        if (argv.length != 1) {
            System.err.println(String.format("Usage: server <url>"));
            System.exit(1);
        }

        Server server = new Server(argv[0]);
        try {
            server.start();
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}
```

> Note: you'll notice the above uses the baked in event handling capabilities
> in the `Context` class. For a more like-for-like example, see the "raw" demo
> in the [demos](./demos) project that shows how to use your own `AioCallback`
> to make a like-for-like version of the nng
> [raw.c](https://github.com/nanomsg/nng/blob/master/demo/raw/raw.c) demo.

## About Memory Management and Safety
Currently, the bare minimum to safely allocate/free native memory for nng
constructs exists, but it's not optimal in preventing things like native heap
fragmentation if a developer just lets the JVM garbage collect `Message`s and
call the message's `free()` method.

While it will probably only occur in long-running, server-based applications,
using the tire-kicking [benchmarks](./benchmarks) project it's been observed
that services running that allocate hundreds of megabytes or gigabytes of
messages via the Java api that _do not manually call `free()` and rely on the
garbage collector_ will experience excessive memory pressure as nng_msg
objects stay allocated on the native heap, causing it to grow, and leading to
fragmentation that is not recoverable.

> tl;dr: if you know you're done with a `Message`, just call `free()` for now.


