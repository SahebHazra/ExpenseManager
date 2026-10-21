package menu;

import java.util.Scanner;
import service.TransactionService;
import util.InputValidator;
import util.InvalidAmountException;

public class TransactionRecordsMenu {
    public static void showMenu() {
        System.out.println("\n=========== Transaction MENU ===========");
        System.out.println("1 : View Income Records");
        System.out.println("2 : View Expense Records");
        System.out.println("3 : View All transactions");
        System.out.println("4 : Search Record by ID");
        System.out.println("5 : Edit Record By ID");
        System.out.println("6 : Back");
    }

    public static void implementation(Scanner sc) throws InvalidAmountException{
        TransactionService TRS = new TransactionService(sc);
        while (true) {
            System.out.print("\nChoose: ");
            int option = InputValidator.getValidInt(sc);
            if (option == 1) {
                TRS.showIncomeRecords();
            } else if (option == 2) {
                TRS.showExpenseRecords();
            } else if (option == 3) {
                TRS.showAllRecords();
            } else if (option == 4) {
                System.out.print("Enter ID : ");
                int id = InputValidator.getValidInt(sc);
                TransactionService.searchDetailsByID(id);
            } else if (option == 5) {
                System.out.print("Enter ID : ");
                int ID = InputValidator.getValidInt(sc);
                System.out.println("1: Edit, 2: Delete, AnyKey: Cancel");
                int opt = InputValidator.getValidInt(sc);
                if (opt == 1)
                    TRS.editReordByID(ID);
                else if (opt == 2)
                    TRS.deleteRecordByID(ID);
                else
                    System.out.println("Cancled!");
            } else if (option == 6) {
                System.out.println("Back to Main-Menu!");
                return;
            } else
                System.out.println("Invalid option, Enter between [1-6]");
            showMenu();
        }
    }
}
