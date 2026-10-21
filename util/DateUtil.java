package util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateUtil {

    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr);
            return true;
        } catch (DateTimeParseException e) {
            System.out.print("Invalid Date");
        }
        return false;
    }

    public static boolean isValidDateV2(String dateStr) {
        if (dateStr.length() != 10 || dateStr.charAt(4) != '-' || dateStr.charAt(7) != '-')
            return false;
        Integer year = Integer.valueOf(dateStr.substring(0, 4));
        Integer month = Integer.valueOf(dateStr.substring(5, 7));
        Integer day = Integer.valueOf(dateStr.substring(8, dateStr.length() - 1));
        // Check if the date is valid or not | Highest year is 10 years from now and lowest year is 100 years from now
        if (year > LocalDate.now().getYear() + 10 || month > 12 || day > 31)
            return false;
        if (year < LocalDate.now().getYear() - 100 || month < 1 || day < 1)
            return false;
        if (month == 2) {
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                if (day == 29)
                    return true;
            } else if (day == 28)
                return true;
        } else if (month == 8 && day == 31)
            return true;
        else if (month % 2 == 0 && day == 30)
            return true;
        else if (month % 2 != 0 && day == 31)
            return true;

        return false;
    }

}
