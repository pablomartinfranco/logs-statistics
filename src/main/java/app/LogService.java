package app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class LogService {
    private static final int EXPIRATION_TIME = 10;
    private static final int TOP_USERS_LIMIT = 5;

    public static void processLogFile(Path logFile, Map<String, List<LocalDateTime>> userSessions) {
        try (var reader = Files.newBufferedReader(logFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                var entry = LogEntry.parse(line);
                userSessions.computeIfAbsent(entry.userId(), k -> Collections.synchronizedList(new ArrayList<>()))
                        .add(entry.timestamp().toLocalDateTime());
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + logFile.getFileName());
        }
    }

    public static Map<String, Session> calculateSessions(Map<String, List<LocalDateTime>> userSessions) {
        Map<String, Session> aggregatedData = new HashMap<>();

        for (Map.Entry<String, List<LocalDateTime>> entry : userSessions.entrySet()) {
            List<LocalDateTime> timestamps = entry.getValue();
            timestamps.sort(LocalDateTime::compareTo);
            Session sessionData = new Session();
            sessionData.setPageViews(timestamps.size());

            long sessionLength = 0;
            for (int i = 1; i < timestamps.size(); i++) {
                long deltaMinutes = ChronoUnit.MINUTES.between(timestamps.get(i - 1), timestamps.get(i));
                if (deltaMinutes <= EXPIRATION_TIME) {
                    sessionLength += deltaMinutes;
                } else if (sessionLength > 0) {
                    sessionData.addSessionLength(sessionLength);
                    sessionLength = 0;
                }
            }
            var userId = entry.getKey();
            aggregatedData.put(userId, sessionData);
        }
        return aggregatedData;
    }

    public static void printReport(Map<String, Session> aggregatedData) {
        System.out.println("Total unique users: " + aggregatedData.size());
        System.out.println("Top users:");
        System.out.printf("%-15s %-10s %-10s %-10s %-10s%n", "id", "# pages", "# sess", "longest", "shortest");

        var topUsers = getTopUsersByPageViews(aggregatedData);

        for (Map.Entry<String, Session> entry : topUsers) {
            String userId = entry.getKey();
            Session data = entry.getValue();
            System.out.printf("%-15s %-10d %-10d %-10d %-10d%n", userId, data.getPageViews(), data.getSessions(),
                    data.getLongestSession(), data.getShortestSession());
        }
    }

    private static List<Map.Entry<String, Session>> getTopUsersByPageViews(Map<String, Session> aggregatedData) {
        return aggregatedData.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().getPageViews(), a.getValue().getPageViews()))
                .limit(TOP_USERS_LIMIT).toList();
    }
}
