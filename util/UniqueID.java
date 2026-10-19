package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public final class UniqueID {
    private static final String LAST_ID_FILE = "ExpenseManager/data/last_id.txt";

    public static synchronized int getID() {
        try {
            File dir = new File("ExpenseManager/data");
            if (!dir.exists())
                dir.mkdirs();
            File f = new File(LAST_ID_FILE);
            int last = 0;
            if (f.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String s = br.readLine();
                    if (s != null && !s.isBlank())
                        last = Integer.parseInt(s.trim());
                } catch (Exception e) {
                    last = 0;
                }
            }
            int next = last + 1;
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                bw.write(Integer.toString(next));
            }
            return next;
        } catch (Exception e) {
            // fallback
            return (int) (System.currentTimeMillis() % 1000000);
        }
    }
}