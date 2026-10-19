package model;
import util.InvalidAmountException;

public class Income extends Transaction {

    private String source;

    public Income(int ID, double amount, String source, String date, String note)
            throws InvalidAmountException {
        super(ID, amount, date, note);
        setSource(source);
    }

    // Method
    @Override
    boolean isValidAmount(double amount) throws InvalidAmountException {
        if (amount <= 0)
            throw new InvalidAmountException("Income Should in Positive");
        return true;
    }

    public void setSource(String source) {
        if (source == null || source.isEmpty())
            source = "------";
        this.source = source;
    }

    public String getSource() {
        return source;
    }

}
