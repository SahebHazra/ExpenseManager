package storage;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

enum OperationType {
    GET_RECORDS, DECRIMENT, INCRIMENT;
}

// TO-DO
// Have to impliment remove section

public class FileStorage {
    private final String incomeCountFile;
    private final String expenseCountFile;
    private final String incomeRecordsFile;
    private final String expenseRecordsFile;
    private final String PATH_DIR;

    public FileStorage() {
        PATH_DIR = "data/";
        new File(PATH_DIR).mkdir();
        incomeCountFile = PATH_DIR + "IncomeCount.txt";
        expenseCountFile = PATH_DIR + "ExpenseCount.txt";
        incomeRecordsFile = PATH_DIR + "IncomeRecords.txt";
        expenseRecordsFile = PATH_DIR + "ExpenseRecords.txt";
    }

    private File tryToCreateFile(String fileNameWithExtention) throws Exception {
        File file = new File(fileNameWithExtention);
        if (!file.exists())
            file.createNewFile();
        return file;
    }

    // incriments only count
    private void incrimentIncomeCount() {
        ManageExpenseIncomeCount(incomeCountFile, OperationType.INCRIMENT);
    }

    private void incrimentExpenseCount() {
        ManageExpenseIncomeCount(expenseCountFile, OperationType.INCRIMENT);
    }

    // decriments only count
    private void decrimentIncomeCount() {
        ManageExpenseIncomeCount(expenseCountFile, OperationType.DECRIMENT);
    }

    private void decrimentExpenseCount() {
        ManageExpenseIncomeCount(expenseCountFile, OperationType.DECRIMENT);
    }

    // return count if operation Success | return -1 if failed
    public int getIncomeCount() {
        return ManageExpenseIncomeCount(incomeCountFile, OperationType.GET_RECORDS);
    }

    public int getExpenseCount() {
        return ManageExpenseIncomeCount(expenseCountFile, OperationType.GET_RECORDS);
    }

    private int ManageExpenseIncomeCount(String fileNameWithExtention, OperationType oprn) {
        int count = 0;
        try {
            File file = tryToCreateFile(fileNameWithExtention);
            try (Scanner fileScanner = new Scanner(file)) {
                if (fileScanner.hasNextInt())
                    count = fileScanner.nextInt();
            }

            if (oprn == OperationType.INCRIMENT || oprn == OperationType.DECRIMENT) {
                try (FileWriter fileWriter = new FileWriter(file, false)) {
                    if (oprn == OperationType.INCRIMENT)
                        count++;
                    else if (oprn == OperationType.DECRIMENT) {
                        if (count > 0)
                            count--;
                    }
                    fileWriter.write(String.valueOf(count));
                }
            }
        } catch (Exception e) {
            System.out.println("\terr: " + e.getMessage());
            count = -1;
        }
        return count;
    }

    // Actual stores the data
    public boolean addIncomeRecord(int ID, double amount, String dateStr, String source, String note) {
        if (!addRecords(ID, amount, dateStr, source, note, incomeRecordsFile))
            return false;
        incrimentIncomeCount();
        return true;
    }

    public boolean addExpenseRecord(int ID, double amount, String dateStr, String Category, String note) {
        if (!addRecords(ID, amount, dateStr, Category, note, expenseRecordsFile))
            return false;
        incrimentExpenseCount();
        return true;
    }

    private boolean addRecords(int ID, double amount, String dateStr, String sourceORcategory, String note,
            String fileName) {
        try {
            File file = tryToCreateFile(fileName);
            try (FileWriter fileWriter = new FileWriter(file, true)) {
                fileWriter.write(
                        ID + "    " + amount + "    " + dateStr + "       " + sourceORcategory + "    " + note + "\n");
            }
            return true;
        } catch (Exception e) {
            System.out.println("\terr: " + e.getMessage());
        }
        return false;
    }

    // Get all records
    public void getAllIncomeRecords() {
        getAllRecords(incomeRecordsFile);
    }

    public void getAllExpenseRecords() {
        getAllRecords(expenseRecordsFile);
    }

    private void getAllRecords(String fileName) {
        try {
            // File file = new File(fileName);
            // if (!file.exists()) {
            // System.out.println("Records not found");
            // return;
            // }
            File file = tryToCreateFile(fileName);
            try (Scanner fileScanner = new Scanner(file)) {
                if (!fileScanner.hasNextLine())
                    System.out.println("Records not found");
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            System.out.println("\terr: " + e);
        }
    }

    // Delete record file of a type
    public void deleteAllRecordsOfIncome() {
        deleteAllRecordsFrom(incomeRecordsFile, incomeCountFile);
    }

    public void deleteAllRecordsOfExpense() {
        deleteAllRecordsFrom(expenseRecordsFile, expenseCountFile);
    }

    private void deleteAllRecordsFrom(String fileOfRecord, String fileOfCount) {
        // 1
        File recordFile = new File(fileOfRecord);
        if (!recordFile.exists())
            System.out.println("Record file not found");
        else
            recordFile.delete();

        // 2
        File countFile = new File(fileOfCount);
        if (!countFile.exists())
            System.out.println("Count file not found");
        else
            countFile.delete();
    }

    // remove targeted record
    public boolean removeIncomeRecord(int id) {
       return removeRecordsForMatchedField(id, incomeRecordsFile);
    }

    public boolean removeExpenseRecord(int id) {
        return removeRecordsForMatchedField(id, expenseRecordsFile);
    }

    private boolean removeRecordsForMatchedField(int id, String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println("File not found");
                return false;
            }
            try (Scanner fileScanner = new Scanner(file)) {
                while (fileScanner.hasNextLine()) {
                    int idFromData = Integer.parseInt(fileScanner.next());
                    if (idFromData == id) {
                        // data.remove
                        // how to remove a particular block ?
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("\terr: " + e.getMessage());
        }
        System.out.println("Id not match");
        return false;
    }

    // Stub method for check

    // public static void main(String[] args) {
    // FileStorage fs = new FileStorage();
    // fs.addIncomeRecord(1, 1000, "2023-10-01", "Salary", "Job");
    // fs.addExpenseRecord(1, -100, "2023-10-02", "Lunch", "Food");
    // System.out.println();
    // System.out.println(fs.getIncomeCount());
    // System.out.println(fs.getExpenseCount());
    // System.out.println(" \n");
    // fs.getAllIncomeRecords();
    // System.out.println(" \n");
    // fs.getAllExpenseRecords();

    // fs.deleteAllRecordsOfExpense();
    // fs.getAllExpenseRecords();
    // fs.addExpenseRecord(1, -100, "2023-10-02", "Lunch", "Food");

    // }
}