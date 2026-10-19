package service;

import java.util.HashMap;
import model.Category;

public abstract class CategoryExpenseService {

    private static HashMap<String, Double> categoryExpense = new HashMap<>();

    public static boolean isCategoryExistInExpense(String category) {
        return categoryExpense.containsKey(category);
    }

    public static double getCategoryExpense(String expense) {
        if (isCategoryExistInExpense(expense))
            return categoryExpense.get(expense);
        return 0.0;
    }

    public static void setCategoryExpense(String category, double expenseAmount) {
        if (Category.isExist(category))
            categoryExpense.put(category, expenseAmount);
        else
            System.out.println("Invalid Category");
    }

    public static void incrimentCategoryExpense(String category, double expenseAmount) {
        double oldExpenseAmount = 0;
        if (isCategoryExistInExpense(category))
            oldExpenseAmount = categoryExpense.get(category);
        setCategoryExpense(category, oldExpenseAmount + expenseAmount);
    }

    public static void decrimentCategoryExpense(String category, double expenseAmount) {
        if (!isCategoryExistInExpense(category))
            return;
        double oldExpenseAmount = categoryExpense.get(category);
        setCategoryExpense(category, oldExpenseAmount - expenseAmount);
    }

    public static void adjustBetweenCategory(String oldCategory, String newCategory, double expenseAmount) {
        if (isCategoryExistInExpense(oldCategory) && isCategoryExistInExpense(newCategory)) {
            decrimentCategoryExpense(oldCategory, expenseAmount);
            incrimentCategoryExpense(newCategory, expenseAmount);
        } else {
            System.out.println("Operation Failed!");
        }
    }

    public static void showExpenseOf(String category) {
        System.out.println("---- CATEGORY    -----    EXPENSE ----\n");
        System.out.print("  " + category + "       " + getCategoryExpense(category));
        System.out.println("\n-------------------------------------");
    }

    public static void showAllCategoryExpense() {
        System.out.println("---- CATEGORY    -----    EXPENSE ----\n");
        double totalExpenseAmount = 0;
        for (String category : categoryExpense.keySet()) {
            double currentExpenseAmount = categoryExpense.get(category);
            System.out.print("  " + category + "       " + currentExpenseAmount);
            totalExpenseAmount += currentExpenseAmount;
        }
        System.out.println("\n-------------------------------------");
        System.out.println("TOTAL        " + totalExpenseAmount);
    }

}
