package io.sisu.nng.demo.async;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.reqrep.Req0Socket;

/**
 * Java implementation of the NNG async demo client program.
 */
public class Client {
    private final String url;
    private final int millis;

    public Client(String url, int millis) {
        this.url = url;
        this.millis = millis;
    }

    public void run() throws NngException {
        Message msg = new Message();
        msg.appendU32(millis);

        try (Socket sock = new Req0Socket()) {
            sock.dial(url);

            long start = System.currentTimeMillis();
            sock.sendMessage(msg);
            sock.receiveMessage();
            long end = System.currentTimeMillis();

            System.out.println(String.format("Request took %d milliseconds.", end - start));
        }
    }

    public static void main(String[] argv) {
        if (argv.length != 2) {
            System.err.println(String.format("Usage: client <url> <msecs>"));
            System.exit(1);
        }

        try {
            Client client = new Client(argv[0], Integer.parseUnsignedInt(argv[1]));
            client.run();
        } catch (NngException nngErr) {
            System.err.println(nngErr);
        }
    }
}
