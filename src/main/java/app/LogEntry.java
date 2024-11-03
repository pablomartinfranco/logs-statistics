package app;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public record LogEntry(String ipAddress, ZonedDateTime timestamp, String httpMethod,
        String urlPath, String userId, String httpVersion, int statusCode,
        String userAgent, String referrer, int responseTime, String forwardedFor)
{
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

    //10.10.6.90 - - 15/Aug/2016:23:59:20 -0500 "GET /ecf8427e/b443dc7f/71f28176/174ef735/1dd4d421 HTTP/1.0" 200 - "-" "-" 7 "10.10.1.231, 10.10.6.90" -
    @Override
    public String toString() {
        return String.format("%s - - %s \"%s %s %s\" %d - \"%s\" \"%s\" %d \"%s\" -",
                ipAddress, FORMATTER.format(timestamp), httpMethod, urlPath, httpVersion,
                statusCode, userAgent, referrer, responseTime, forwardedFor);
    }

    public String zonedDateTime() {
        return FORMATTER.format(timestamp);
    }

    public static LogEntry parse(String logLine) {
        var parts = logLine.split(" ");
        var ipAddress = parts[0];
        var timestampString = parts[3] + " " + parts[4];
        var timestamp = ZonedDateTime.parse(timestampString, FORMATTER);

        var httpMethod = parts[5].substring(1);
        var urlPath = parts[6];
        var userId = urlPath.split("/")[3];
        var httpVersion = parts[7].substring(0, parts[7].length() - 1);
        var statusCode = Integer.parseInt(parts[8]);

        var userAgent = parts[10].replace("\"", "");
        var referrer = parts[11].replace("\"", "");
        var responseTime = Integer.parseInt(parts[12]);
        var forwardedFor = parts[13].replace("\"", "") + " " + parts[14].replace("\"", "");

        return new LogEntry(ipAddress, timestamp, httpMethod, urlPath, userId, httpVersion,
                statusCode, userAgent, referrer, responseTime, forwardedFor);
    }
}

