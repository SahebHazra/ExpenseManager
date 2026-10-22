import menu.*;
import java.util.Scanner;
import util.InvalidAmountException;

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
