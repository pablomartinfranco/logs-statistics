package app;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LogServiceTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void processLogFile() throws Exception {
        // Arrange
        Map<String, List<LocalDateTime>> usersData = new HashMap<>();
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path fakeFilePath = fs.getPath("/data/logs/fake-log.txt");
        Files.createDirectories(fakeFilePath.getParent());
        String fakeLogData = "10.10.6.90 - - 15/Aug/2016:23:59:20 -0500 \"GET /ecf8427e/b443dc7f/71f28176/174ef735/1dd4d421 HTTP/1.0\" 200 - \"-\" \"-\" 7 \"10.10.1.231, 10.10.6.90\" -";
        Files.write(fakeFilePath, fakeLogData.getBytes(StandardCharsets.UTF_8));
        Files.lines(fakeFilePath).forEach(System.out::println);

        // Act
        LogService.processLogFile(fakeFilePath, usersData);

        // Assert
        assertEquals(1, usersData.size());
        assertEquals(1, usersData.get("71f28176").size());
        assertEquals("2016-08-15T23:59:20", usersData.get("71f28176").getFirst().toString());
    }

    @Test
    void calculateSessions() {
        // Arrange
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        var userid = "71f28176";
        Map<String, List<LocalDateTime>> userSessions = new HashMap<>();
        userSessions.put(userid, new ArrayList<>(List.of(
                LocalDateTime.parse("2024-10-01 10:15", formatter), // 0  1
                LocalDateTime.parse("2024-10-01 10:27", formatter), // 12 2
                LocalDateTime.parse("2024-10-01 10:35", formatter), // 8  2
                LocalDateTime.parse("2024-10-01 10:48", formatter), // 13 3
                LocalDateTime.parse("2024-10-01 10:55", formatter), // 7  3
                LocalDateTime.parse("2024-10-01 11:02", formatter)  // 7  3
        )));

        // Act
        var aggregated = LogService.calculateSessions(userSessions);
        var sessionData = aggregated.get(userid);
        var pageViews = sessionData.getPageViews();
        var sessions = sessionData.getSessions();
        var longestSession = sessionData.getLongestSession();
        var shortestSession = sessionData.getShortestSession();

        // Assert
        assertEquals(6, pageViews);
        assertEquals(2, sessions);
        assertEquals(18, longestSession);
        assertEquals(10, shortestSession);
        assertEquals(1, aggregated.size());
    }

    @Test
    void printReport() {
    }
}