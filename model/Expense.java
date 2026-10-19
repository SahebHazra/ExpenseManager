package model;

import util.InvalidAmountException;

public class Expense extends Transaction {

    private String category;

    public Expense(int ID, double amount, String category, String date, String note)
            throws InvalidAmountException {
        super(ID, amount, date, note);
        setCategory(category);
    }

    @Override
    boolean isValidAmount(double amount) throws InvalidAmountException {
        if (amount <= 0)
            throw new InvalidAmountException("Expense amount must be positive");
        return true;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

}