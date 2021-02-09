package io.sisu.nng.demo.raw;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.aio.Aio;
import io.sisu.nng.aio.AioCallback;
import io.sisu.nng.aio.AioProxy;
import io.sisu.nng.reqrep.RawRep0Socket;
import io.sisu.nng.reqrep.Req0Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Demonstrate using "raw" Sockets with async messaging for high concurrency messaging.
 * <p>
 * This demo relies on constructing a custom {@link AioCallback} for processing events. In practice,
 * if not using a raw socket, it's easier to use {@link io.sisu.nng.aio.Context}s since they contain
 * pre-build event handlers. (See {@link io.sisu.nng.demo.async.Server} for an example.)
 */
public class Raw {

    // Max concurrency
    private static final int PARALLEL = 128;

    // Our 3 core states. The original nng demo uses 4 (including INIT).
    enum State {
        RECV, SEND, WAIT;
    }

    /**
     * Container for the state of a connection, including a reference to the original client Message
     */
    static class Work {
        public State state;
        public Message message;

        public Work(State state) {
            this.state = state;
            this.message = null;
        }
    }

    /**
     * The server creates {@link #PARALLEL} {@link Aio}
     */
    static class Server {
        final String url;

        // Keep references to each Aio to prevent garbage collection
        final List<Aio> aioList = new ArrayList<>();

        public Server(String url) throws NngException {
            this.url = url;
        }

        public void run() throws NngException, InterruptedException {
            final Socket socket = new RawRep0Socket();
            socket.listen(url);
            System.out.println("Server listening on " + url);

            // Define our event handler for use in the AioCallback. It captures a reference to
            // the server's Socket.
            final BiConsumer<AioProxy, Work> handler = (aioProxy, work) -> {
                try {
                    switch (work.state) {
                        case RECV:
                            aioProxy.assertSuccessful();
                            Message msg = aioProxy.getMessage();
                            int when = msg.trim32Bits();
                            work.message = msg;
                            work.state = State.WAIT;
                            aioProxy.sleep(when);
                            break;

                        case WAIT:
                            aioProxy.setMessage(work.message);
                            work.message = null;
                            work.state = State.SEND;
                            aioProxy.send(socket);
                            break;

                        case SEND:
                            aioProxy.assertSuccessful();
                            work.state = State.RECV;
                            aioProxy.receive(socket);
                            break;

                        default:
                            System.err.println("bad state!");
                            System.exit(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    System.exit(1);
                }
            };

            // Create unique AIOs, each using the same callback handler closure, but with a unique
            // instance of Work as the 2nd argument
            for (int i=0; i<PARALLEL; i++) {
                Work work = new Work(State.RECV);
                Aio aio = new Aio(new AioCallback(handler, work));
                aioList.add(aio);
                aio.receive(socket);
            }

            // Sleep the main thread
            Thread.sleep(3600000);
            socket.close();
        }
    }

    /**
     * The client simply sends a raw request message containing a 32-bit number encoding how long
     * the server should sleep for before responding.
     */
    static class Client {
        final String url;
        final int millis;

        public Client(String url, int millis) {
            this.url = url;
            this.millis = millis;
        }

        public void run() throws NngException {
            Socket socket = new Req0Socket();
            socket.dial(url);

            long start = System.currentTimeMillis();

            Message msg = new Message();
            msg.appendU32(millis);
            socket.sendMessage(msg);

            socket.receiveMessage();
            long end = System.currentTimeMillis();

            socket.close();

            System.out.println(String.format("Request took %d millisecond.", end - start));
        }
    }

    public static void main(String[] argv) throws Exception {
        if (argv.length != 2) {
            System.err.println("Usage: raw <url> [-s|<secs>]");
            return;
        }

        final String url = argv[0];

        if (argv[1].compareTo("-s") == 0) {
            Server server = new Server(url);
            server.run();
        } else {
            Client client = new Client(url, 1000 * Integer.parseInt(argv[1]));
            client.run();
        }
    }
}
