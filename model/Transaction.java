package model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import util.InvalidAmountException;
import util.NullPointerHandler;

abstract public class Transaction {
    private int ID;
    private double amount;
    private LocalDate date;
    private String note;

    public Transaction(int ID, double amount, String dateStr, String note) throws InvalidAmountException {
        setID(ID);
        setAmount(amount);
        setDate(dateStr);
        setNote(note);
    }

    // Seters
    private void setID(int ID) {
        this.ID = ID;
    }

    public void setAmount(Double amount) throws InvalidAmountException {
        if (amount.isNaN() || amount.isInfinite())
            throw new InvalidAmountException("This is an Invalid Amount!");
        else if (amount == 0)
            throw new InvalidAmountException("Amount Should not be Zero!");
        this.amount = amount;
    }

    public void setDate(String date) {
        // If the date is invalid bychance , set it to the current date
        try {
            this.date = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            this.date = LocalDate.now();
        }

    }

    public void setNote(String note) {
        this.note = NullPointerHandler.perform(note);
    }

    // Geaters
    public int getID() {
        return ID;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }
}
