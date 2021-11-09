import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Terminal terminal = new Terminal() ;
        Scanner input = new Scanner(System.in) ;
        String x = input.nextLine();
        terminal.chooseCommandAction(x.trim());
    }
}
