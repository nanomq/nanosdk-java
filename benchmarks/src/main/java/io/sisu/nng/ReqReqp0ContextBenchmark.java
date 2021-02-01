package io.sisu.nng;

import io.sisu.nng.aio.Context;
import io.sisu.nng.reqrep.Rep0Socket;
import io.sisu.nng.reqrep.Req0Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ReqReqp0ContextBenchmark {

    private static final String url = "inproc://reqrep0benchmark";
    private static final int parallelism = 24;
    private static final int warmup = 10_000;
    private static final int iterations = 100_00_000;

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String argv[]) throws NngException, IOException, InterruptedException {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(reader);

        final Socket client = new Req0Socket();
        final Socket server = new Rep0Socket();
        long warmupStart, warmupStop, benchStart, benchStop;
        long warmupDelta, benchDelta;

        //client.setReceiveTimeout(5000);

        server.listen(url);
        client.dial(url);
        System.out.println("Connected client to server.");

        System.out.println("Hit enter to start.");
        bufferedReader.readLine();

        final List<Context> contexts = new ArrayList<>();
        for (int i=0; i<parallelism; i++) {
            Context ctx = new Context(server);
            ctx.setRecvHandler((ctxProxy, msg) -> {
                ctxProxy.send(msg);
            });
            ctx.setSendHandler(ctxProxy -> {
                final int cnt = counter.incrementAndGet();
                if (cnt % 5000 == 0) {
                    System.out.println("server handled " + cnt);
                }
                ctx.receiveMessage();
            });
            contexts.add(ctx);
            ctx.receiveMessage();
        }
        System.out.println("Server ready to receive.");

        System.out.println("Warming up...");
        warmupStart = System.currentTimeMillis();
        for (int i=0; i<warmup; i++) {
            client.sendMessage(new Message());
            client.receiveMessage();
        }
        warmupStop = System.currentTimeMillis();
        warmupDelta = warmupStop - warmupStart;
        client.close();

        System.out.println(
                String.format("Warmup took %d millis (%.4f ops/ms) (%.1f msgs/s)",
                        warmupDelta,
                        warmup / (1.0f * warmupDelta),
                        warmup / (warmupDelta / 1000.0f)));


        final int clientCnt = parallelism / 2;
        final int batchSize = iterations / clientCnt;
        System.out.println(String.format("Benchmarking with %d clients, batchSize %d",
                clientCnt, batchSize));

        final CountDownLatch latch = new CountDownLatch(clientCnt);
        for (int i=0; i<clientCnt; i++) {
            Thread thread = new Thread(() -> {
                try {
                    Socket s = new Req0Socket();
                    s.dial(url);
                    for (int n = 0; n < batchSize; n++) {
                        s.sendMessage(new Message());
                        s.receiveMessage();
                    }
                    s.close();
                } catch (Exception e) {
                    System.err.println("Exception in client thread: " + e);
                    e.printStackTrace(System.err);
                } finally {
                    latch.countDown();
                }
            });
            thread.start();
        }

        benchStart = System.currentTimeMillis();
        latch.await();
        benchStop = System.currentTimeMillis();
        benchDelta = benchStop - benchStart;

        System.out.println(
                String.format("Benchmark took %d millis (%f ops/ms) (%.1f msgs/s)",
                        benchDelta, iterations / (1.0f * benchDelta),
                        iterations / (benchDelta / 1000.0f)));

        contexts.forEach(ctx -> {
            try {
                ctx.close();
            } catch (NngException e) {
                e.printStackTrace();
            }
        });
        server.close();

        System.out.println("Finished. Hit Enter to close.");
        bufferedReader.readLine();

        System.out.println("bye!");
    }
}
