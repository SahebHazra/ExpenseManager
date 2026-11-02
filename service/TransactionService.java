package service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Scanner;

import model.*;
import storage.FileStorage;
import util.*;

// Enum to represent the type of record: INCOME, EXPENSE, or MIXED
enum RecordType {
    INCOME, EXPENSE, MIXED;
}

// TransactionService is a class that manages income and expense records. It
// provides methods to add, retrieve, edit, and delete records. The class uses
// HashMaps to store income and expense records, with unique IDs as keys. It
// also includes methods for validating input, handling categories, and
// displaying record details.

public class TransactionService {
    private int ID;
    private double amount;
    private String source, note, category;
    private String dateStr;
    private Scanner sc;

    private static HashMap<Integer, Income> incomeTracker = new HashMap<>();
    private static HashMap<Integer, Expense> expenseTracker = new HashMap<>();

    private final FileStorage FS;
    private static boolean recordsLoaded = false;

    public TransactionService(Scanner scanner) {
        this.sc = scanner;
        this.FS = new FileStorage();
        loadPersistedRecords();
    }

    private void loadPersistedRecords() {
        if (recordsLoaded) return;
        for (String[] row : FS.loadIncomeRecords()) {
            try {
                int id = Integer.parseInt(row[0]);
                double amount = Double.parseDouble(row[1]);
                incomeTracker.put(id, new Income(id, amount, row[2], row[3], row[4]));
            } catch (Exception e) {
                System.out.println("Skipping invalid income record: " + e.getMessage());
            }
        }
        for (String[] row : FS.loadExpenseRecords()) {
            try {
                int id = Integer.parseInt(row[0]);
                double amount = Double.parseDouble(row[1]);
                String category = Category.normalize(row[3]);
                if (!Category.isExist(category)) Category.addCategory(category);
                Expense expense = new Expense(id, amount, row[2], category, row[4]);
                expenseTracker.put(id, expense);
                CategoryExpenseService.incrimentCategoryExpense(category, expense.getAmount());
            } catch (Exception e) {
                System.out.println("Skipping invalid expense record: " + e.getMessage());
            }
        }
        recordsLoaded = true;
    }

    private int getUniqueID() {
       int localID = FS.IncrimentAndGetID();
        while ((localID == -1) || incomeTracker.containsKey(localID) || expenseTracker.containsKey(localID))
            localID = UniqueID.getID();
        return localID;
    }

    private void takeInputs() throws InvalidAmountException {
        this.ID = getUniqueID();
        System.out.print("Amount : ");
        this.amount = InputValidator.getValidDouble(sc);
        if (!Double.isFinite(amount))
            throw new InvalidAmountException("Please Enter Valid Digit for Amount!");
        sc.nextLine(); // consume the newline left by next()
        System.out.print("Notes : ");
        this.note = NullPointerHandler.perform(sc.nextLine());
        System.out.print("Date (yyyy-mm-dd) : ");
        this.dateStr = sc.nextLine();
        if (!DateUtil.isValidDate(dateStr)) {
            System.out.println(": Date Format as -> yyyy-mm-dd ");
            System.out.print("Enter again : ");
            this.dateStr = sc.nextLine();
            if (!DateUtil.isValidDate(dateStr)) {
                System.out.println(":  Initializing with Today's Date, You can Change it Later");
                this.dateStr = LocalDate.now().toString();
            }
        }
    }

    public void addIncomeRecords() throws InvalidAmountException {
        takeInputs();
        System.out.print("Source : ");
        this.source = NullPointerHandler.perform(sc.nextLine());
        if (!FS.addIncomeRecord(ID, amount, dateStr, source, note)) {
            System.out.println("Could not save income record. No changes were made.");
            return;
        }
        incomeTracker.put(ID, new Income(ID, amount, dateStr, source, note));
    }

    private String categoryValidator(String category) {
        category = Category.normalize(category);
        if (category.isEmpty() || category.equalsIgnoreCase("N/A"))
            return "Other";
        else if (!Category.isExist(category)) {
            System.out.println("This Category is not a valid Category!");
            System.out.println("Wan'na make a new Category ? [y/n] : ");
            String respond = sc.next();
            if (respond.equalsIgnoreCase("y") && Category.addCategory(category)) {
                return category;
            } else {
                System.out.println("Fall-back : 'Other' ");
                return "Other";
            }
        }
        return category;
    }

