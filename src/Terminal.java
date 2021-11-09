import java.nio.file.Paths;

public class Terminal {

    Parser parser ;
    //Implement each command in a method, for example:
    public static String pwd(){

        return Paths.get("").toAbsolutePath().toString();
    }
    public void cd(String[] args){

    }


    //This method will choose the suitable command method to be called
    public void chooseCommandAction(){

    }

    public static void main(String[] args) {
        System.out.println(pwd());
    }
}
