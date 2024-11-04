package app;

import app.api.Aggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Session implements Aggregate {

    private int pageViews = 0;
    private int sessions = 0;
    private final List<Long> sessionLengths = new ArrayList<>();

    @Override
    public void setPageViews(int pageViews) {
        this.pageViews = pageViews;
    }

    @Override
    public int getSessions() {
        return sessions;
    }

    @Override
    public int getPageViews() {
        return pageViews;
    }

    @Override
    public void addSessionLength(long lengthInMinutes) {
        sessionLengths.add(lengthInMinutes);
        sessions++;
    }

    @Override
    public long getLongestSession() {
        return sessionLengths.isEmpty() ? 0
            : Collections.max(sessionLengths);
    }

    @Override
    public long getShortestSession() {
        return sessionLengths.isEmpty() ? 0
            : Collections.min(sessionLengths);
    }
}
