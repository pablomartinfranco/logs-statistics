package app.api;

import java.nio.file.Path;

public interface Context {
    FileAnalyzer runner();
    Path folder();
}
