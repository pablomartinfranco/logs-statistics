package app.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SessionData {

    private int pageViews = 0;
    private int sessions = 0;
    private final List<Long> sessionLengths = new ArrayList<>();

    public void setPageViews(int pageViews) {
        this.pageViews = pageViews;
    }

    public int getSessions() {
        return sessions;
    }

    public void setSessions(int sessions) {
        this.sessions = sessions;
    }

    public int getPageViews() {
        return pageViews;
    }

    public void addPageView() {
        pageViews++;
    }

    public void addSessionLength(long lengthInMinutes) {
        sessionLengths.add(lengthInMinutes);
        sessions++;
    }

    public long getLongestSession() {
        return sessionLengths.isEmpty() ? 0
            : Collections.max(sessionLengths);
    }

    public long getShortestSession() {
        return sessionLengths.isEmpty() ? 0
            : Collections.min(sessionLengths);
    }
}
