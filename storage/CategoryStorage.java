package storage;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CategoryStorage {
    private final String PATH_DIR;
    private final String avilableCategoryFile;

    public CategoryStorage() {
        PATH_DIR = "category_data/";
        avilableCategoryFile = "avilableCategoryFile.txt";
    }

    // if not created or dont have initial categorys
    private File tryToCreateFile(String fileNameWithExtention) {
        String strArr[] = { "Food", "Transport", "Education", "Entertainment", "Bills", "Health", "Other" };
        File file = null;
        try {
            file = new File(fileNameWithExtention);
            if (!file.exists())
                file.createNewFile();
            else {
                try (Scanner fileScanner = new Scanner(file)) {
                    if (!fileScanner.hasNextLine()) {
                        try (FileWriter fileWriter = new FileWriter(file)) {
                            for (String string : strArr) {
                                fileWriter.write(string + " \n");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return file;
    }

    public void addCategory(String category) {
        try {
            try (FileWriter fileWriter = new FileWriter(avilableCategoryFile)) {
                fileWriter.write(category + "\n");
                fileWriter.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public List<String> getAllCategory() {
        ArrayList<String> strArr = new ArrayList<String>();
        try {
            try (Scanner fileScanner = new Scanner(new File(avilableCategoryFile))) {
                while (fileScanner.hasNextLine())
                    strArr.add(fileScanner.next());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return new ArrayList<String>(strArr);
    }

    public void getAvilableCategory() {
        try {
            File file = new File(avilableCategoryFile);
            try (Scanner fileScanner = new Scanner(file)) {
                if (!fileScanner.hasNextLine())
                    System.out.println("Records not found");
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
