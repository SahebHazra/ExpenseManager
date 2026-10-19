package model;
import java.time.LocalDate;
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
    public void setAmount(double amount) throws InvalidAmountException {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = LocalDate.parse(date);
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
