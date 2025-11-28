package com.simple.expense;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Small file-based CSV storage. Thread-safety is not a concern for this simple CLI.
 * Data file path is provided at construction.
 */
public class Storage {
    private final Path file;

    /**
     * @param filePath path to CSV file (will be created if missing)
     */
    public Storage(Path filePath) {
        this.file = filePath;
    }

    /**
     * Load all expenses from the CSV file.
     * If file doesn't exist, returns empty list (and creates parent directories).
     */
    public List<Expense> loadAll() {
        List<Expense> list = new ArrayList<>();
        try {
            if (!Files.exists(file)) {
                Path parent = file.getParent();
                if (parent != null) Files.createDirectories(parent);
                Files.createFile(file);
                return list;
            }
            try (BufferedReader r = Files.newBufferedReader(file)) {
                String line;
                while ((line = r.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    list.add(Expense.fromCsvRow(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load data: " + e.getMessage());
        }
        return list;
    }

    /**
     * Write all expenses to a temporary file and atomically replace the original file.
     */
    public void saveAll(List<Expense> expenses) {
        try {
            Path tmp = Paths.get(file.toString() + ".tmp");
            try (BufferedWriter w = Files.newBufferedWriter(tmp, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Expense e : expenses) {
                    w.write(e.toCsvRow());
                    w.newLine();
                }
            }
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }
}
