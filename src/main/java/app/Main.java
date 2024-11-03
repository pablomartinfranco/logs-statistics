package app;

import app.api.Application;
import app.api.FileAnalyzer;
import app.api.Runners;

import java.util.Map;
import java.util.function.Supplier;

public class Main {

    private static final
    Map<String, Supplier<FileAnalyzer>> runners = Map.of(
            Runners.FIBERS, LogAnalyzerFibers::new,
            Runners.PARALLEL, LogAnalyzerParallel::new,
            Runners.SIMPLE, LogAnalyzerSimple::new,
            Runners.DEFAULT, LogAnalyzerSimple::new
    );

    public static void main(String[] args) {
        var context = new AppContextFactory(runners).tryParse(args);
        if (context.isEmpty()) return;
        Application.run(context.get());
    }
}