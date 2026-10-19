package model;

import java.time.LocalDate;
import util.InvalidAmountException;

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

    abstract boolean isValidAmount(double amount) throws InvalidAmountException;

    public void setAmount(double amount) throws InvalidAmountException {
        if (isValidAmount(amount))
            this.amount = amount;
    }

    public void setDate(String date) {
        this.date = LocalDate.parse(date);
    }

    public void setNote(String note) {
        if (note == null || note.isEmpty())
            note = "------";
        this.note = note;
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
