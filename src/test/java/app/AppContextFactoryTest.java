package app;

import app.api.FileAnalyzer;
import app.api.Runners;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class AppContextFactoryTest {

    @Test
    void tryParse() {
        // Arrange
        Map<String, Supplier<FileAnalyzer>> runners = Map.of(
                Runners.FIBERS, LogAnalyzerFibers::new,
                Runners.PARALLEL, LogAnalyzerParallel::new,
                Runners.SIMPLE, LogAnalyzerSimple::new,
                Runners.DEFAULT, LogAnalyzerSimple::new
        );

        // Act
        var factory = new AppContextFactory(runners);
        var folder = Path.of("src", "test", "resources");
        var context = new String[] { "--runner=fibers", "--folder=" + folder };
        var result = factory.tryParse(context);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(LogAnalyzerFibers.class, result.get().runner().getClass());
        assertEquals(folder, result.get().folder());
    }
}