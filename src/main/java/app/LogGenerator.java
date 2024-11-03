package app;

import app.api.FileGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class LogGenerator implements FileGenerator {

    private static final Executor EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();
    private static final List<Integer> statusCodes = List.of(200, 200, 200, 200, 200, 200, 404, 404, 500);
    private static final List<String> httpMethods = List.of("GET", "PUT");
    private static final Map<String, String> uriIpMap = Map.of(
            "/ecf8427e/b443dc7f/71f28176/174ef735/1dd4d421", "10.10.6.90",
            "/5d6b9f9e/1c8d2e7f/4abf23de/5f8e6c67/2b4c5d6e", "10.10.1.23",
            "/7f8e9a1b/1c3d4e5f/6f7a8b9c/2a3b4c5d/3e4f5g6h", "10.10.1.12",
            "/6f3b6b9c/8d4c9c7e/72f29a11/8c7f19e8/12e4f3c5", "10.10.1.10",
            "/a1b2c3d4/e5f6a7b8/c9d0e1f2/a3b4c5d6/7e8f9g0h", "10.10.0.50",
            "/9e8d7c6b/5a4b3c2d/1e2f3g4h/5i6j7k8l/9m0n1o2p", "10.10.0.10",
            "/4d3c2b1a/9f8e7d6c/5b4a3c2d/1f0e9d8c/7b6a5c4d", "10.10.1.20",
            "/6c5b4a3d/2e1f9d0c/8b7a6c5d/4e3f2g1h/9j0k1l2m", "10.10.2.34",
            "/2a3b4c5d/6e7f8g9h/0i1j2k3l/4m5n6o7p/8q9r0s1t", "10.10.3.40",
            "/3e4f5g6h/7i8j9k0l/1m2n3o4p/5q6r7s8t/9u0v1w2x", "10.10.3.15"
    );

    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("Usage: LogGenerator <filesCount> <linesCount>");
            System.exit(1);
        }
        int filesCount = 0, linesCount = 0;
        try {
            filesCount = Integer.parseInt(args[0]);
            linesCount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Usage: LogGenerator <filesCount> <linesCount>");
            System.exit(1);
        }
        new LogGenerator().generate(filesCount, linesCount);
    }

    @Override
    public void generate(int filesCount, int linesCount) {
        var start = System.currentTimeMillis();

        CompletableFuture<?>[] futures = new CompletableFuture[filesCount];

        for (int fileIndex = 0; fileIndex < filesCount; fileIndex++) {
            final int currentFileIndex = fileIndex;
            futures[fileIndex] = CompletableFuture.runAsync(() -> {
                var dateTime = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
                var logFile = Path.of(String.format("./data/logfile_%s.txt",
                        dateTime.minusDays(currentFileIndex)
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                try {
                    Files.createFile(logFile);
                } catch (IOException e) {
                    System.err.println("Error creating file: " + e.getMessage());
                    return;
                }
                var builder = new StringBuilder();
                for (int i = 0; i < linesCount; i++) {
                    dateTime = dateTime.plusMinutes(ThreadLocalRandom.current().nextInt(0, 10));
                    var uri = uriIpMap.keySet().stream()
                            .skip(ThreadLocalRandom.current().nextInt(uriIpMap.size()))
                            .findFirst().orElseThrow();
                    var ipAddress = uriIpMap.get(uri);
                    var requestType = httpMethods.get(ThreadLocalRandom.current().nextInt(httpMethods.size()));
                    var statusCode = statusCodes.get(ThreadLocalRandom.current().nextInt(statusCodes.size()));
                    var responseTime = ThreadLocalRandom.current().nextInt(10, 99);
                    var logEntry = new LogEntry(ipAddress, dateTime, requestType, uri, "user", "HTTP/1.0",
                            statusCode, "-", "-", responseTime, "10.10.1.231, 10.10.6.90");
                    builder.append(logEntry).append('\n');
                }
                try (BufferedWriter bw = Files.newBufferedWriter(logFile, StandardOpenOption.APPEND)) {
                    bw.write(builder.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, EXECUTOR_SERVICE);
        }

        CompletableFuture.allOf(futures).join();

        System.out.printf("Created %d files with %,d entries each in %s ms%n", filesCount, linesCount, System.currentTimeMillis() - start);
    }
}
