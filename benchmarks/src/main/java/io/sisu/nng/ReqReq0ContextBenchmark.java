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
import java.util.concurrent.atomic.AtomicLong;


public class ReqReq0ContextBenchmark {

    private static final String url = "inproc://reqrep0benchmark";
    private static final int parallelism = Integer.parseInt(
            System.getProperty("NNG_PARALLELISM", "24"));
    private static final int warmup = 50_000;
    private static final int iterations = Integer.parseInt(
            System.getProperty("NNG_ITERATIONS", "10000000"));

    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final AtomicLong benchStart = new AtomicLong(0);

    public static void main(String argv[]) throws NngException, IOException, InterruptedException {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(reader);

        final Socket client = new Req0Socket();
        final Socket server = new Rep0Socket();
        long warmupStart, warmupStop, benchStop;
        long warmupDelta, benchDelta;

        client.setReceiveTimeout(30 * 1000);

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
                counter.incrementAndGet();
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


        counter.set(0);
        final int clientCnt = parallelism / 2;
        final int batchSize = iterations > 0 ? iterations / clientCnt : -1;
        System.out.println(String.format("Benchmarking with %d clients, batchSize %d",
                clientCnt, batchSize));

        // Reporting thread
        Thread reporter = new Thread(() -> {
            try {
                long currentDelta;
                int cnt;
                while (true) {
                    Thread.sleep(15_000);
                    currentDelta = System.currentTimeMillis() - benchStart.get();
                    cnt = counter.get();
                    System.out.println(
                            String.format("%.2f kilo-messages\t%.2f secs\t%f ops/ms\t%.1f\tmsg/s",
                                    cnt / 1000.0f,
                                    currentDelta / 1000.0f,
                                    cnt / (1.0f * currentDelta),
                                    cnt / (currentDelta / 1000.0f)));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        reporter.setDaemon(true);
        reporter.start();

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
            thread.setDaemon(true);
            thread.setName("client-" + i);
            thread.start();
        }

        benchStart.set(System.currentTimeMillis());
        latch.await();
        benchStop = System.currentTimeMillis();
        benchDelta = benchStop - benchStart.get();

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
