import java.util.ArrayList;

public class Parser {

    private String commandName ;
    private String args[] ;
    private ArrayList<String> commands = new ArrayList<>();

    public Parser() {
        commandName = "" ;
        addCommands();
    }
    private void addCommands(){
        commands.add("pwd") ;
        commands.add("ls") ;
        //commands.add("ls -r") ;
        commands.add("echo") ;
        commands.add("cd") ;
        commands.add("cat") ;
        commands.add("rm") ;
        commands.add("mkdir") ;
        commands.add("rmdir") ;
        commands.add("touch") ;
        commands.add("cp") ;
        //commands.add("cp -r") ;
    }

    public boolean parse(String input) {
        boolean s = false ;
        if (input.lastIndexOf(' ') == -1){
            for (String x : commands){
                if (x.equalsIgnoreCase(input)){
                    commandName = input ;
                    args = new String[1] ;
                    s = true ;
                }
            }

        } else {
            commandName = input.substring(0,input.indexOf(' ')) ;
            for (String x : commands){
                if (x.equalsIgnoreCase(commandName))
                {
                    if (commandName.equalsIgnoreCase("echo")){
                        args = new String[1] ;
                        args[0] = input.substring(input.indexOf(' ')+1) ;
                    }else{
                        args = new String[getCountOfSpaces(input)] ;
                        storeArg(input.substring(input.indexOf(' ')+1));
                    }

                    s = true ;
                    break;
                }
            }
        }

        return s ;
    }

    private void storeArg(String inp){
        String temp = "" ;
        int index = 0  , i= 0;
        while (index<inp.length()){
            if (inp.charAt(index)==' '){
                args[i] = temp ;
                //System.out.println(args[i]);
                temp = "" ;
                i++ ;
            }else {
                temp += inp.charAt(index) ;
            }

            index++;
        }
        args[i] = temp ;
    }


    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
    private int getCountOfSpaces(String args){
        int count = 0 ;
        for (char c : args.toCharArray()){
            if (c==' ')
                count ++ ;
        }
        return count ;
    }
}
