package model;

import util.InvalidAmountException;

public class Budget {
    private double budgetAmount;
    private double expenseAmount;

    public Budget(double amount, double expenseAmount) throws InvalidAmountException {
        setBudgetAmount(amount);
        setExpenseAmount(expenseAmount);
    }

    // seters
    public void setBudgetAmount(double budget) throws InvalidAmountException {
        if (budget <= 0)
            throw new InvalidAmountException("Budget Amount Should Valid Positive");
        budgetAmount = budget;
    }

    public void setExpenseAmount(double expenseAmount) throws InvalidAmountException {
        if (expenseAmount >= 0)
            throw new InvalidAmountException("Expense are evaluate at Negative");
        this.expenseAmount = expenseAmount;
    }

    // geters
    public double getBudgetAmount() {
        return budgetAmount;
    }

    public double getExpenseAmount() {
        return expenseAmount;
    }

}
