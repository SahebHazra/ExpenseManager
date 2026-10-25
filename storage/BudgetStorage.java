package storage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;

/** Persistent category-to-budget mapping. */
public class BudgetStorage {
    private final File directory = new File("data");
    private final File file = new File(directory, "Budgets.txt");

    public BudgetStorage() {
        try {
            if (!directory.exists() && !directory.mkdirs()) throw new IOException("Cannot create data directory");
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) { throw new IllegalStateException("Cannot initialize budget storage", e); }
    }

    public synchronized Map<String, Double> loadAll() {
        Map<String, Double> budgets = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);
                if (parts.length != 2) continue;
                try {
                    String category = new String(Base64.getDecoder().decode(parts[0]), StandardCharsets.UTF_8);
                    double amount = Double.parseDouble(parts[1]);
                    if (Double.isFinite(amount) && amount > 0) budgets.put(category, amount);
                } catch (IllegalArgumentException ignored) { }
            }
        } catch (IOException e) { System.out.println("Could not load budgets: " + e.getMessage()); }
        return budgets;
    }

    public synchronized boolean saveAll(Map<String, Double> budgets) {
        File temp = new File(file.getPath() + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            for (Map.Entry<String, Double> entry : budgets.entrySet()) {
                String encoded = Base64.getEncoder().encodeToString(entry.getKey().getBytes(StandardCharsets.UTF_8));
                writer.write(encoded + "|" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            temp.delete();
            System.out.println("Could not save budgets: " + e.getMessage());
            return false;
        }
        if (file.exists() && !file.delete()) { temp.delete(); return false; }
        if (!temp.renameTo(file)) { temp.delete(); return false; }
        return true;
    }
}