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
                System.out.println(e);
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println(e);
                sc.nextLine();
                e.printStackTrace();
                break;
            }
        } while (!MainMenu.isEnd());
    }
}
