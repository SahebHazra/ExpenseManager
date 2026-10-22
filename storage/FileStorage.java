package storage;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

enum OperationType {
    GET_RECORDS, SET_RECORDS
}

public class FileStorage {
    private final String incomeCountFile;
    private final String expenseCountFile;
    private final String incomeRecordsFile;
    private final String expenseRecordsFile;

    public FileStorage() {
        incomeCountFile = "IncomeCount.txt";
        expenseCountFile = "ExpenseCount.txt";
        incomeRecordsFile = "IncomeRecords.txt";
        expenseRecordsFile = "ExpenseRecords.txt";
    }

    // Stores only count
    public boolean storeIncomeCount() {
        int count = ManageExpenseIncomeCount(incomeCountFile, OperationType.SET_RECORDS);
        return count >= 0;
    }

    public boolean storeExpenseCount() {
        int count = ManageExpenseIncomeCount(expenseCountFile, OperationType.SET_RECORDS);
        return count >= 0;
    }

    public int getIncomeCount() {
        return ManageExpenseIncomeCount(incomeCountFile, OperationType.GET_RECORDS);
    }

    public int getExpenseCount() {
        return ManageExpenseIncomeCount(expenseCountFile, OperationType.GET_RECORDS);
    }

    // return count if operation Success | return -1 if failed
    private int ManageExpenseIncomeCount(String fileNameWithExtention, OperationType oprn) {
        try {
            File file = new File(fileNameWithExtention);
            if (!file.exists())
                file.createNewFile();

            int count = 0;
            try (Scanner sc = new Scanner(file)) {
                if (sc.hasNextInt()) {
                    count = sc.nextInt();
                }
            }

            if (oprn == OperationType.SET_RECORDS) {
                count++;
                try (FileWriter fileWriter = new FileWriter(file, false)) {
                    fileWriter.write(String.valueOf(count));
                }
            }
            return count;
        } catch (Exception e) {
            System.out.println("\terr: " + e.getMessage());
        }
        return -1;
    }

    // Large amount of record stores
    public boolean addIncomeRecord(int ID, double amount, String dateStr, String source, String note) {
        if (!addRecords(ID, amount, dateStr, source, note, incomeRecordsFile))
            return false;
        storeIncomeCount();
        return true;
    }

    public boolean addExpenseRecord(int ID, double amount, String dateStr, String Category, String note) {
        if (!addRecords(ID, amount, dateStr, Category, note, expenseRecordsFile))
            return false;
        storeExpenseCount();
        return true;
    }

    private boolean addRecords(int ID, double amount, String dateStr, String sourceORcategory, String note,
            String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists())
                file.createNewFile();

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
            File file = new File(fileName);
            if (!file.exists()) {
                System.out.println("File not found");
                return;
            }
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            System.out.println("\terr: " + e.getMessage());
        }
    }

    // public static void main(String[] args) {
    // FileStorage fs = new FileStorage();

    // fs.addIncomeRecord(1, 1000, "2023-10-01", "Salary", "Job");
    // fs.addExpenseRecord(1, -100, "2023-10-02", "Lunch", "Food");
    // System.out.println(fs.getIncomeCount());
    // System.out.println(fs.getExpenseCount());
    // System.out.println(" \n");
    // fs.getAllIncomeRecords();
    // System.out.println(" \n");
    // fs.getAllExpenseRecords();

    // }
}