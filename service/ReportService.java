package service;

import model.Expense;
import model.Income;

public class ReportService {
    private int incomeCount;
    private int expenseCount;
    private int highestIncomeId;
    private int highestExpenseId;

    public double getTotalIncomeAmount() {
        double totalIncome = 0;
        for (Income income : TransactionService.getAllIncomeRecords().values()) {
            double lastAmount = 0;
            double currentAmount = income.getAmount();
            if (lastAmount <= currentAmount) {
                lastAmount = currentAmount;
                highestIncomeId = income.getID();
            }
            totalIncome += currentAmount;
            incomeCount++;
        }
        return totalIncome;
    }

    public double getTotalExpenseAmount() {
        double totalExpense = 0;
        for (Expense expense : TransactionService.getAllExpenseRecords().values()) {
            double lastAmount = 0;
            double currentAmount = expense.getAmount();
            if (lastAmount >= currentAmount) {
                lastAmount = currentAmount;
                highestExpenseId = expense.getID();
            }
            totalExpense += expense.getAmount();
            expenseCount++;
        }
        return totalExpense;
    }

    private void showHighestIncomeDetails() {
        TransactionService.searchDetailsByID(highestIncomeId);
    }

    private void showHighestExpenseDetails() {
        TransactionService.searchDetailsByID(highestExpenseId);
    }

    public void generateReport() {
        double income = getTotalIncomeAmount();
        double expense = getTotalExpenseAmount();
        if (income <= 0 && expense >= 0) {
            System.out.println("There were no record");
            return;
        }
        if (income <= 0 || expense >= 0) {
            System.out.println("\n------------------------------------------");
            System.out.println("| Total Income        ->     " + income + "   |");
            System.out.println("| Total Expence       ->    " + expense + "   |");
            System.out.println("------------------------------------------");
            System.out.println("| Calculated Balance  ->    " + (income + expense) + "   |");
            System.out.println("------------------------------------------\n");
        }
        if (income > 0) {
            System.out.println("Highest Income : ");
            showHighestIncomeDetails();
        }
        if (expense < 0) {
            System.out.println("Highest Expense : ");
            showHighestExpenseDetails();
        }

        System.out.println();
        System.out.println("Transactions: ");
        System.out.println("Incomes: " + incomeCount);
        System.out.println("Expenses: " + expenseCount);
    }
}
