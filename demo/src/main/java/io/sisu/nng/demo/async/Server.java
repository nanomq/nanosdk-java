package io.sisu.nng.demo.async;

import io.sisu.nng.Context;
import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.reqrep.Rep0Socket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Server {
    private static final int PARALLEL = 12;
    private final String url;

    public Server(String url) {
        this.url = url;
    }

    public void start() throws Exception {
        Map<Context, CompletableFuture> map = new HashMap<>();

        Socket socket = new Rep0Socket();

        for (int i=0; i < PARALLEL; i++) {
            Context ctx = new Context(socket);
            ctx.setRecvHandler((proxy, msg) -> {
                try {
                    int when = msg.trim32Bits();
                    proxy.sleep(when);
                    proxy.put("reply", msg);
                } catch (NngException e) {
                    proxy.receive();
                }
            });
            ctx.setSendHandler((proxy) -> proxy.receive());
            ctx.setWakeHandler((proxy) -> proxy.send((Message) proxy.get("reply")));

            // initialize context state to RECV
            ctx.receiveMessage();
        }

        socket.listen(this.url);
        System.out.println("Listening on " + this.url);

        Thread.sleep(360000);
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
