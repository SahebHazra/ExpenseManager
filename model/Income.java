package model;

import util.InvalidAmountException;
import util.NullPointerHandler;

public class Income extends Transaction {

    private String source;

    public Income(int ID, double amount,String date, String source, String note)
            throws InvalidAmountException {
        super(ID, amount, date, note);
        setSource(source);
    }

    @Override
    public void setAmount(Double amount) throws InvalidAmountException {
        if (amount < 0)
            throw new InvalidAmountException("Income Should in Positive");
        super.setAmount(amount);
    }

    public void setSource(String source) {
        this.source = NullPointerHandler.perform(source);
    }

    public String getSource() {
        return source;
    }

}
