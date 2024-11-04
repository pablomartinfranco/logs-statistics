package app;

import app.api.Aggregate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    @Test
    void sessionTest() {
        // Arrange
        Aggregate session = new Session();
        // Act
        session.setPageViews(10);
        session.addSessionLength(100);
        session.addSessionLength(200);
        session.addSessionLength(300);
        // Assert
        assertEquals(10, session.getPageViews());
        assertEquals(3, session.getSessions());
        assertEquals(300, session.getLongestSession());
        assertEquals(100, session.getShortestSession());
    }
}