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
    public boolean isCategoryExistInBudget(String category) {
        return categoryBudget.containsKey(category);
    }

    public double getCategoryBGT(String category) {
        if (isCategoryExistInBudget(category))
            return categoryBudget.get(category).getBudgetAmount();
        return 0.0;
    }

    public void setBudgetCategory(String category, double budget) throws InvalidAmountException {
        if (isCategoryExistInBudget(category)) {
            System.out.println("The category alredy exist");
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
            categoryBudget.put(category, new Budget(budget));
            System.out.println("The category : \'" + category + "\' is Initialized");
        }
    }

    public void updateIncrimentBudget(String category) throws InvalidAmountException {
        updateChangeBudget(category, getCategoryBGT(category) * 1.5);
    }

    public void updateChangeBudget(String category, double newBudget) throws InvalidAmountException {
        if (!isCategoryExistInBudget(category)) {
            System.out.println("Budget plan was not created for this Category");
        } else if (isCategoryExistInBudget(category) && getCategoryBGT(category) != newBudget) {
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
        System.out.println(result);
    }

    private double getBudgetExpenseRelationInPersenteg(double budgetAmount, double expenseAmount) {
        double ret = (expenseAmount / budgetAmount) * 100;
        return (ret < 0) ? -ret : ret;
    }

    public void showCategoryBudget(String category) {
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
            System.out.print("  (" + getBudgetExpenseRelationInPersenteg(bgtAmount, bgtExpense) + "%)  ");
            showBugetExpenceRelationShip(bgtAmount, bgtExpense);
        }
        System.out.println("-----------------------------------------------------------------");
    }

    public void removeBudgetCategory(String category) {
        if (isCategoryExistInBudget(category)) {
            categoryBudget.remove(category);
            System.out.println("Category " + category + " is removed");
        } else
            System.out.println("The Budget Category is Not exist");
    }

}
