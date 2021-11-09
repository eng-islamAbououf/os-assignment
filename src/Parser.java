public class Parser {

    private String commandName ;
    private String args[] ;

    public boolean parse(String input){
        return true ;
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
}
