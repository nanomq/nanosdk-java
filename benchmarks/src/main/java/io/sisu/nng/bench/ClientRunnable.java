package io.sisu.nng.bench;

import com.sun.jna.Memory;
import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.aio.Context;
import io.sisu.nng.reqrep.Req0Socket;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class ClientRunnable implements Runnable {

    private final Consumer<Integer> onRecv;
    private final Consumer<Integer> onFinish;
    private final String url;
    private final Memory buffer;
    private final int bufferLen;
    private final int batchSize;

    public ClientRunnable(String url, int batchSize, byte[] data,
                          Consumer<Integer> onRecv, Consumer<Integer> onFinish) {
        this.url = url;
        this.batchSize = batchSize;

        this.buffer = new Memory(data.length);
        this.buffer.write(0, data, 0, data.length);
        this.bufferLen = data.length;

        this.onRecv = onRecv;
        this.onFinish = onFinish;
    }

    private Message newMessage() throws NngException {
        Message msg = new Message();
        msg.append(this.buffer, this.bufferLen);
        return msg;
    }

    @Override
    public void run() {
        final CountDownLatch latch = new CountDownLatch(batchSize);

        try (Socket s = new Req0Socket()) {
            s.dial(url);

            try (Context ctx = new Context(s)) {
                ctx.setSendHandler(contextProxy -> {
                    contextProxy.receive();
                });

                ctx.setRecvHandler(((contextProxy, message) -> {
                    latch.countDown();
                    onRecv.accept(message.getBodyLen());
                    try {
                        ctx.sendMessage(newMessage());
                    } catch (NngException e) {
                        e.printStackTrace();
                    }
                }));

                ctx.sendMessage(newMessage());
                latch.await();
            }

        } catch (Exception e) {
            System.err.println("Exception in ClientRunnable: " + e.getMessage());
            e.printStackTrace(System.err);
        } finally {
            onFinish.accept(batchSize);
        }
    }
}
