package app;

import app.api.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class AppContextFactory implements ContextFactory {

    private final Map<String, Supplier<FileAnalyzer>> runners;

    public AppContextFactory(Map<String, Supplier<FileAnalyzer>> runners) {
        this.runners = runners;
    }

    @Override
    public Optional<Context> tryParse(String[] args) {
        var input = parseArguments(args);
        if (input.containsKey(Flags.HELP)) {
            System.out.println("Usage: java -jar log-analyzer.jar --folder=<folder> [--runner=<runner>]");
            System.out.println("Available runners: simple, fibers, parallel");
            return Optional.empty();
        }
        if (!input.containsKey(Flags.FOLDER)) {
            System.out.println("Missing parameter --folder=<folder>");
            return Optional.empty();
        }
        var folder = Paths.get(input.get(Flags.FOLDER));
        if (!Files.exists(folder) || !Files.isDirectory(folder)) {
            System.out.println("Folder " + folder + " does not exist");
            return Optional.empty();
        }
        var runner = Optional.ofNullable(runners.get(input.getOrDefault(Flags.RUNNER, Runners.DEFAULT)))
                .map(Supplier::get).orElseGet(() -> runners.get(Runners.DEFAULT).get());
        return Optional.of(new AppContext(runner, folder));
    }

    private Map<String, String> parseArguments(String[] args) {
        var params = new HashMap<String, String>();
        for (var arg : args) {
            var split = arg.split("=", 2);
            params.put(split[0], (split.length > 1) ? split[1] : "");
        }
        return params;
    }
}
