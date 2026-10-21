package service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import model.*;
import util.InvalidAmountException;

public class BudgetService extends CategoryExpenseService {
    private Scanner sc;

    private static HashMap<String, Budget> categoryBudget = new HashMap<>();

    public BudgetService(Scanner scanner) {
        sc = scanner;
    }

    // Category & Budget
    // private static String categoryNormalization(String category) {
    // return Category.normalize(category);
    // }

    public boolean isCategoryExistInBudget(String category) {
        return categoryBudget.containsKey(categoryNormalization(category));
    }

    // Get Budget Amount for a Category | if the category is not exist in Budget,
    // then return 0.0
    public double getCategoryBGT(String category) {
        category = categoryNormalization(category);
        if (isCategoryExistInBudget(category))
            return categoryBudget.get(category).getBudgetAmount();
        return 0.0;
    }

    private boolean createNewCategory(String category) {
        category = categoryNormalization(category);
        if (!Category.isExist(category)) {
            System.out.println("This Category is not a valid Category!");
            System.out.println("Wan'na make new Category ? [y/n] : ");
            String respond = sc.next();
            if (respond.equalsIgnoreCase("y")) {
                if (Category.addCategory(category))
                    return true;
            }
        }
        return false;
    }

    // Set Budget for a Category | if the category is not exist in Budget, then
    // create new Budget for that category
    public void setBudgetCategory(String category, double budget) throws InvalidAmountException {
        category = categoryNormalization(category);
        if (isCategoryExistInBudget(category)) {
            System.out.println("The category alredy exist!");
            return;
        }
        if (!Category.isExist(category)) {
            if (!createNewCategory(category)) {
                System.out.println("Unable to create Category!");
                return;
            }
        }
        if (!isCategoryExistInBudget(category)) {
            categoryBudget.put(category, new Budget(budget));
            System.out.println("The category : \'" + category + "\' is Initialized");
        }
    }

    public void updateChangeBudget(String category, double newBudget) throws InvalidAmountException {
        category = categoryNormalization(category);
        if (!isCategoryExistInBudget(category)) {
            System.out.println("Budget plan was not created for this Category");
        } else if (isCategoryExistInBudget(category) && getCategoryBGT(category) != newBudget) {
            categoryBudget.get(category).setBudgetAmount(newBudget);
        }
    }

    // show Budget & Expense Relation | show Remaning or Exceed Amount
    public void showBugetExpenceRelationShip(String category) {
        category = categoryNormalization(category);
        if (isCategoryExistInBudget(category))
            showBugetExpenceRelationShip(getCategoryBGT(category), getCategoryExpense(category));
        else
            System.out.println("Seems the Budget category was not created");
    }

    private void showBugetExpenceRelationShip(double bgtAmount, double bgtExpense) {
        double result = bgtAmount + bgtExpense;
        if (result >= 0)
            System.out.print(" |  Remaning : ");
        else
            System.out.print(" |  Exceed with : ");
        System.out.println(result);
    }

    // Budget & Expense Relation | return percentage like xx.xx %
    private double getBudgetExpenseRelationInPersenteg(double budgetAmount, double expenseAmount) {
        double ret = (expenseAmount / budgetAmount) * 100;
        ret = Math.round(ret * 100.0) / 100.0; // Round to two decimal places
        return (ret < 0) ? -ret : ret;
    }

    // Show Budget & Expense Details for a specific category
    public void showCategoryBudget(String category) {
        category = categoryNormalization(category);
        if (isCategoryExistInBudget(category)) {
            System.out.println(" CATEGORY     BUDGET    EXPENSE ");
            System.out.println("-------------------------------------------------------------");
            double bgtAmount = getCategoryBGT(category);
            double bgtExpense = getCategoryExpense(category);
            System.out.print(" " + category + "       " + bgtAmount + "      " + bgtExpense);
            System.out.print("  (" + getBudgetExpenseRelationInPersenteg(bgtAmount, bgtExpense) + "%)  ");
            showBugetExpenceRelationShip(bgtAmount, bgtExpense);
            System.out.println("-------------------------------------------------------------");
        } else
            System.out.println("The Category \'" + category + "\' in Budget List is not exist");
    }

    // Show Budget & Expense Details for all category
    public void showAllCategorysBudget() {
        if (categoryBudget.isEmpty()) {
            System.out.println("Seems there no Budget category was created");
            return;
        }
        Iterator<String> keys = categoryBudget.keySet().iterator();
        System.out.println(" CATEGORY      BUDGET      EXPENSE ");
        System.out.println("-----------------------------------------------------------------");
        while (keys.hasNext()) {
            String category = keys.next();
            double bgtAmount = getCategoryBGT(category);
            double bgtExpense = getCategoryExpense(category);
            System.out.print(" " + category + "        " + bgtAmount + "        " + bgtExpense);
            System.out.print("    (" + getBudgetExpenseRelationInPersenteg(bgtAmount, bgtExpense) + "%)  ");
            showBugetExpenceRelationShip(bgtAmount, bgtExpense);
        }
        System.out.println("-----------------------------------------------------------------");
    }

    // remove Budget Category
    public void removeBudgetCategory(String category) {
        category = categoryNormalization(category);
        if (isCategoryExistInBudget(category)) {
            categoryBudget.remove(category);
            System.out.println("Category " + category + " is removed");
        } else
            System.out.println("The Budget Category is Not exist");
    }

}
