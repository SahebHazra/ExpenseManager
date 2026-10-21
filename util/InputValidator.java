package util;

import java.util.Scanner;

public class InputValidator {

    public static int getValidInt(Scanner sc) {
        while (true) {
            try {
                int validNum = Integer.parseInt(sc.nextLine());
                return validNum;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                System.out.print("Enter again: ");
            }

        }
    }

    public static Double getValidDouble(Scanner sc) {
        while (true) {
            try {
                double validNum = Double.parseDouble(sc.nextLine());
                return validNum;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid double.");
                System.out.print("Enter again: ");
            }
        }

    }

    public static long getValidLong(Scanner sc) {
        while (true) {
            try {
                long validLong = Long.parseLong(sc.nextLine());
                return validLong;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid long.");
                System.out.print("Enter again: ");
            }
        }
    }

}
