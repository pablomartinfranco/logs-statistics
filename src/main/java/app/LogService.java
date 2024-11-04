package app;

import app.api.SessionData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogService {
    private static final int EXPIRATION_TIME = 10;
    private static final int TOP_USERS_LIMIT = 5;

    public static void processLogFile(Path logFile, Map<String, List<LocalDateTime>> userSessions) {
        try (var reader = Files.newBufferedReader(logFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                var entry = LogEntry.parse(line);
                userSessions.computeIfAbsent(entry.userId(), k -> new ArrayList<>())
                        .add(entry.timestamp().toLocalDateTime());
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + logFile.getFileName());
        }
//        return userSessions.size();
    }

    public static Map<String, SessionData> calculateSessions(Map<String, List<LocalDateTime>> userSessions) {
        Map<String, SessionData> aggregatedData = new HashMap<>();

        for (Map.Entry<String, List<LocalDateTime>> entry : userSessions.entrySet()) {
            List<LocalDateTime> timestamps = entry.getValue();
            timestamps.sort(LocalDateTime::compareTo);
            SessionData sessionData = new SessionData();
            sessionData.setPageViews(timestamps.size());

            long sessionLength = 0;
            for (int i = 1; i < timestamps.size(); i++) {
                long deltaMinutes = ChronoUnit.MINUTES.between(timestamps.get(i - 1), timestamps.get(i));
                if (deltaMinutes <= EXPIRATION_TIME) {
                    sessionLength += deltaMinutes;
                } else {
                    sessionData.addSessionLength(sessionLength + EXPIRATION_TIME);
                    sessionLength = 0;
                }
            }
            var userId = entry.getKey();
            aggregatedData.put(userId, sessionData);
        }
        return aggregatedData;
    }

    public static void printReport(Map<String, SessionData> aggregatedData) {
        System.out.println("Total unique users: " + aggregatedData.size());
        System.out.println("Top users:");
        System.out.printf("%-15s %-10s %-10s %-10s %-10s%n", "id", "# pages", "# sess", "longest", "shortest");

        var topUsers = getTopUsersByPageViews(aggregatedData);

        for (Map.Entry<String, SessionData> entry : topUsers) {
            String userId = entry.getKey();
            SessionData data = entry.getValue();
            System.out.printf("%-15s %-10d %-10d %-10d %-10d%n", userId, data.getPageViews(), data.getSessions(),
                    data.getLongestSession(), data.getShortestSession());
        }
    }

    private static List<Map.Entry<String, SessionData>> getTopUsersByPageViews(Map<String, SessionData> aggregatedData) {
        return aggregatedData.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().getPageViews(), a.getValue().getPageViews()))
                .limit(TOP_USERS_LIMIT).toList();
    }
}
