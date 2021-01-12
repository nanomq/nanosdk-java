package io.sisu.nng.pair;

import com.sun.jna.Native;
import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PairTest {

    private byte[] dump(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return bytes;
    }

    @Test
    public void simplePair0Test() {
        final String url = String.format("inproc://%s",
                new Throwable().getStackTrace()[0].getMethodName());

        try {
            testPairSockets(url, new Pair0Socket(), new Pair0Socket());
        } catch (NngException e) {
            Assertions.fail("failed test with Pair0 sockets", e);
        }

        try {
            testPairSockets(url, new Pair1Socket(), new Pair1Socket());
        } catch (NngException e) {
            Assertions.fail("failed test with Pair1 sockets", e);
        }
    }

    public void testPairSockets(String url, Socket pairA, Socket pairB) throws NngException {
        pairA.listen(url);
        pairB.dial(url);

        final String hello = "Hello from B!";
        Message msgB = new Message();
        msgB.append(hello.getBytes(StandardCharsets.UTF_8));
        ByteBuffer body = msgB.getBody();
        Assertions.assertArrayEquals(hello.getBytes(StandardCharsets.UTF_8), dump(body));
        pairB.sendMessage(msgB);

        Message msgA = pairA.receiveMessage();
        Assertions.assertEquals(hello,
                StandardCharsets.UTF_8.decode(msgA.getBody()).toString());

        pairB.sendMessage(ByteBuffer.wrap("Hello again!".getBytes(StandardCharsets.UTF_8)));
        Assertions.assertEquals("Hello again!",
                StandardCharsets.UTF_8.decode(pairA.receiveMessage().getBody()).toString());

        pairA.close();
        pairB.close();
    }

    /**
     * The simplest send/recv test since the Pair0 protocol doesn't use any header so it's very
     * simple and lightweight.
     */
    @Test
    public void rawPair0Test() {
        final String url = String.format("inproc://%s",
                new Throwable().getStackTrace()[0].getMethodName());

        try {
            Socket pairA = new Pair0Socket();
            Socket pairB = new Pair0Socket();

            pairA.listen(url);
            pairB.dial(url);

            final String hello = "Hello from B!";
            ByteBuffer data = ByteBuffer.wrap(Native.toByteArray(hello, StandardCharsets.UTF_8));
            pairB.send(data);

            // XXX: C "strings" have null terminator bonus byte
            ByteBuffer buffer = ByteBuffer.allocateDirect(data.limit()+1);
            long n = pairA.receive(buffer);

            Assertions.assertEquals(hello.length() + 1, n);
            byte[] response = new byte[buffer.limit()];
            buffer.get(response);
            Assertions.assertEquals(hello, Native.toString(response, StandardCharsets.UTF_8));

        } catch (NngException e) {
            Assertions.fail(e);
        }
    }
}