    public void addExpenseRecords() throws InvalidAmountException {
        takeInputs();
        System.out.println("Avilable Category " + Category.getAllCategorys());
        System.out.print("Category : ");
        this.category = categoryValidator(sc.nextLine());
        amount = Math.abs(amount) * -1; // Ensure Negative
        if (!FS.addExpenseRecord(ID, amount, dateStr, category, note)) {
            System.out.println("Could not save expense record. No changes were made.");
            return;
        }
        expenseTracker.put(ID, new Expense(ID, amount, dateStr, category, note));
        CategoryExpenseService.incrimentCategoryExpense(category, amount);
    }

    public void showIncomeRecords() {
        System.out.println("All Income Records...");
        // showRecordsFromMap(RecordType.INCOME);

        System.out.println(" ID        AMOUNT       DATE       SOURCE    NOTE");
        System.out.println("------------------------------------------------------");
        FS.getAllIncomeRecords();
        System.out.println("------------------------------------------------------");
        System.out.println("Total Income Records : " + FS.getIncomeCount());
    }

    public void showExpenseRecords() {
        // System.out.println("All Expense Records...");
        // showRecordsFromMap(RecordType.EXPENSE);

        System.out.println(" ID        AMOUNT       DATE        CATEGORY     NOTE");
        System.out.println("------------------------------------------------------");
        FS.getAllExpenseRecords();
        System.out.println("------------------------------------------------------");
        System.out.println("Total Expense Records : " + FS.getExpenseCount());
    }

    public void showAllRecords() {
        System.out.println("All Income & Expense Records...");
        // showRecordsFromMap(RecordType.MIXED);
        showIncomeRecords();
        System.out.println();
        showExpenseRecords();
    }

    private void showRecordsFromMap(RecordType recTyp) {
        if (recTyp == RecordType.INCOME && incomeTracker.isEmpty()) {
            System.out.println("Income Records Not Found!");
            return;
        } else if (recTyp == RecordType.EXPENSE && expenseTracker.isEmpty()) {
            System.out.println("Expense Records Not Found!");
            return;
        } else if (incomeTracker.isEmpty()
                && expenseTracker.isEmpty()) {
            System.out.println("Records Not Found!");
            return;
        }
        System.out.println(" ID       DATE      RecordType      AMOUNT    CATEGORY/SOURCE");
        System.out.println("------------------------------------------------------");
        if (recTyp == RecordType.INCOME || recTyp == RecordType.MIXED) {
            for (Income TRI : incomeTracker.values()) {
                System.out.print(TRI.getID() + "   " + TRI.getDate() + "   " + RecordType.INCOME);
                System.out.println("      " + TRI.getAmount() + "    " + TRI.getSource());
            }
        }
        if (recTyp == RecordType.EXPENSE || recTyp == RecordType.MIXED) {
            for (Expense TRE : expenseTracker.values()) {
                System.out.print(TRE.getID() + "   " + TRE.getDate() + "   " + RecordType.EXPENSE);
                System.out.println("      " + TRE.getAmount() + "    " + TRE.getCategory());
            }
        }
        System.out.println("------------------------------------------------------");
    }

    public static void searchDetailsByID(int id) {

        if (!incomeTracker.containsKey(id)
                && !expenseTracker.containsKey(id)) {
            System.out.println("ID not Match!");
            return;
        }
        if (incomeTracker.containsKey(id)) {
            Income record = incomeTracker.get(id);
            System.out.println(
                    "\t" + id + "   " + record.getDate() + "   INCOME    " + record.getAmount() + "    "
                            + record.getSource() + "    Note: " + record.getNote());
        }
        if (expenseTracker.containsKey(id)) {
            Expense record = expenseTracker.get(id);
            System.out.println(
                    "\t" + id + "   " + record.getDate() + "   EXPENSE    " + record.getAmount() + "    "
                            + record.getCategory() + "    Note: " + record.getNote());
        }
    }

    // Delete method for delete Record by ID
    public void deleteRecordByID(int id) {
        String recordRecordType = "Income";
        if (incomeTracker.containsKey(id)) {
            if (!FS.removeIncomeRecord(id)) {
                System.out.println("Could not delete income record from storage.");
                return;
            }
            incomeTracker.remove(id);
        } else if (expenseTracker.containsKey(id)) {
            recordRecordType = "Expense";
            Expense record = expenseTracker.get(id);
            if (!FS.removeExpenseRecord(id)) {
                System.out.println("Could not delete expense record from storage.");
                return;
            }
            CategoryExpenseService.decrimentCategoryExpense(record.getCategory(), record.getAmount());
            expenseTracker.remove(id);
        } else {
            System.out.println("The id " + id + " is not match");
            return;
        }
        System.out.println("Record has been deleted sucessfully, from " + recordRecordType);
    }

