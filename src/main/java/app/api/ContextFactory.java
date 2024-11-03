package app.api;

import java.util.Optional;

public interface ContextFactory {
    Optional<Context> tryParse(String[] args);
}
