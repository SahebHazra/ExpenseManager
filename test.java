import model.Expense;
import model.Income;
import service.*;
import util.InvalidAmountException;
import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        try {
            // Income income = new Income(1, 1000, "Salary", "2023-06-01",
            // "Monthly_Salary");
            Income income = new Income(0, 0, null, "2023-06-01", null);
            System.out.println("Income created successfully.");
            System.out.println("ID: " + income.getID());
            System.out.println("Amount: " + income.getAmount());
            System.out.println("Source: " + income.getSource());
            System.out.println("Date: " + income.getDate());
            System.out.println("Note: " + income.getNote());
        } catch (InvalidAmountException e) {
            System.out.println("Error creating Income: " + e.getMessage());
        }

        try {
            // Expense expense = new Expense(2, -500, "Food", "2023-06-02", "Lunch");
            Expense expense = new Expense(0, 0, null, "2023-06-01", null);
            System.out.println("\nExpense created successfully.");
            System.out.println("ID: " + expense.getID());
            System.out.println("Amount: " + expense.getAmount());
            System.out.println("Category: " + expense.getCategory());
            System.out.println("Date: " + expense.getDate());
            System.out.println("Note: " + expense.getNote());
        } catch (InvalidAmountException e) {
            System.out.println("Error creating Expense: " + e.getMessage());
        }

    }
}
