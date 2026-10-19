package model;

import util.InvalidAmountException;

public class Budget {
    private double budgetAmount;

    public Budget(double budgetAmount) throws InvalidAmountException {
        setBudgetAmount(budgetAmount);
    }

    // seters
    public void setBudgetAmount(double budget) throws InvalidAmountException {
        if (budget <= 0)
            throw new InvalidAmountException("Budget Amount Should Valid Positive");
        budgetAmount = budget;
    }

    // geters
    public double getBudgetAmount() {
        return budgetAmount;
    }
}
