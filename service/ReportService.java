package service;

import model.Expense;
import model.Income;

public class ReportService {
    private static int incomeCount;
    private static int expenseCount;
    private static int highestIncomeId;
    private static int highestExpenseId;

    public static double getTotalIncomeAmount() {
        double totalIncome = 0;
        double highestAmount = 0;
        double currentAmount;
        for (Income income : TransactionService.getAllIncomeRecords().values()) {
            currentAmount = income.getAmount();
            if (highestAmount <= currentAmount) {
                highestAmount = currentAmount;
                highestIncomeId = income.getID();
            }
            totalIncome += currentAmount;
            incomeCount++;
        }
        return totalIncome;
    }

    public static double getTotalExpenseAmount() {
        double totalExpense = 0;
        double highestExpense = 0;
        for (Expense expense : TransactionService.getAllExpenseRecords().values()) {
            double currentAmount = expense.getAmount();
            if (currentAmount >= highestExpense) {
                highestExpense = currentAmount;
                highestExpenseId = expense.getID();
            }
            totalExpense += currentAmount;
            expenseCount++;
        }
        return totalExpense;
    }

    private static void showHighestIncomeDetails() {
        TransactionService.searchDetailsByID(highestIncomeId);
    }

    private static void showHighestExpenseDetails() {
        TransactionService.searchDetailsByID(highestExpenseId);
    }

    public static void generateReport() {
        double income = getTotalIncomeAmount();
        double expense = getTotalExpenseAmount();
        if (income == 0 && expense == 0) {
            System.out.println("There were no record");
            return;
        }
        System.out.println("------------------------------------------");
        System.out.println("| Total Income        ->     " + income + "   |");
        System.out.println("| Total Expence       ->    " + expense + "   |");
        System.out.println("------------------------------------------");
        System.out.println("| Calculated Balance  ->    " + (income - expense) + "   |");
        System.out.println("------------------------------------------");

        System.out.println();
        System.out.println("Highest Income : ");
        showHighestIncomeDetails();
        System.out.println("Highest Expense : ");
        showHighestExpenseDetails();

        System.out.println();
        System.out.println("Transactions: ");
        System.out.println("Incomes: " + incomeCount);
        System.out.println("Expenses: " + expenseCount);
    }
}
