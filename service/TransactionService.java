package service;

import java.time.LocalDate;
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

    private BudgetService bgtService;
    private static final SharedRuntimeResourceManager SRRM = new SharedRuntimeResourceManager();

    public TransactionService(Scanner scanner) {
        this.sc = scanner;
        bgtService = new BudgetService(scanner);
    }

    private int getUniqueID() {
        ID = UniqueID.getID();
        while (!(ID > 0) || SRRM.incomeTracker.containsKey(ID) || SRRM.expenseTracker.containsKey(ID))
            ID = UniqueID.getID();
        return ID;
    }

    private void takeInputs() throws InvalidAmountException {
        this.ID = getUniqueID();
        System.out.print("Amount : ");
        this.amount = InputValidator.getValidDouble(sc);
        if (amount == 0) {
            throw new InvalidAmountException("Can't Process this Ammount");
        }
        sc.nextLine();
        System.out.print("Notes : ");
        this.note = SRRM.nullPointerHandler(sc.nextLine());
        System.out.print("Date (yyyy-mm-dd) : ");
        this.dateStr = sc.nextLine();
        if (!DateUtil.isValidDate(dateStr)) {
            this.dateStr = LocalDate.now().toString();
        }
    }

    public void addIncomeRecords() throws InvalidAmountException {
        takeInputs();
        System.out.print("Source : ");
        this.source = SRRM.nullPointerHandler(sc.nextLine());
        SRRM.incomeTracker.put(ID, new Income(ID, amount, source, dateStr, note));
    }

    private void categoryValidator() {

        if (category.equalsIgnoreCase("N/A")) {
            this.category = "Other";
            return;
        }

        if (!Category.isExist(category)) {
            System.out.println("This Category is not a vlid Category!");
            System.out.println("Wan'na make a new Category ? [y/n] : ");
            String respond = sc.next();
            if (respond.equalsIgnoreCase("y"))
                Category.addCategory(category);
            else {
                System.out.println("Fall-back : 'Other' ");
                this.category = "Other";
            }
        }
    }

    public void addExpenseRecords() throws InvalidAmountException {
        takeInputs();
        System.out.println("Enter Category " + Category.getAllCategorys());
        System.out.print("Category : ");
        this.category = SRRM.nullPointerHandler(sc.nextLine());
        categoryValidator();
        SRRM.expenseTracker.put(ID, new Expense(ID, amount, category, dateStr, note));
        bgtService.updateIncrimentExpense(category, amount);
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

    public void deleteRecordByID(int id) throws Exception {
        String recordRecordType = "Income";
        if (SRRM.incomeTracker.containsKey(id))
            SRRM.incomeTracker.remove(id);
        else if (SRRM.expenseTracker.containsKey(id)) {
            recordRecordType = "Expense";
            Expense record = SRRM.expenseTracker.get(id);
            // Adjust either the active budget expense or the deletedCategory cache
            bgtService.adjustDeletedOrExistingExpense(record.getCategory(), record.getAmount());
            SRRM.expenseTracker.remove(id);
        } else {
            System.out.println("The id " + id + " is not match");
            return;
        }
        System.out.println("Record has been deleted sucessfully, from " + recordRecordType);
    }

    private void getRecords(RecordType recTyp) {
        if (recTyp == RecordType.INCOME && SRRM.incomeTracker.isEmpty()) {
            System.out.println("Income Records Not Found!");
            return;
        } else if (recTyp == RecordType.EXPENSE && SRRM.expenseTracker.isEmpty()) {
            System.out.println("Expense Records Not Found!");
            return;
        } else if (SRRM.incomeTracker.isEmpty() && SRRM.expenseTracker.isEmpty()) {
            System.out.println("Records Not Found!");
            return;
        }
        System.out.println(" ID       DATE         RecordType      AMOUNT    CATEGORY/SOURCE");
        System.out.println("------------------------------------------------------");
        if (recTyp == RecordType.INCOME || recTyp == RecordType.MIXED) {
            for (Income TRI : SRRM.incomeTracker.values()) {
                System.out.print(TRI.getID() + "   " + TRI.getDate() + "   " + RecordType.INCOME);
                System.out.println("    " + TRI.getAmount() + "    " + TRI.getSource());
            }
        }
        if (recTyp == RecordType.EXPENSE || recTyp == RecordType.MIXED) {
            for (Expense TRE : SRRM.expenseTracker.values()) {
                System.out.print(TRE.getID() + "   " + TRE.getDate() + "   " + RecordType.EXPENSE);
                System.out.println("    " + TRE.getAmount() + "    " + TRE.getCategory());
            }
        }
        System.out.println("------------------------------------------------------");
    }

    public static void searchDetailsByID(int id) {

        if (!SRRM.incomeTracker.containsKey(id) && !SRRM.expenseTracker.containsKey(id)) {
            System.out.println("ID not Match!");
            return;
        }
        if (SRRM.incomeTracker.containsKey(id)) {
            Income record = SRRM.incomeTracker.get(id);
            System.out.println(
                    "\t" + id + "   " + record.getDate() + "   INCOME    " + record.getAmount() + "    "
                            + record.getSource() + "    Note: " + record.getNote());
        }
        if (SRRM.expenseTracker.containsKey(id)) {
            Expense record = SRRM.expenseTracker.get(id);
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
            this.note = SRRM.nullPointerHandler(sc.nextLine());
        } else if (option == 4 && recTyp == RecordType.INCOME) {
            System.out.print("Source : ");
            this.source = SRRM.nullPointerHandler(sc.nextLine());
        } else if (option == 4 && recTyp == RecordType.EXPENSE) {
            System.out.println("Enter Category " + Category.getAllCategorys());
            System.out.print("Category : ");
            this.category = SRRM.nullPointerHandler(sc.nextLine());
        } else {
            System.out.println("Operation Canceld!");
        }
        return option;

    }

    public void editReordByID(int id) throws InvalidAmountException {

        if (SRRM.incomeTracker.containsKey(id)) {
            int option = filedToEdit(RecordType.INCOME);
            if (option == 1)
                SRRM.incomeTracker.get(id).setAmount(amount);
            else if (option == 2)
                SRRM.incomeTracker.get(id).setDate(dateStr);
            else if (option == 3)
                SRRM.incomeTracker.get(id).setNote(note);
            else if (option == 4)
                SRRM.incomeTracker.get(id).setSource(source);
            else
                return;
        } else if (SRRM.expenseTracker.containsKey(id)) {
            int option = filedToEdit(RecordType.EXPENSE);
            if (option == 1) {
                Expense record = SRRM.expenseTracker.get(id);

                if (amount >= 0) {
                    System.out.println("Expense amount must be negative!");
                    return;
                }

                double adjustableAmount = amount - record.getAmount();
                String recordCategory = record.getCategory();
                double newCategoryExpense = bgtService.getCategoryExpense(recordCategory) + adjustableAmount;

                record.setAmount(amount);
                bgtService.updateChangeExpense(recordCategory, newCategoryExpense);
            } else if (option == 2)
                SRRM.expenseTracker.get(id).setDate(dateStr);
            else if (option == 3)
                SRRM.expenseTracker.get(id).setNote(note);
            else if (option == 4) {
                categoryValidator();
                SRRM.expenseTracker.get(id).setCategory(category);
            } else
                return;
        } else {
            System.out.println("ID not match!");
            return;
        }
        System.out.println("ID " + id + " is Edited Sucessfully!");

        searchDetailsByID(id);
    }
}