package util;

public class NullPointerHandler {
    public static String perform(String field) {
        if (field == null || field.isEmpty() || field.isBlank() || field.equalsIgnoreCase("null"))
            return "N/A";
        return field;
    }
}