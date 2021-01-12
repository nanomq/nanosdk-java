package io.sisu.nng.pubsub;

import com.sun.jna.Native;
import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class PubSub0Test {
    @Test
    public void simplePubSubTest() throws NngException {
        final String url = String.format("inproc://%s",
                new Throwable().getStackTrace()[0].getMethodName());

        Pub0Socket publisher = new Pub0Socket();

        Sub0Socket sub1 = new Sub0Socket();
        sub1.setReceiveTimeout(100);
        Sub0Socket sub2 = new Sub0Socket();
        sub2.setReceiveTimeout(100);

        sub1.subscribe("topic1");
        sub2.subscribe("topic1");

        sub1.subscribe("topic2");

        publisher.listen(url);
        sub1.dial(url);
        sub2.dial(url);

        Message topic1Message = new Message();
        topic1Message.append("topic1");
        topic1Message.append("yankees win!");
        publisher.sendMessage(topic1Message);

        Message sub1Message = sub1.receiveMessage();
        Assertions.assertArrayEquals(
                Native.toByteArray("topic1\0yankees win!", StandardCharsets.UTF_8),
                sub1Message.getBodyOnHeap().array());

        Message sub2Message = sub2.receiveMessage();
        Assertions.assertArrayEquals(
                Native.toByteArray("topic1\0yankees win!", StandardCharsets.UTF_8),
                sub2Message.getBodyOnHeap().array());

        Message topic2Message = new Message();
        topic2Message.append("topic2");
        topic2Message.append("dinner is served");
        publisher.sendMessage(topic2Message);

        sub1Message = sub1.receiveMessage();
        Assertions.assertArrayEquals(
                Native.toByteArray("topic2\0dinner is served", StandardCharsets.UTF_8),
                sub1Message.getBodyOnHeap().array());

        Assertions.assertTimeout(Duration.ofMillis(200), () -> {
            Assertions.assertThrows(NngException.class, sub1::receiveMessage);
        });

        sub2.close();
        sub1.close();
        publisher.close();
    }
}
