package io.sisu.nng;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageTest {
    @Test
    public void simpleMessageTest() throws NngException {
        Message m = new Message(10);
        m.getBody().putInt(0, 1);
        Assertions.assertEquals(10, m.getBodyLen());
        System.out.println(m.getBody().getInt(0));
    }
}
