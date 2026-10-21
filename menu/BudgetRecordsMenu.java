package menu;

import java.util.Scanner;
import service.BudgetService;
import util.InputValidator;
import util.InvalidAmountException;

public class BudgetRecordsMenu {
    public static void showMenu() {
        System.out.println("\n=========== Budget MENU ===========");
        System.out.println("1 : Set Budget Category");
        System.out.println("2 : Show Budget Of a Category");
        System.out.println("3 : Show All Category's Budget");
        System.out.println("4 : Update & Change Category's  Budget");
        System.out.println("5 : Show Expense By Category");
        System.out.println("6 : Remove Budget Category");
        System.out.println("7 : Back");
    }

    public static void implementation(Scanner sc) throws InvalidAmountException {
        BudgetService BGTS = new BudgetService(sc);
        String category;
        double budget;
        while (true) {
            System.out.print("\nChoose: ");
            int option = InputValidator.getValidInt(sc);
            if (option == 1) {
                System.out.print("Category : ");
                category = sc.nextLine();
                System.out.print("BudgetAmount : ");
                budget = InputValidator.getValidDouble(sc);
                BGTS.setBudgetCategory(category, budget);
            } else if (option == 2) {
                System.out.print("Category : ");
                category = sc.nextLine();
                BGTS.showCategoryBudget(category);
            } else if (option == 3) {
                BGTS.showAllCategorysBudget();
            } else if (option == 4) {
                System.out.print("Category : ");
                category = sc.nextLine();
                System.out.print("Budget Amount : ");
                budget = InputValidator.getValidDouble(sc);
                BGTS.updateChangeBudget(category, budget);
            } else if (option == 5) {
                BudgetService.showAllCategoryExpense();
            } else if (option == 6) {
                System.out.print("Category : ");
                category = sc.nextLine();
                BGTS.removeBudgetCategory(category);
            } else if (option == 7) {
                System.out.println("Back to Main-Menu!");
                return;
            } else
                System.out.println("Invalid option, Enter between [1-7]");
            showMenu();
        }
    }
}
