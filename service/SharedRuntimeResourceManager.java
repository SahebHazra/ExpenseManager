package service;

import java.util.HashMap;
import model.Expense;
import model.Income;

public class SharedRuntimeResourceManager {
    static HashMap<Integer, Income> incomeTracker = new HashMap<>();
    static HashMap<Integer, Expense> expenseTracker = new HashMap<>();

    public String nullPointerHandler(String field) {
        if (field == null || field.isEmpty())
            return "N/A";
        return field;
    }

}
