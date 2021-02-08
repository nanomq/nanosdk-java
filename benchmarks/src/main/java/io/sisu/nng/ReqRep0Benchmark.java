package io.sisu.nng;

import io.sisu.nng.reqrep.Rep0Socket;
import io.sisu.nng.reqrep.Req0Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ReqRep0Benchmark {

    private static final String url = "inproc://reqpre0benchmark";
    private static final int warmup = 10_000;
    private static final int iterations = 500_000;

    public static void main(String[] argv) throws NngException, IOException, InterruptedException {
        final BlockingQueue<Boolean> haltQueue = new ArrayBlockingQueue<>(1);
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(reader);

        final Socket client = new Req0Socket();
        final Socket server = new Rep0Socket();
        long warmupStart, warmupStop, benchStart, benchStop;
        long warmupDelta, benchDelta;

        // client.setReceiveTimeout(30 * 1000);

        server.listen(url);
        client.dial(url);
        System.out.println("Connected client to server.");

        System.out.println("Hit enter to start.");
        bufferedReader.readLine();

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    Message msg = server.receiveMessage();
                    server.sendMessage(msg);
                }
            } catch (Exception e) {
                System.out.println("Server stopping: " + e.getMessage());
            }
            try {
                haltQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        System.out.println("Server ready to receive.");

        System.out.println("Warming up...");
        warmupStart = System.currentTimeMillis();
        for (int i=0; i<warmup; i++) {
            client.sendMessage(new Message());
            client.receiveMessage();
        }
        warmupStop = System.currentTimeMillis();
        warmupDelta = warmupStop - warmupStart;

        System.out.println(
                String.format("Warmup took %d millis (%.4f ops/ms) (%.1f msgs/s)",
                        warmupDelta,
                        warmup / (1.0f * warmupDelta),
                        warmup / (warmupDelta / 1000.0f)));

        System.out.println("Benchmarking...");
        benchStart = System.currentTimeMillis();
        for (int i=0; i<iterations; i++) {
            client.sendMessage(new Message());
            client.receiveMessage();
        }
        benchStop = System.currentTimeMillis();
        benchDelta = benchStop - benchStart;

        System.out.println(
                String.format("Benchmark took %d millis (%f ops/ms) (%.1f msgs/s)",
                        benchDelta, iterations / (1.0f * benchDelta),
                        iterations / (benchDelta / 1000.0f)));
        client.close();
        server.close();

        System.out.println("Finished. Hit Enter to close.");
        bufferedReader.readLine();

        haltQueue.put(true);
        thread.join(500);
        System.out.println("bye!");
    }
}
