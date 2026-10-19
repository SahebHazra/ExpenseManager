package model;

import java.util.HashSet;
import java.util.List;

abstract public class Category {

    private static HashSet<String> types = new HashSet<>(List.of("Food", "Transport", "Education", "Entertainment",
            "Shopping", "Shopping", "Bills", "Health", "Technology", "Other"));

    public static boolean isExist(String category) {
        if (types.contains(category))
            return true;
        return false;
    }

    public static void addCategory(String category) {
        if (!isExist(category)) {
            types.add(category);
            System.out.println("Catagory Added");
        }
    }

    public static HashSet<String> getAllCategorys() {
        return types;
    }
}
