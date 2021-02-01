package io.sisu.nng;

import com.sun.jna.Native;
import io.sisu.nng.aio.Context;
import io.sisu.nng.reqrep.Rep0Socket;
import io.sisu.nng.reqrep.Req0Socket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ContextTest {

    @Test
    @Timeout(250)
    public void simpleHandlerTest() throws NngException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        final String url = String.format("inproc://%s",
                new Throwable().getStackTrace()[0].getMethodName());

        Socket req = new Req0Socket();
        Socket rep = new Rep0Socket();

        Context reqCtx = new Context(req);
        Context repCtx = new Context(rep);

        repCtx.setRecvHandler((contextProxy, message) -> {
            Assertions.assertEquals("hello",
                    Native.toString(message.getBodyOnHeap().array(), StandardCharsets.UTF_8));

            contextProxy.put("msg", message);
            contextProxy.sleep(100);
        });
        repCtx.setWakeHandler(contextProxy -> {
            Message msg = (Message) contextProxy.get("msg");
            contextProxy.send(msg);
        });
        repCtx.setSendHandler(contextProxy -> latch.countDown());

        rep.listen(url);
        req.dial(url);

        CompletableFuture<Message> future = repCtx.receiveMessage();

        Message msg = new Message();
        msg.append("hello");
        reqCtx.sendMessageSync(msg);

        Message reply = reqCtx.receiveMessageSync();
        Assertions.assertEquals("hello",
                Native.toString(reply.getBodyOnHeap().array(), StandardCharsets.UTF_8));

        Assertions.assertTrue(latch.await(100, TimeUnit.MILLISECONDS));
        Assertions.assertTrue(future.isDone());

        repCtx.close();
        reqCtx.close();

        req.close();
        rep.close();
    }

    @Test
    @Timeout(250)
    public void simpleContextSendReceiveTest() throws NngException {
        final String url = String.format("inproc://%s",
                new Throwable().getStackTrace()[0].getMethodName());

        Socket req = new Req0Socket();
        Socket rep = new Rep0Socket();

        rep.listen(url);
        req.dial(url);

        Context reqCtx = new Context(req);
        Context repCtx = new Context(rep);

        Message msg = new Message();
        msg.append("hello");
        reqCtx.sendMessageSync(msg);

        Message reply = repCtx.receiveMessageSync();
        Assertions.assertEquals("hello",
                Native.toString(reply.getBodyOnHeap().array(), StandardCharsets.UTF_8));

        req.close();
        rep.close();
    }

    @Test
    @Timeout(1000)
    public void simpleTimeoutTest() throws NngException {
        final String url = String.format("inproc://%s",
                new Throwable().getStackTrace()[0].getMethodName());

        Socket rep = new Rep0Socket();
        rep.listen(url);

        Context repCtx = new Context(rep);
        repCtx.setReceiveTimeout(50);
        Assertions.assertThrows(NngException.class, repCtx::receiveMessageSync);

        rep.close();
    }
}
