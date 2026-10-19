package service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Scanner;

import model.*;
import util.*;

enum RecordType {
    INCOME, EXPENSE, MIXED;
}

public class TransactionService {
    private int ID;
    private double amount;
    private String source, note, category;
    private String dateStr;
    private Scanner sc;

    // private BudgetService bgtService;
    private static HashMap<Integer, Income> incomeTracker = new HashMap<>();
    private static HashMap<Integer, Expense> expenseTracker = new HashMap<>();

    public TransactionService(Scanner scanner) {
        this.sc = scanner;
    }

    private int getUniqueID() {
        ID = UniqueID.getID();
        while (!(ID > 0) || incomeTracker.containsKey(ID) || expenseTracker.containsKey(ID))
            ID = UniqueID.getID();
        return ID;
    }

    private void takeInputs() throws InvalidAmountException {
        this.ID = getUniqueID();
        System.out.print("Amount : ");
        this.amount = InputValidator.getValidDouble(sc);
        if (amount == 0) {
            throw new InvalidAmountException("Can't ProCategoryExpenseServices this Ammount");
        }
        sc.nextLine();
        System.out.print("Notes : ");
        this.note = sc.nextLine();
        System.out.print("Date (yyyy-mm-dd) : ");
        this.dateStr = sc.nextLine();
        if (!DateUtil.isValidDate(dateStr)) {
            this.dateStr = LocalDate.now().toString();
        }
    }

    public void addIncomeRecords() throws InvalidAmountException {
        takeInputs();
        System.out.print("Source : ");
        this.source = sc.nextLine();
        incomeTracker.put(ID, new Income(ID, amount, source, dateStr, note));
    }

    private String categoryValidator(String category) {
        if (NullPointerHandler.perform(category).equalsIgnoreCase("N/A"))
            return "Other";
        else if (!Category.isExist(category)) {
            System.out.println("This Category is not a valid Category!");
            System.out.println("Wan'na make a new Category ? [y/n] : ");
            String respond = sc.next();
            if (respond.equalsIgnoreCase("y")) {
                Category.addCategory(category);
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
        expenseTracker.put(ID, new Expense(ID, amount, category, dateStr, note));
        CategoryExpenseService.incrimentCategoryExpense(category, amount);
        // bgtService.updateIncrimentExpense(category, amount);
    }

    public void getIncomeRecords() {
        System.out.println("All Income Records...");
        getRecords(RecordType.INCOME);
    }

    public void getExpenseRecords() {
        System.out.println("All Expense Records...");
        getRecords(RecordType.EXPENSE);
    }

    public void getAllRecords() {
        System.out.println("All Income & Expense Records...");
        getRecords(RecordType.MIXED);
    }

    // Heavy Manupulations
    public void deleteRecordByID(int id) throws Exception {
        String recordRecordType = "Income";
        if (incomeTracker.containsKey(id))
            incomeTracker.remove(id);
        else if (expenseTracker.containsKey(id)) {
            recordRecordType = "Expense";
            Expense record = expenseTracker.get(id);
            CategoryExpenseService.decrimentCategoryExpense(record.getCategory(), record.getAmount());
            expenseTracker.remove(id);
        } else {
            System.out.println("The id " + id + " is not match");
            return;
        }
        System.out.println("Record has been deleted suCategoryExpenseServicesfully, from " + recordRecordType);
    }

    private void getRecords(RecordType recTyp) {
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
        System.out.println(" ID       DATE         RecordType      AMOUNT    CATEGORY/SOURCE");
        System.out.println("------------------------------------------------------");
        if (recTyp == RecordType.INCOME || recTyp == RecordType.MIXED) {
            for (Income TRI : incomeTracker.values()) {
                System.out.print(TRI.getID() + "   " + TRI.getDate() + "   " + RecordType.INCOME);
                System.out.println("    " + TRI.getAmount() + "    " + TRI.getSource());
            }
        }
        if (recTyp == RecordType.EXPENSE || recTyp == RecordType.MIXED) {
            for (Expense TRE : expenseTracker.values()) {
                System.out.print(TRE.getID() + "   " + TRE.getDate() + "   " + RecordType.EXPENSE);
                System.out.println("    " + TRE.getAmount() + "    " + TRE.getCategory());
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

    private int filedToEdit(RecordType recTyp) {
        System.out.println("What do you want to edit ? ");
        System.out.print(" 1: Amount ,2: Date, 3:Note, ");
        if (recTyp == RecordType.INCOME)
            System.out.println("4:Source ");
        else
            System.out.println("4:Category ");
        System.out.println("AnyKey: Cancel");
        int option = InputValidator.getValidInt(sc);
        System.out.print("Enter ");
        sc.nextLine();
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
                CategoryExpenseService.incrimentCategoryExpense(recordCategory, adjustableAmount); // auto
                                                                                                   // incriment/decriment
                record.setAmount(amount);
            } else if (option == 2)
                expenseTracker.get(id).setDate(dateStr);
            else if (option == 3)
                expenseTracker.get(id).setNote(note);
            else if (option == 4) {
                Expense record = expenseTracker.get(id);
                CategoryExpenseService.adjustBetweenCategory(record.getCategory(), category, record.getAmount());
                record.setCategory(category);
            } else
                return;
        } else {
            System.out.println("ID not match!");
            return;
        }
        System.out.println("ID " + id + " is Edited SuCategoryExpenseServicesfully!");

        searchDetailsByID(id);
    }

    static HashMap<Integer, Income> getAllIncomeRecords() {
        return incomeTracker;
    }

    static HashMap<Integer, Expense> getAllExpenseRecords() {
        return expenseTracker;
    }

}