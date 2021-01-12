package io.sisu.nng;

import io.sisu.nng.pair.Pair0Socket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class SocketTest {

    @Test
    public void timeoutTest() throws NngException {
        final Pair0Socket socket = new Pair0Socket();
        socket.setSendTimeout(100);
        socket.setReceiveTimeout(100);

        Assertions.assertTimeout(Duration.ofMillis(200), () -> {
            Assertions.assertThrows(NngException.class, () -> {
                Message msg = new Message();
                msg.append("test");
                socket.sendMessage(msg);
            });
        });

        Assertions.assertTimeout(Duration.ofMillis(200), () -> {
            Assertions.assertThrows(NngException.class, socket::receiveMessage);
        });
    }
}
