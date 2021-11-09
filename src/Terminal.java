import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

public class Terminal {
    Parser parser ;

    public Terminal() {
        this.parser = new Parser();
    }


    //Implement each command in a method, for example:
    public static String pwd(){

        return Paths.get("").toAbsolutePath().toString();
    }
    public void ls(){
        File file = new File(Paths.get("").toAbsolutePath().toString());

        // returns an array of all files
        String[] fileList = file.list();
        Arrays.sort(fileList);
        for(String str : fileList) {
            System.out.println(str);
        }

    }

    public void lsReverse(){
        File file = new File(Paths.get("").toAbsolutePath().toString());

        // returns an array of all files
        String[] fileList = file.list();
        Arrays.sort(fileList);
        for(int i= fileList.length-1; i>=0 ; i--) {
            System.out.println(fileList[i]);
        }
    }


    //This method will choose the suitable command method to be called
    public void chooseCommandAction(String command){
        parser.parse(command);
        if (parser.getCommandName().equalsIgnoreCase("pwd")){
            System.out.println(pwd());
        }else if (parser.getCommandName().equalsIgnoreCase("ls")){
            ls();
        }else if (parser.getCommandName().equalsIgnoreCase("ls -r")){
            lsReverse();
        }
    }

}
