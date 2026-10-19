package model;

import util.InvalidAmountException;
import util.NullPointerHandler;

public class Expense extends Transaction {

    private String category;

    public Expense(int ID, double amount, String category, String date, String note)
            throws InvalidAmountException {
        super(ID, amount, date, note);
        setCategory(category);
    }

    @Override
    public void setAmount(double amount) throws InvalidAmountException {
        amount = Math.abs(amount) * -1; // Ensure the amount is negative
        super.setAmount(amount);
    }

    public void setCategory(String category) {
        this.category = NullPointerHandler.perform(category);
    }

    public String getCategory() {
        return category;
    }

}