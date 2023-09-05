import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("1. Login or 2. Register");
        String choice = input.nextLine();
        LoginSignupManagement obj = new LoginSignupManagement();
        if (choice.equals("1")){
        obj.login();
        } else if (choice.equals("2")) {
            obj.signUp();
        }
    }
}