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
    // private static boolean isCriticalCatagory(String category) {
    // if (category.equalsIgnoreCase("Other"))
    // return true;
    // return false;
    // }

    // public static void removeCategory(String category) {
    // if (isCriticalCatagory(category)) {
    // return;
    // }
    // if (isExist(category)) {
    // types.remove(category);
    // System.out.println("Catagory removed");
    // }
    // }

    // public static void renameCategory(String oldCategory, String newCategory) {
    // if (isCriticalCatagory(oldCategory)) {
    // return;
    // } else if (oldCategory.equals(newCategory)) {
    // return;
    // }
    // if (isExist(newCategory)) {
    // removeCategory(oldCategory);
    // addCategory(newCategory);
    // }
    // }

}
