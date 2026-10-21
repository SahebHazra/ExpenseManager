import menu.*;
import java.util.Scanner;
import util.InvalidAmountException;



// I want you to test this project as instructed below - you can test it with more parameter  and give me result 
// x/x tested   - x/x passed   - failed : what/when/why

// Add income

// Add income of 1,000. Verify the record and report total.

// Add expenses

// Add Food -100 and Transport -200. Verify category totals.

// Edit an amount

// Change Food from -100 to -150. Food should total -150.

// Change a category

// Move the Food expense to a new category. Both totals should update.

// Delete an expense

// Delete the -200 expense. Its category total should return to zero.

// Generate reports repeatedly

// Generate the report three times. Counts and highest records must remain stable.

// Try invalid input

// Test abc, NaN, Infinity, zero, negative income, and invalid dates.

// Remove a budget

// Remove a budget, then edit and delete a transaction in that category.



public class Main {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        MainMenu.showHeader();
        do {
            MainMenu.showBody();
            try {
                MainMenu.implementation(sc);
            } catch (InvalidAmountException e) {
                System.out.println("An Error Occered : " + e);
            } catch (NumberFormatException e) {
                System.out.println("An Error Occered With Number Format : " + e);
            } catch (Exception e) {
                System.out.println("An UnExpected Error Occered : " + e);
                e.printStackTrace();
                break;
            }
        } while (!MainMenu.isEnd());

    }
}
