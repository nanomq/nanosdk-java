package io.sisu.nng.demo.reqrep;

import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.reqrep.Rep0Socket;
import io.sisu.nng.reqrep.Req0Socket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ReqRep {
    static final int DATE_CMD = 1;

    interface Demoable {
        void run() throws NngException;
    }

    static class Server implements Demoable {
        private final String url;

        public Server(String url) {
            this.url = url;
        }

        public void run() throws NngException {
            Socket socket = new Rep0Socket();
            socket.listen(this.url);

            ByteBuffer buffer = ByteBuffer.allocate(8);

            while (true) {
                buffer.clear();
                long size = socket.receive(buffer);
                if (size == 8) {
                    System.out.println("SERVER: RECEIVED DATE REQUEST");
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println(String.format("SERVER: SENDING DATE: %s", now.withNano(0)));

                    buffer.putLong(now.toEpochSecond(ZoneOffset.UTC));
                    buffer.flip();

                    socket.send(buffer);
                } else {
                    System.err.println("SERVER: received invalid message (size = " + size + ")");
                }
            }
        }
    }

    static class Client implements Demoable {
        private final String url;
        public Client(String url) {
            this.url = url;
        }

        public void run() throws NngException {
            // PUT64 analog
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putInt(DATE_CMD);
            buffer.putInt(0); // required for padding to send all 8 bytes for now
            buffer.flip();

            Socket socket = new Req0Socket();
            socket.dial(url);

            System.out.println("CLIENT: SENDING DATE REQUEST");
            socket.send(buffer);
            if (socket.receive(buffer) != 8) {
                System.err.println("CLIENT: GOT WRONG SIZE");
            } else {
                long epoch = buffer.getLong();
                LocalDateTime datetime = LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC);
                System.out.println(String.format("CLIENT: RECEIVED DATE: %s", datetime));
            }

            socket.close();
        }
    }

    public static void main(String[] argv) throws Exception {
        if (argv.length != 2) {
            System.err.println("Usage: reqrep client|server <url>");
        } else {
            final String url = argv[1];
            Demoable demo;
            switch (argv[0].toLowerCase()) {
                case "client":
                    demo = new Client(url);
                    break;
                case "server":
                    demo = new Server(url);
                    break;
                default:
                    throw new IllegalArgumentException("expected 'client' or 'server', got " + argv[0]);
            }
            demo.run();
        }
    }
}
