package service;

import java.util.HashMap;
import model.Category;

// CategoryExpenseService is an abstract class that provides methods for managing category expenses. It uses a HashMap to store the expenses associated with each category. The class includes methods to check if a category exists, get the expense for a category, set the expense for a category, increment or decrement the expense for a category, adjust expenses between categories, and display expenses for specific categories or all categories.

public abstract class CategoryExpenseService {

    private static HashMap<String, Double> categoryExpense = new HashMap<>();

    static String categoryNormalization(String category) {
        return Category.normalize(category);
    }

    // Check if a category exists in the categoryExpense HashMap
    public static boolean isCategoryExistInExpense(String category) {
        return categoryExpense.containsKey(categoryNormalization(category));
    }

    // Get the expense amount for a specific category. If the category does not
    // exist, return 0.0
    public static double getCategoryExpense(String expense) {
        expense = categoryNormalization(expense);
        if (isCategoryExistInExpense(expense))
            return categoryExpense.get(expense);
        return 0.0;
    }

    // Set the expense amount for a specific category. If the category does not
    // exist, print "Invalid Category"
    public static void setCategoryExpense(String category, double expenseAmount) {
        category = categoryNormalization(category);
        if (!Category.isExist(category)) {
            System.out.println("Category creation for Expense is Failed!");
            return;
        }
        categoryExpense.put(category, expenseAmount);
    }

    // Increment the expense amount for a specific category by a given
    // expenseAmount. If the category does not exist, initialize it with the given
    // expenseAmount
    public static void incrimentCategoryExpense(String category, double expenseAmount) {
        double oldExpenseAmount = 0;
        category = categoryNormalization(category);
        if (isCategoryExistInExpense(category))
            oldExpenseAmount = categoryExpense.get(category);
        setCategoryExpense(category, oldExpenseAmount + expenseAmount);
    }

    // Decrement the expense amount for a specific category by a given
    // expenseAmount. If the category does not exist, do nothing
    public static void decrimentCategoryExpense(String category, double expenseAmount) {
        category = categoryNormalization(category);
        if (!isCategoryExistInExpense(category))
            return;
        double oldExpenseAmount = categoryExpense.get(category);
        setCategoryExpense(category, oldExpenseAmount - expenseAmount);
    }

    // Adjust the expense amounts between two categories by decrementing the
    // oldCategory's expense and incrementing the newCategory's expense by a given
    // expenseAmount. If either category does not exist, print "Operation Failed!"

    public static boolean adjustBetweenCategory(String oldCategory, String newCategory, double expenseAmount) {
        if (oldCategory == null || newCategory == null || oldCategory.equals(newCategory)) {
            return false;
        }
        oldCategory = categoryNormalization(oldCategory);
        newCategory = categoryNormalization(newCategory);
        if (!Category.isExist(oldCategory) || !Category.isExist(newCategory)) {
            System.out.println("Category dose't Exist!");
            return false;
        }

        System.out.println(
                "Adjusting expense from " + oldCategory + " to " + newCategory + " by amount: " + expenseAmount);

        categoryExpense.put(oldCategory, getCategoryExpense(oldCategory) - expenseAmount);
        categoryExpense.put(newCategory, getCategoryExpense(newCategory) + expenseAmount);

        return true;
    }

    // Show the expense amount for a specific category
    public static void showExpenseOf(String category) {
        category = categoryNormalization(category);
        System.out.println("\n-------------------------------------");
        System.out.println(" CATEGORY       EXPENSE\n");
        System.out.print("  " + category + "      " + getCategoryExpense(category));
        System.out.println("\n-------------------------------------");
    }

    // Show the expense amounts for all categories
    public static void showAllCategoryExpense() {
        System.out.println("\n CATEGORY       EXPENSE");
        System.out.println("-------------------------------------\n");
        double totalExpenseAmount = 0;
        for (String category : categoryExpense.keySet()) {
            double currentExpenseAmount = categoryExpense.get(category);
            System.out.println(" " + category + "         " + currentExpenseAmount);
            totalExpenseAmount += currentExpenseAmount;
        }
        System.out.println("\n-------------------------------------");
        System.out.println("TOTAL        " + totalExpenseAmount);
    }

}
