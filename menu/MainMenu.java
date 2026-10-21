package menu;

import java.util.Scanner;
import util.*;
import service.TransactionService;

public class MainMenu {
    private static boolean isEnd;

    public static boolean isEnd() {
        return isEnd;
    }

    public static void showHeader() {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║ PERSONAL EXPENSE MANAGER     ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println();
        System.out.println("Version v1 -> Non-Presist simple");
        // showBody();
    }

    public static void showBody() {
        System.out.println("\n=========== MAIN MENU ===========");
        System.out.println("1 : Add income");
        System.out.println("2 : Add expense");
        System.out.println("3 : Transaction Records");
        System.out.println("4 : Budget Services");
        System.out.println("5 : Report Menu ");
        System.out.println("6 : Exit");
    }

    public static void implementation(Scanner sc) throws InvalidAmountException {
        TransactionService TRS = new TransactionService(sc);
        System.out.print("\nChoose: ");
        int option = InputValidator.getValidInt(sc);
        System.out.println();
        if (option == 1) {
            TRS.addIncomeRecords();
        } else if (option == 2) {
            TRS.addExpenseRecords();
        } else if (option == 3) {
            TransactionRecordsMenu.showMenu();
            TransactionRecordsMenu.implementation(sc);
        } else if (option == 4) {
            BudgetRecordsMenu.showMenu();
            BudgetRecordsMenu.implementation(sc);
        } else if (option == 5) {
            ReportMenu.showMenu();
            ReportMenu.implementation(sc);
        } else if (option == 6) {
            System.out.println("See you next time!");
            isEnd = true;
            return;
        } else
            System.out.println("Please Enter between [1-6]");
    }

}