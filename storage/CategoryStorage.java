package storage;

import java.io.*;
import java.util.*;

/** Persistent storage for user-defined expense categories. */
public class CategoryStorage {
    private final File directory = new File("data");
    private final File categoryFile = new File(directory, "Categories.txt");
    private static final List<String> DEFAULTS = Arrays.asList(
            "Food", "Transport", "Education", "Entertainment", "Shopping",
            "Bills", "Health", "Technology", "Other");

    public CategoryStorage() {
        try {
            if (!directory.exists() && !directory.mkdirs())
                throw new IOException("Could not create data directory");
            if (!categoryFile.exists()) categoryFile.createNewFile();
            if (getAllCategory().isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(categoryFile))) {
                    for (String category : DEFAULTS) {
                        writer.write(category);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize category storage", e);
        }
    }

    public synchronized List<String> getAllCategory() {
        List<String> categories = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(categoryFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.strip();
                if (!line.isEmpty() && !categories.contains(line)) categories.add(line);
            }
        } catch (IOException e) {
            System.out.println("Could not read categories: " + e.getMessage());
        }
        return categories;
    }

    public synchronized boolean addCategory(String category) {
        if (category == null || category.isBlank()) return false;
        String normalized = category.strip();
        for (String existing : getAllCategory()) {
            if (existing.equalsIgnoreCase(normalized)) return true;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(categoryFile, true))) {
            writer.write(normalized);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Could not save category: " + e.getMessage());
            return false;
        }
    }

    public void getAvilableCategory() {
        for (String category : getAllCategory()) System.out.println(category);
    }
}
