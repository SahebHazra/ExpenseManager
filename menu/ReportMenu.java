package menu;

import java.util.Scanner;

import service.ReportService;
import util.InputValidator;

public class ReportMenu {

    public static void showMenu() {
        System.out.println("\n=========== Transaction MENU ===========");
        System.out.println("1 : Generate Report");
        System.out.println("5 : Back");
    }

    public static void implementation(Scanner sc) throws Exception {
        ReportService repoServ = new ReportService();
        while (true) {
            System.out.print("\nChoose: ");
            int option = InputValidator.getValidInt(sc);
            if (option == 1) {
                repoServ.generateReport();
            } else if (option == 5) {
                System.out.println("Back to Main-Menu!");
                return;
            } else
                System.out.println("Invalid option, Enter between [1-5]");
            showMenu();
        }
    }
}
