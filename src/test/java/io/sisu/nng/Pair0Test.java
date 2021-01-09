package io.sisu.nng;

import io.sisu.nng.reqrep0.Pair0Socket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public class Pair0Test {

    private byte[] dump(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return bytes;
    }

    @Test
    public void SimplePair0Test() {
        final String url = String.format("inproc://%s",
                new Throwable().getStackTrace()[0].getMethodName());

        try {
            Socket pairA = new Pair0Socket();
            Socket pairB = new Pair0Socket();

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

        } catch (NngException e) {
            Assertions.fail(e);
        }
    }
}