    private int filedToEdit(RecordType recTyp) {
        System.out.println("What do you want to edit ? ");
        System.out.print(" 1: Amount ,2: Date, 3:Note, ");
        if (recTyp == RecordType.INCOME)
            System.out.println("4:Source ");
        else
            System.out.println("4:Category ");
        System.out.println("AnyKey: Cancel");
        int option = InputValidator.getValidInt(sc);
        sc.nextLine(); // consume the newline left by next()
        System.out.print("Enter ");
        if (option == 1) {
            System.out.print("Amount : ");
            this.amount = InputValidator.getValidDouble(sc);
        } else if (option == 2) {
            System.out.print("Date : ");
            this.dateStr = sc.nextLine();
            if (!DateUtil.isValidDate(dateStr))
                this.dateStr = LocalDate.now().toString();
        } else if (option == 3) {
            System.out.print("Note : ");
            this.note = sc.nextLine();
        } else if (option == 4 && recTyp == RecordType.INCOME) {
            System.out.print("Source : ");
            this.source = sc.nextLine();
        } else if (option == 4 && recTyp == RecordType.EXPENSE) {
            System.out.println("Enter Category " + Category.getAllCategorys());
            System.out.print("Category : ");
            this.category = categoryValidator(sc.nextLine());
        } else {
            System.out.println("Operation Canceld!");
        }
        return option;

    }

    public void editReordByID(int id) throws InvalidAmountException {

        if (incomeTracker.containsKey(id)) {
            int option = filedToEdit(RecordType.INCOME);
            if (option == 1)
                incomeTracker.get(id).setAmount(amount);
            else if (option == 2)
                incomeTracker.get(id).setDate(dateStr);
            else if (option == 3)
                incomeTracker.get(id).setNote(note);
            else if (option == 4)
                incomeTracker.get(id).setSource(source);
            else
                return;
        } else if (expenseTracker.containsKey(id)) {
            int option = filedToEdit(RecordType.EXPENSE);
            if (option == 1) {
                Expense record = expenseTracker.get(id);

                if (amount == 0) {
                    System.out.println("Zero not allow for edit!");
                    System.out.println("Consider to Delete the record");
                    return;
                }
                amount = Math.abs(amount) * -1;

                double adjustableAmount = amount - record.getAmount();
                String recordCategory = record.getCategory();
                CategoryExpenseService.incrimentCategoryExpense(recordCategory, adjustableAmount); // auto -/+
                record.setAmount(amount);
            } else if (option == 2)
                expenseTracker.get(id).setDate(dateStr);
            else if (option == 3)
                expenseTracker.get(id).setNote(note);
            else if (option == 4) {
                Expense record = expenseTracker.get(id);
                if (CategoryExpenseService.adjustBetweenCategory(record.getCategory(), category, record.getAmount()))
                    record.setCategory(category);
                else
                    System.out.println("Operation Failed!");
            } else
                return;
        } else {
            System.out.println("ID not match!");
            return;
        }
        boolean saved;
        if (incomeTracker.containsKey(id)) {
            Income r = incomeTracker.get(id);
            saved = FS.updateIncomeRecord(id, r.getAmount(), r.getDate().toString(), r.getSource(), r.getNote());
        } else {
            Expense r = expenseTracker.get(id);
            saved = FS.updateExpenseRecord(id, r.getAmount(), r.getDate().toString(), r.getCategory(), r.getNote());
        }
        if (!saved) {
            System.out.println("Warning: record changed in memory but could not be saved to disk.");
            return;
        }
        System.out.println("ID " + id + " is Edited Sucessfully!");
        searchDetailsByID(id);
    }

    static HashMap<Integer, Income> getAllIncomeRecords() {
        return new HashMap<>(incomeTracker);
    }

    static HashMap<Integer, Expense> getAllExpenseRecords() {
        return new HashMap<>(expenseTracker);
    }

    public static int getIncomeCount() {
        return incomeTracker.size();
    }

    public static int getExpenseCount() {
        return expenseTracker.size();
    }
}