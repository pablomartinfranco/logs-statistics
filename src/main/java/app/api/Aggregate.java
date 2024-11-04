package app.api;

public interface Aggregate {
    void setPageViews(int pageViews);

    int getSessions();

    int getPageViews();

    void addSessionLength(long lengthInMinutes);

    long getLongestSession();

    long getShortestSession();
}
