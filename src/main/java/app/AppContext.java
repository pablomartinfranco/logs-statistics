package app;

import app.api.Context;
import app.api.FileAnalyzer;

import java.nio.file.Path;

public record AppContext(FileAnalyzer runner, Path folder) implements Context {}
