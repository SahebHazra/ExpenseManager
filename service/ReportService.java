
package service;

import model.Expense;
import model.Income;

public class ReportService {
    private int incomeCount;
    private int expenseCount;
    private int highestIncomeId;
    private int highestExpenseId;

    public ReportService() {
        incomeCount = TransactionService.getIncomeCount();
        expenseCount = TransactionService.getExpenseCount();
    }

    public double getTotalIncomeAmount() {
        double totalIncome = 0;
        if (incomeCount == 0)
            return totalIncome;

        double highestIncomeAmount = Double.NEGATIVE_INFINITY;
        for (Income income : TransactionService.getAllIncomeRecords().values()) {
            double currentAmount = income.getAmount();
            if (currentAmount >= highestIncomeAmount) {
                highestIncomeAmount = currentAmount;
                highestIncomeId = income.getID();
            }
            totalIncome += currentAmount;
        }
        return totalIncome;
    }

    public double getTotalExpenseAmount() {
        double totalExpense = 0;
        if (expenseCount == 0)
            return totalExpense;

        double highestExpenseAmount = Double.POSITIVE_INFINITY;
        for (Expense expense : TransactionService.getAllExpenseRecords().values()) {
            double currentAmount = expense.getAmount();
            if (currentAmount <= highestExpenseAmount) {
                highestExpenseAmount = currentAmount;
                highestExpenseId = expense.getID();
            }
            totalExpense += expense.getAmount();
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

        if (incomeCount == 0.0 && expenseCount == 0.0) {
            System.out.println("There were no record");
            return;
        }
        if (incomeCount > 0 || expenseCount > 0) {

            double income = getTotalIncomeAmount();
            double expense = getTotalExpenseAmount();

            System.out.println("\n------------------------------------------");
            System.out.println("| Total Income  ->  " + income);
            System.out.println("| Total Expence -> " + expense);
            System.out.println("------------------------------------------");
            System.out.println("|  Cal. Balance -> " + (income + expense));
            System.out.println("------------------------------------------\n");
        }
        if (incomeCount > 0) {
            System.out.println("Highest Income : ");
            showHighestIncomeDetails();
        }
        if (expenseCount > 0) {
            System.out.println("Highest Expense : ");
            showHighestExpenseDetails();
        }

        System.out.println();
        System.out.println("Transactions: ");
        System.out.println("Incomes: " + incomeCount);
        System.out.println("Expenses: " + expenseCount);
    }
}
