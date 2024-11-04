package app;

import app.api.FileAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LogAnalyzerFibers implements FileAnalyzer {
    private static final Executor EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void analyze(Path path) {
        Map<String, List<LocalDateTime>> usersData = new ConcurrentHashMap<>();
        var futures = new ArrayList<CompletableFuture<?>>();

        try (var directoryStream = Files.newDirectoryStream(path, "*.txt")) {
            for (Path logFile : directoryStream) {
                futures.add(CompletableFuture.runAsync(
                    () -> LogService.processLogFile(logFile, usersData),
                    EXECUTOR_SERVICE
                ));
            }
        } catch (IOException e) {
            System.err.println("Error reading log files: " + e.getMessage());
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        var aggregatedData = LogService.calculateSessions(usersData);

        LogService.printReport(aggregatedData);
    }
}
