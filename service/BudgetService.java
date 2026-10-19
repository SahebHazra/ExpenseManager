package service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import model.*;
import util.InvalidAmountException;

public class BudgetService {
    private Scanner sc;
    private final double DEFAULT_EXPENSE_FOR_BUDGET;

    private static HashMap<String, Budget> categoryBudget = new HashMap<>();
    private static HashMap<String, Double> deletedCategory = new HashMap<>();

    public BudgetService(Scanner scanner) {
        sc = scanner;
        DEFAULT_EXPENSE_FOR_BUDGET = -0.00001;
    }

    // Category & Budget
    public boolean isCategoryExistInBudget(String category) {
        return categoryBudget.containsKey(category);
    }


    public double getCategoryExpense(String category) {
        return categoryBudget.get(category).getExpenseAmount();
    }

    public double getCategoryBGT(String category) {
        return categoryBudget.get(category).getBudgetAmount();
    }

    public void setBudgetCategory(String category, double budget) throws InvalidAmountException {
        setBudgetCategory(category, budget, DEFAULT_EXPENSE_FOR_BUDGET);
    }

    public void setBudgetCategory(String category, double budget, double expense) throws InvalidAmountException {
        if (isCategoryExistInBudget(category)) {
            System.out.println("The category exist, Updating Data...");
            updateChangeBudget(category, budget);
            updateIncrimentExpense(category, expense);
            return;
        }
        if (!Category.isExist(category)) {
            System.out.println("This Category is not a vlid Category!");
            System.out.println("Wan'na make new Category ? [y/n] : ");
            String respond = sc.next();
            if (respond.equalsIgnoreCase("y"))
                Category.addCategory(category);
        }
        if (!isCategoryExistInBudget(category)) {
            if (deletedCategory.containsKey(category)) {
                expense += deletedCategory.get(category);
                deletedCategory.remove(category);
            }
            categoryBudget.put(category, new Budget(budget, expense));
            System.out.println("The category : \'" + category + "\' is Initialized");
        }
    }

    public void updateChangeExpense(String category, double expense) throws InvalidAmountException {
        if (!isCategoryExistInBudget(category)) {
            double amount = (expense >= 0) ? 100 : -(expense + expense);
            setBudgetCategory(category, amount, expense);
        } else {
            if (expense == 0)
                expense = DEFAULT_EXPENSE_FOR_BUDGET;
            if (expense < 0)
                categoryBudget.get(category).setExpenseAmount(expense);
        }
    }

    public void updateIncrimentExpense(String category, double expense) throws InvalidAmountException {
        if (!isCategoryExistInBudget(category)) {
            double amount = (expense >= 0) ? 100 : -(expense + expense);
            setBudgetCategory(category, amount, expense);
        } else {
            if (expense < 0) {
                double oldExpense = getCategoryExpense(category);
                double bgtExpense = (oldExpense == DEFAULT_EXPENSE_FOR_BUDGET) ? expense : oldExpense + expense;
                categoryBudget.get(category).setExpenseAmount(bgtExpense);
            }
        }
    }

    public void updateIncrimentBudget(String category) throws InvalidAmountException {
        updateChangeBudget(category, getCategoryBGT(category) * 1.5);
    }

    public void updateChangeBudget(String category, double newBudget) throws InvalidAmountException {
        if (!isCategoryExistInBudget(category))
            setBudgetCategory(category, newBudget, DEFAULT_EXPENSE_FOR_BUDGET);
        else if (isCategoryExistInBudget(category) && getCategoryBGT(category) != newBudget) {
            categoryBudget.get(category).setBudgetAmount(newBudget);
        }
    }

    public void showBugetExpenceRelationShip(String category) {
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
        result = Math.round(result * 1000.0f) / 1000.0f;
        System.out.println(result);
    }

    public void showCategoryBudget(String category) {
        if (isCategoryExistInBudget(category)) {
            System.out.println(" CATEGORY     BUDGET    EXPENSE ");
            System.out.println("-------------------------------------------------------------");
            double bgtAmount = getCategoryBGT(category);
            double bgtExpense = getCategoryExpense(category);
            System.out.print(" " + category + "       " + bgtAmount + "      " + bgtExpense);
            System.out.print("  (" + getBudgetExpenseRelationInPersenteg(bgtAmount, bgtExpense) + ")  ");
            showBugetExpenceRelationShip(bgtAmount, bgtExpense);
            System.out.println("-------------------------------------------------------------");
        } else
            System.out.println("The Category \'" + category + "\' in Budget List is not exist");
    }

    public void showAllCategorysBudget() {
        if (categoryBudget.size() < 1) {
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
            System.out.print("  (" + getBudgetExpenseRelationInPersenteg(bgtAmount, bgtExpense) + ")  ");
            showBugetExpenceRelationShip(bgtAmount, bgtExpense);
        }
        System.out.println("-----------------------------------------------------------------");
    }

    public void removeBudgetCategory(String category) {
        if (isCategoryExistInBudget(category)) {
            double expense = categoryBudget.get(category).getExpenseAmount();
            if (expense != DEFAULT_EXPENSE_FOR_BUDGET)
                deletedCategory.put(category, expense);
            // if (expense == DEFAULT_EXPENSE_FOR_BUDGET){
            categoryBudget.remove(category);
            // }
            System.out.println("Category " + category + " is removed");
        } else
            System.out.println("The Budget Category is Not exist");
    }

    private float getBudgetExpenseRelationInPersenteg(double budgetAmount, double expenseAmount) {
        float ret = (float) (expenseAmount / budgetAmount) * 100;
        float finalVal = (ret < 0) ? -ret : ret;
        return Math.round(finalVal * 1000.0f) / 1000.0f;
    }

    public void showExpenseByCategory() {
        System.out.println("-------- EXPENSE BY CATEGORY --------\n");
        double totalExpenseAmount = 0;
        for (String category : categoryBudget.keySet()) {
            double currentExpense = categoryBudget.get(category).getExpenseAmount();
            System.out.print(category + "       " + currentExpense);
            double persenteg = getBudgetExpenseRelationInPersenteg(
                    categoryBudget.get(category).getBudgetAmount(), currentExpense);
            System.out.println(" (" + persenteg + ")");
            totalExpenseAmount += currentExpense;
        }
        System.out.println("\n-------------------------------------");
        System.out.println("TOTAL        " + totalExpenseAmount);
    }

    public void adjustDeletedOrExistingExpense(String category, double expenseAmount) throws InvalidAmountException {
        if (isCategoryExistInBudget(category)) {
            double newExpense = getCategoryExpense(category) - expenseAmount;
            updateChangeExpense(category, newExpense);
        } else if (deletedCategory.containsKey(category)) {
            double currentDeleted = deletedCategory.get(category);
            double newDeleted = currentDeleted - expenseAmount;
            if (newDeleted == 0 || newDeleted >= 0) {
                deletedCategory.remove(category);
            } else {
                deletedCategory.put(category, newDeleted);
            }
        }
    }
}
