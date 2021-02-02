package io.sisu.nng;

import io.sisu.nng.aio.Context;
import io.sisu.nng.bench.ClientRunnable;
import io.sisu.nng.reqrep.Rep0Socket;
import io.sisu.nng.reqrep.Req0Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


public class ReqRep0ContextBenchmark {

    private static final String url = System.getProperty("nng.url", "inproc://reqrep0benchmark");
    private static final int clientParallelism = Integer.parseInt(
            System.getProperty("nng.parallel.clients", "8"));
    private static final int serverParallelism = Integer.parseInt(
            System.getProperty("nng.parallel.servers", "8"));
    private static final int batchSize = Integer.parseInt(
            System.getProperty("nng.size.batch", "5000"));
    private static final int warmup = Integer.parseInt(
            System.getProperty("nng.size.warmup", "10000"));
    private static final int totalMessages = Integer.parseInt(
            System.getProperty("nng.size.total", "1000000"));
    private static final int numBytes = Integer.parseInt(
            System.getProperty("nng.size.bytes", "4096"));
    private static final int reportIntervalSecs = Integer.parseInt(
            System.getProperty("nng.reporter.interval", "15"));

    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final AtomicLong benchStart = new AtomicLong(0);
    private static final CountDownLatch stopReporter = new CountDownLatch(1);

    private static void report() {
        try {
            BiConsumer<Long, Integer> reportIt = (currentDelta, cnt) ->
                    System.out.println(
                            String.format("%.2f kilo-messages\t\t%.2f secs\t\t%f ops/ms\t\t%.1f msg/s\t\t%.1f kb/s",
                                cnt / 1000.0f,
                                currentDelta / 1000.0f,
                                cnt / (1.0f * currentDelta),
                                cnt / (currentDelta / 1000.0f),
                                (cnt / (currentDelta / 1000.0f)) * (numBytes / 1024.0f)));

            while (!stopReporter.await(reportIntervalSecs, TimeUnit.SECONDS)) {
                reportIt.accept(System.currentTimeMillis() - benchStart.get(), counter.get());
            }

            // One parting shot.
            reportIt.accept(System.currentTimeMillis() - benchStart.get(), counter.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) throws NngException, IOException, InterruptedException, TimeoutException, ExecutionException {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(reader);

        final Socket client = new Req0Socket();
        final Socket server = new Rep0Socket();
        long warmupStart, warmupStop, benchStop;
        long warmupDelta, benchDelta;

        final int iterations = totalMessages > 0
                ? (totalMessages / batchSize )
                + (totalMessages % batchSize > 0 ? 1 : 0)
                : -1;
        final boolean runForever = iterations < 1;
        final ByteBuffer body = ByteBuffer.allocate(numBytes);
        final byte[] marker = "Find me\0".getBytes(StandardCharsets.UTF_8);
        body.put(marker);
        for (int i=body.position(); i<body.capacity(); i++) {
            body.put((byte) 0x00);
        }
        body.flip();

        client.setReceiveTimeout(30 * 1000);

        System.out.println(String.format("Using params: { url: %s, clients: %d, servers: %d, totalMessages : %s, payload size: %d, batchSize: %d }",
                url, clientParallelism, serverParallelism,
                totalMessages > 0 ? String.valueOf(totalMessages) : "infinite",
                numBytes,
                batchSize));

        final List<Context> contexts = new ArrayList<>();
        for (int i=0; i<serverParallelism; i++) {
            Context ctx = new Context(server);
            ctx.setRecvHandler((ctxProxy, msg) -> ctxProxy.send(msg));
            ctx.setSendHandler(ctxProxy -> ctxProxy.receive());
            contexts.add(ctx);
            ctx.receiveMessage();
        }
        System.out.println("Server ready to receive.");

        server.listen(url);
        client.dial(url);
        System.out.println("Connected client to server.");

        System.out.println("Hit enter to start.");
        bufferedReader.readLine();

        System.out.println("Warming up...");
        warmupStart = System.currentTimeMillis();
        for (int i=0; i<warmup; i++) {
            Message msg = new Message();
            msg.append(body.array());
            client.sendMessage(msg);
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

        // prep work and housekeeping
        counter.set(0);
        System.out.println(String.format("Benchmarking with %d simultaneous clients, batchSize %s",
                clientParallelism, batchSize));

        // Reporting thread
        Thread reporter = new Thread(ReqRep0ContextBenchmark::report);
        reporter.setDaemon(true);
        reporter.setName("Reporter");
        reporter.start();

        // Client threads
        ExecutorService executor = Executors.newFixedThreadPool(
                clientParallelism,
                (runnable) -> {
                    Thread thread = new Thread(runnable);
                    thread.setDaemon(true);
                    thread.setName("Client");
                    return thread;
                });
        final AtomicInteger concurrency = new AtomicInteger(0);
        int leftToSubmit = totalMessages;
        List<Future<?>> futures = new ArrayList<>();

        benchStart.set(System.currentTimeMillis());

        // We know we only loop N number of times (unless N = infinite)
        System.out.println(String.format("Starting benchmark with %s iterations",
                iterations > 0 ? String.valueOf(iterations) : "infinite"));
        for (int i=0; runForever || i < iterations; i++) {
            // figure out how much work to send to this new client
            final int batch = runForever || leftToSubmit > batchSize
                    ? batchSize
                    : leftToSubmit;

            ClientRunnable runnable = new ClientRunnable(
                    url, batch, body.array(),
                    bytesSent -> counter.incrementAndGet(),
                    totalSent -> concurrency.decrementAndGet());

            // Try to submit work
            while (true) {
                // only submit work up to the capacity + some fudge factor (8 for now?)
                if (concurrency.get() < (clientParallelism + 8)) {
                    futures.add(executor.submit(runnable));
                    concurrency.incrementAndGet();
                    break;
                } else {
                    // try some cleanup in the meantime before we park
                    futures = futures.stream()
                            .filter(f -> f.isCancelled() || f.isDone())
                            .collect(Collectors.toList());
                    LockSupport.parkNanos(100);
                }
            }
        }

        // wait on any outstanding work
        for (Future f : futures) {
            f.get(5000, TimeUnit.MILLISECONDS);
        };

        // Stop timers and Reporter
        benchStop = System.currentTimeMillis();
        stopReporter.countDown();
        benchDelta = benchStop - benchStart.get();

        // Cleanup remaining threads
        executor.awaitTermination(5, TimeUnit.SECONDS);
        server.close();
        reporter.join();

        System.out.println(
                String.format("Benchmark took %d millis (%f ops/ms)", benchDelta, iterations / (1.0f * benchDelta)));
        System.out.println(String.format("Finished sending %d messages. Hit Enter to close.", counter.get()));
        bufferedReader.readLine();

        System.out.println("bye 👋!");
    }
}
