package app;

import org.junit.jupiter.api.Test;

import java.time.chrono.ChronoZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LogEntryTest {

    @Test
    void testToString() {
        // Arrange
        var entry = "10.10.6.90 - - 15/Aug/2016:23:59:20 -0500 \"GET /ecf8427e/b443dc7f/71f28176/174ef735/1dd4d421 HTTP/1.0\" 200 - \"-\" \"-\" 7 \"10.10.1.231, 10.10.6.90\" -";
        // Act
        var logEntry = LogEntry.parse(entry);
        // Assert
        assertEquals(entry, logEntry.toString());
    }

    @Test
    void parse() {
        // Arrange
        var entry = "10.10.6.90 - - 15/Aug/2016:23:59:20 -0500 \"GET /ecf8427e/b443dc7f/71f28176/174ef735/1dd4d421 HTTP/1.0\" 200 - \"-\" \"-\" 7 \"10.10.1.231, 10.10.6.90\" -";
        // Act
        var logEntry = LogEntry.parse(entry);
        // Assert
        assertEquals("10.10.6.90", logEntry.ipAddress());
        assertEquals("15/Aug/2016:23:59:20 -0500", logEntry.zonedDateTime());
        assertEquals("GET", logEntry.httpMethod());
        assertEquals("/ecf8427e/b443dc7f/71f28176/174ef735/1dd4d421", logEntry.urlPath());
        assertEquals("71f28176", logEntry.userId());
        assertEquals("HTTP/1.0", logEntry.httpVersion());
        assertEquals(200, logEntry.statusCode());
        assertEquals("-", logEntry.userAgent());
        assertEquals("-", logEntry.referrer());
        assertEquals(7, logEntry.responseTime());
        assertEquals("10.10.1.231, 10.10.6.90", logEntry.forwardedFor());
    }
}