package io.sisu.nng.demo.async;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.aio.Context;
import io.sisu.nng.reqrep.Rep0Socket;

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

    public static void main(String argv[]) {
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
