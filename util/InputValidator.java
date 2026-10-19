package util;

import java.util.Scanner;

public class InputValidator {

    public static int getValidInt(Scanner sc) {
        try {
            int validNum = Integer.parseInt(sc.next());
            return validNum;
        } catch (Exception e) {
            return 0;
        }
    }

    public static Double getValidDouble(Scanner sc) {
        try {
            double validNum = Double.parseDouble(sc.next());
            return validNum;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static long getValidLong(Scanner sc) {
        try {
            long validLong = Long.parseLong(sc.next());
            return validLong;
        } catch (Exception e) {
            return 0;
        }
    }

}
