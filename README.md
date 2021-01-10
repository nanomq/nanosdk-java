# nng-java

An experiment of wrapping [nng](https://nng.nanomsg.org) using 
[JNA](https://github.com/java-native-access/jna).

## Goals
1. Wrap nng in a way that can be used "naturally" via Java idioms so I can
   quickly spin up sockets, dial/listen, and send/receive data without all the
   pomp and circumstance seen in the C code (because Java has enough pomp and
   circumstance to deal with, amiright).
2. Keep the wrapping (via JNA) at just the public API surface allowing it to
   track releases that might have internal (i.e. nni_*) changes only.
3. Use a (somewhat) strong typing using JNA's extensible type system to keep
   usage of the library somewhat safe from memory issues.

In general, only the publicly visible types and functions from the official
[man page](https://nng.nanomsg.org/man/v1.3.2) should be accessible.

> This last part is important...it should allow for easier upgrades and avoid
> dealing with all the internal (nni_*) data structures that JNAerator would
> expose if I took the code-generator route.

My downline goal is to write my own protocol in an nng extension and use it in
a Java application...but we'll see if/when I get there.

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
Most of the nng api is accessible via the `io.sisu.nng.Nng` instance of the
`io.sisu.nng.internal.NngLibrary`. Everything you need for low-level usage
should be in the `io.sisu.nng.internal` namespace.

See the unit tests for examples as they exist primarily to validate both my
understanding of nng, but also my use of JNA to expose the library.

### Using the Java version
As part of my Java-fication of nng, I'm currently marrying Sockets to their
protocols explicitly. (Maybe I'll go full on OOP and go overboard here...tbd.)

An [example](./src/test/java/io/sisu/nng/Example.java) of how this simplifies
things:

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

## Random Q's and Thoughts
- Wrapping by hand (as opposed to using a code generator like
  [JNAerator](https://github.com/nativelibs4java/JNAerator) is pretty easy
  given the design of nng.
- Not clear yet what parts are needed vs. not (e.g. supplemental).
- Versioning...makes sense to track the supported NNG version? Not sure.
