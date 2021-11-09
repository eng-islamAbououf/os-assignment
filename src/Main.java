import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Terminal terminal = new Terminal() ;
        Scanner input = new Scanner(System.in) ;
        String x ;
        while (true){
            System.out.print(">");
            x = input.nextLine();
            if (x.equalsIgnoreCase("exit"))
                break;
            terminal.chooseCommandAction(x.trim());
        }

    }
}
