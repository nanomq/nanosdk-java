# nng-java demos
This aims to recreate the original nng demos in Java as both a form of
functional validation as well as a means of illustrating how to use the Java
api.

A gradle task is provided to build a "fat jar" to make them easier to run.
From the root project directory, use the gradle wrapper to call the task:

```
$ ./gradlew demo:shadowJar
```

Running a demo should then be as easy as:

```
$ java -cp ./demo/libs/demo-${version}-all.jar <demo class> <...args>
```

Where `${version}` is the current version of the nng-java project.

> The upstream nng demos are here:
> https://github.com/nanomsg/nng/tree/master/demo

All the demos take an nng url, so you can experiment with different transports
like `tcp` and `ipc`.

## Async
The async demo includes a `Client` and a `Server` utilizing the Req/Rep
protocol. It highlights using AIO concurrency to scale the server side.

The demo is simple:
* A client uses a `Req0` socket to send a Message to the server containing a
  number that represents the number of milliseconds to wait before replying
* The server uses multiple Contexts on a `Rep0` socket to listen for requests
* When the server gets a request, it parses the message body into a number `n`
* The server then performs an async sleep for `n` milliseconds, simulating
  being busy processing the request
* When the Context wakes from sleep, it replies to the client

To start the server, provide a valid nng url to listen on:

```
$ java -cp ./demo/libs/demo-${version}-all.jar \
    io.sisu.nng.demo.async.Server \
    tcp://localhost:9999
```

Then start some clients, providing a "wait time" argument (in milliseconds):

```
$ java -cp ./demo/libs/demo-${version}-all.jar \
    io.sisu.nng.demo.async.Client \
    tcp://localhost:9999 100
```

### Raw
The raw demo demonstrates multiple concepts:

1. How to utilize the lower-level AIO api in Java, using AioCallbacks directly
   instead of higher-level constructs like Contexts
2. Using raw-mode Sockets (instead of regular "cooked" Sockets)

> It's my understanding certain protocols do not support Contexts and directly 
> using AIOs is the only way for event-based messaging

The server logic mostly mirrors the [async](#async) demo in that it receives
a number from the client that dictates a simulated "busy time."

The client is similar as well, but takes the time in _seconds_ (not ms).

To run, there's a single main class for client and server, but the client mode
is enabled by adding a numeric sleep argument while the server takes a `-s`
flag:

```
$ java -cp ./demo/libs/demo-${version}-all.jar \
    io.sisu.nng.demo.raw.Raw \
    tcp://localhost:9999 [-s | <sleep time in seconds>]
```

### ReqRep
This demo is a simplistic RPC service providing a `DATE` service for clients
to call and receive a UNIX timestamp as a response.

It uses a blocking, synchronous approach to directly reading messages from a
Socket.

For the server, start via:

```
$ java -cp ./demo/libs/demo-${version}-all.jar \
    io.sisu.nng.demo.reqreq.ReqRep server
    tcp://localhost:9999
```

And the client:

```
$ java -cp ./demo/libs/demo-${version}-all.jar \
    io.sisu.nng.demo.reqreq.ReqRep client
    tcp://localhost:9999
```
