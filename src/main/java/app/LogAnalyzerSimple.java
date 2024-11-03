package app;

import app.api.FileAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

public class LogAnalyzerSimple implements FileAnalyzer {

    @Override
    public void analyze(Path path) {
        Map<String, List<LocalDateTime>> usersData = new HashMap<>();

        try (var directoryStream = Files.newDirectoryStream(path, "*.txt")) {
            for (Path logFile : directoryStream) {
                LogService.processLogFile(logFile, usersData);
            }
        } catch (IOException e) {
            System.err.println("Error reading log files: " + e.getMessage());
        }

        var aggregatedData = LogService.calculateSessions(usersData);

        LogService.printReport(aggregatedData);
    }
}
