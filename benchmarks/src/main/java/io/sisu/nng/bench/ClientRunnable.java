package io.sisu.nng.bench;

import io.sisu.nng.Message;
import io.sisu.nng.Socket;
import io.sisu.nng.reqrep.Req0Socket;

import java.util.function.Consumer;

public class ClientRunnable implements Runnable {

    private final Consumer<Integer> onRecv;
    private final Consumer<Integer> onFinish;
    private final String url;
    private final byte[] data;
    private final int batchSize;

    public ClientRunnable(String url, int batchSize, byte[] data,
                          Consumer<Integer> onRecv, Consumer<Integer> onFinish) {
        this.url = url;
        this.batchSize = batchSize;
        this.data = data;
        this.onRecv = onRecv;
        this.onFinish = onFinish;
    }

    @Override
    public void run() {
        int cnt = 0;
        try {
            Socket s = new Req0Socket();
            s.dial(url);

            for (; cnt < batchSize; cnt++) {
                Message msg = new Message();
                msg.append(data);
                s.sendMessage(msg);
                s.receiveMessage();
                onRecv.accept(data.length);
            }

            s.close();
        } catch (Exception e) {
            System.err.println("Exception in ClientRunnable: " + e.getMessage());
            e.printStackTrace(System.err);
        } finally {
            onFinish.accept(cnt);
        }
    }
}
