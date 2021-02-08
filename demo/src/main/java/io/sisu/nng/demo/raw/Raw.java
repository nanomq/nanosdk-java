package io.sisu.nng.demo.raw;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.aio.Aio;
import io.sisu.nng.aio.AioCallback;
import io.sisu.nng.reqrep.RawRep0Socket;
import io.sisu.nng.reqrep.Req0Socket;

import java.util.ArrayList;
import java.util.List;

public class Raw {

    private static final int PARALLEL = 128;

    enum State {
        RECV, SEND, WAIT;
    }

    static class Work {
        public State state;
        public Message message;

        public Work(State state) {
            this.state = state;
            this.message = null;
        }
    }

    static class Server {
        final String url;
        final List<Aio> aios = new ArrayList<>();

        public Server(String url) {
            this.url = url;
        }

        public void run() throws NngException, InterruptedException {
            final Socket socket = new RawRep0Socket();
            socket.listen(url);
            System.out.println("Server listening on " + url);

            for (int i=0; i<PARALLEL; i++) {
                Work w = new Work(State.RECV);

                AioCallback<Work> cb = new AioCallback<>((aioProxy, work) -> {
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
                }, w);

                Aio aio = new Aio(cb);
                aios.add(aio);
                aio.receive(socket);
            }
            Thread.sleep(3600000);
            socket.close();
        }
    }

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
