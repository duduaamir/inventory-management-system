package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CSVHandler {
    public static void ensureFile(String path) throws IOException {
        Path p = Paths.get(path);
        if (!Files.exists(p)) {
            if (p.getParent() != null) Files.createDirectories(p.getParent());
            Files.createFile(p);
        }
    }

    public static List<String> readAll(String path) throws IOException {
        Path p = Paths.get(path);
        if (!Files.exists(p)) return Collections.emptyList();
        return Files.readAllLines(p, StandardCharsets.UTF_8);
    }

    public static void writeAll(String path, List<String> lines) throws IOException {
        Path p = Paths.get(path);
        ensureFile(path);
        Files.write(p, lines, StandardCharsets.UTF_8);
    }

    public static void appendLine(String path, String line) throws IOException {
        Path p = Paths.get(path);
        ensureFile(path);
        Files.write(p, Collections.singletonList(line), StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND);
    }
}
