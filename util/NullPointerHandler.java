package util;

public class NullPointerHandler {
    public static String perform(String field) {
        if (field == null || field.isEmpty())
            return "N/A";
        return field;
    }
}