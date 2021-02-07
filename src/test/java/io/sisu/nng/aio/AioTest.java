package io.sisu.nng.aio;

import com.sun.jna.Native;
import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AioTest {

    @Test
    @Timeout(1000)
    public void aioWithoutCallbackTest() throws NngException {
        Aio aio = new Aio();

        Thread thread = new Thread(() -> {
            Assertions.assertTrue(aio.begin());
            aio.finish(0);
        });
        thread.start();

        aio.waitForFinish();
    }

    @Test
    @Timeout(2000)
    public void aioTimeoutTest() throws NngException, ExecutionException, InterruptedException {
        Aio aio = new Aio();
        aio.setTimeoutMillis(500);

        final CompletableFuture<Boolean> future = new CompletableFuture<>();
        Thread thread = new Thread(() -> {
            Assertions.assertTrue(aio.begin());
            future.complete(true);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // nop
            }
            aio.finish(0);
        });
        thread.start();

        future.get();
        aio.waitForFinish();
        aio.assertSuccessful();
    }

    @Test
    public void aioCallbackTest() throws NngException {
        Map<String, String> args = new HashMap<>();
        args.put("name", "nng");
        args.put("version", "1.3");

        AioCallback cb = new AioCallback<>((aio, map) -> {
            System.out.println("Callback fired.");
            Assertions.assertEquals("nng", map.getOrDefault("name", ""));
            Assertions.assertEquals("1.3", map.getOrDefault("version", ""));

            Message msg = aio.getMessage();
            Assertions.assertNotNull(msg);

            Assertions.assertEquals("aio message",
                    Native.toString(msg.getBodyCopy().array(), StandardCharsets.UTF_8));
        }, args);

        Aio aio = new Aio(cb);
        aio.setTimeoutMillis(500);
        Assertions.assertTrue(aio.begin());

        Message msg = new Message();
        msg.append("aio message");
        aio.setMessage(msg);

        aio.setOutput(0, ByteBuffer.allocateDirect(10).put("Hey man".getBytes(StandardCharsets.UTF_8)));
        aio.finish(0);

        aio.assertSuccessful();
        Assertions.assertEquals("Hey man", aio.getOutputAsString(0));
    }
}
