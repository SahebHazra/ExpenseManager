package model;

import java.util.HashSet;
import java.util.List;

abstract public class Category {

    private static final HashSet<String> types = new HashSet<>(
            List.of("Food", "Transport", "Education", "Entertainment",
                    "Shopping", "Shopping", "Bills", "Health", "Technology", "Other"));

    public static boolean isExist(String category) {
        category = normalize(category);
        if (types.contains(category))
            return true;
        return false;
    }

    public static String normalize(String unFormatedCategory) {
        String str = unFormatedCategory;
        if (str == null || str.isEmpty() || str.isBlank())
            return "";
        str = str.strip();
        String startStr = str.substring(0,1);
        String remnStr = str.substring(1);
        // if (startStr == startStr.toUpperCase() && remnStr == remnStr.toLowerCase()){
        // return str; }
        return startStr.toUpperCase() + remnStr.toLowerCase();
    }

    public static boolean addCategory(String category) {
        category = normalize(category);
        if (!isExist(category) && !category.isEmpty()) {
            types.add(category);
            System.out.println("Category Added");
            return true;
        }
        return false;
    }

    public static HashSet<String> getAllCategorys() {
        return new HashSet<>(types);
    }
}
