public class Parser {

    private String commandName ;
    private String args[] ;

    public void parse(String input){
        if (input.equalsIgnoreCase("pwd")
                || input.equalsIgnoreCase("ls")
                || input.equalsIgnoreCase("ls -r")
        )
            commandName = input ;
        else
            commandName = input.substring(0,input.indexOf(' ')) ;
//        int x = getCountOfSpaces(input.substring(input.indexOf(' ')+1)) ;
//        args = new String[x] ;
//        if (x==0){
//
//        }
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
        int count = 1 ;
        for (char c : args.toCharArray()){
            if (c==' ')
                count ++ ;
        }
        return count ;
    }
}
