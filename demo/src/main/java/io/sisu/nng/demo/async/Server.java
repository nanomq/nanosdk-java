package io.sisu.nng.demo.async;

import io.sisu.nng.Context;
import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.aio.AioCallback;
import io.sisu.nng.aio.AioProxy;
import io.sisu.nng.reqrep.Rep0Socket;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PARALLEL = 12;

    private enum State {
        INIT,
        RECV,
        WAIT,
        SEND,
    }

    private static class Work {
        protected State state;
        protected Message msg;

        public Work() {
            this.state = State.INIT;
        }
    }

    private final String url;

    public Server(String url) {
        this.url = url;
    }

    public static void handler(AioProxy aio, Work work) {
        System.out.println(String.format("%s: processing event with state %s",
                Thread.currentThread().getName(), work.state));
        switch (work.state) {
            case INIT:
                work.state = State.RECV;
                aio.recvAsync();
                break;

            case RECV:
                // TODO: check result?
                Message msg = aio.getMessage();
                try {
                    int when = msg.trim32Bits();
                    work.msg = msg;
                    work.state = State.WAIT;
                    System.out.println(String.format("%s: sleeping for %d ms", Thread.currentThread().getName(), when));
                    aio.sleep(when);
                } catch (NngException e) {
                    // ignore message
                    aio.recvAsync();
                }
                break;

            case WAIT:
                aio.setMessage(work.msg);
                work.msg = null;
                work.state = State.SEND;
                aio.sendAsync();
                break;

            case SEND:
                // TODO: check result
                work.state = State.RECV;
                aio.recvAsync();
                break;

            default:
                System.err.println("Bad state!");
                System.exit(1);
        }
    }

    public void start() throws NngException, InterruptedException {
        List<Work> works = new ArrayList<>();
        List<Context> contexts = new ArrayList<>();

        Socket socket = new Rep0Socket();

        for (int i=0; i < PARALLEL; i++) {
            Work work = new Work();
            AioCallback callback = new AioCallback<>(Server::handler, work);
            Context ctx = new Context(socket, callback);

            contexts.add(ctx);
            works.add(work);
            ctx.trigger();
        }

        socket.listen(this.url);

        Thread.sleep(3600000);
    }

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println(String.format("Usage: server <url>"));
            System.exit(1);
        }

        Server server = new Server(argv[0]);

        try {
            server.start();
        } catch (NngException | InterruptedException nngErr) {
            System.err.println(nngErr);
        }
    }

}
