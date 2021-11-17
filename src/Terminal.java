import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Scanner;

import java.util.ArrayList;

class Parser {

    private String commandName ;
    private String[] args;
    private final ArrayList<String> commands = new ArrayList<>();

    public Parser() {
        commandName = "" ;
        addCommands();
    }
    private void addCommands(){
        commands.add("pwd") ;
        commands.add("ls") ;
        commands.add("echo") ;
        commands.add("cd") ;
        commands.add("cat") ;
        commands.add("rm") ;
        commands.add("mkdir") ;
        commands.add("rmdir") ;
        commands.add("touch") ;
        commands.add("cp") ;
    }

    public boolean parse(String input) {
        boolean flag = false ;
        if (input.lastIndexOf(' ') == -1){
            for (String x : commands){
                if (x.equalsIgnoreCase(input)) {
                    commandName = input;
                    args = new String[1];
                    flag = true;
                    break;
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

                    flag = true ;
                    break;
                }
            }
        }

        return flag ;
    }

    private void storeArg(String inp){
        StringBuilder temp = new StringBuilder();
        int index = 0  , i= 0;
        while (index<inp.length()){
            if (inp.charAt(index)==' '){
                args[i] = temp.toString();
                temp = new StringBuilder();
                i++ ;
            }else {
                temp.append(inp.charAt(index));
            }

            index++;
        }
        args[i] = temp.toString();
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


public class Terminal {
    Parser parser ;
    File currentDir ;

    final String ERROR_MSG_ARG = "Error : Invalid Argument" ;
    final String ERROR_MSG_NAME = "Error : Command name Not Found !" ;


    public Terminal() {
        this.parser = new Parser();
        currentDir = new File(Paths.get("").toAbsolutePath().toString()) ;
    }

    //given a relative path changed into absolute, if directory exists
    protected File makeAbsolute(String sourcePath){
        File file = new File(sourcePath);
        if(!file.isAbsolute()) {
            file = new File(currentDir.getAbsolutePath(), sourcePath);
        }
        return file.getAbsoluteFile();
    }

//    protected boolean hasExtension(String src) {
//        return src.lastIndexOf('.') != -1;
//    }

    public void echo(String x){
        System.out.println(x);
    }

    public String pwd(){
        return currentDir.getAbsolutePath();
    }

    public void changeDirectory(){
        currentDir = new File(System.getProperty("user.home"));
    }

    public void changeDirectory(String dest)throws IOException{
        if(dest.equals("..")){
            currentDir = new File(currentDir.getParent()).getAbsoluteFile();
        }
        else{
            File myFile = makeAbsolute(dest);
            if(!myFile.exists()){
                throw new IOException(myFile.getAbsolutePath() + " does not exist");
            }
            if(myFile.isFile()){
                throw new IOException("Can't cd into file");
            }
            else currentDir = myFile.getAbsoluteFile();
        }
    }

    public void ls(boolean status) throws IOException {
        File myFile = new File(currentDir.getAbsolutePath());
        String[] myFileList = myFile.list();
        if (myFileList == null)
            throw new IOException("No such file in this directory !!");
        Arrays.sort(myFileList);
        // true -> print array ascending
        // false -> print array desc
        int size  = 0 ;
        if (status){
            for(String str : myFileList) {
                System.out.print(str + "\t");
                size++ ;
                if (size%6==0)
                    System.out.println();
            }
        }
        else {
            for(int i= myFileList.length-1; i>=0 ; i--) {
                System.out.print(myFileList[i] + "\t");
                size++ ;
                if (size%6==0)
                    System.out.println();
            }
        }
        System.out.println();
    }

    //creates a new directory with the given name in a given directory
    private void createDir(String newDir)throws IOException{
        File myFile = makeAbsolute(newDir);
        if(!myFile.getParentFile().exists())
            throw new IOException(newDir + " does not exist.");
        if(myFile.exists())
            throw new IOException("Directory already exists.");
        boolean created = myFile.mkdir();
        if(!created)
            throw new IOException("Cannot create directory.");
    }

    public void mkdir(String[] args) {
        for (String name : args){
            try {
                createDir(name);

            }catch (IOException exception){
                echo(exception.getMessage());
            }
        }
    }

    private void removeDir(String name ) throws IOException {
        File myFile = makeAbsolute(name);
        if(!myFile.exists())
            throw new IOException(myFile.getAbsolutePath()+" does not exist");
        if(myFile.isFile())
            throw new IOException("Cannot delete file");
        else if(!myFile.delete())
            throw new IOException("Cannot delete non-empty directory.");

    }

    //deletes empty directory
    public void rmdir(String sourcePath)throws IOException{
        if (sourcePath.equalsIgnoreCase("*")){
            File myFile = new File(currentDir.getAbsolutePath());
            String[] myFileList = myFile.list();
            if (myFileList == null)
                throw new IOException("No such file in this directory !!");
            for (String path : myFileList){
                removeDir(path);
            }
        }else {
            removeDir(sourcePath);
        }

    }

    public void touch(String source) throws IOException {
//        if (!hasExtension(source))
//            source += ".txt";
        File src = makeAbsolute(source);
        if(src.exists())
            throw new IOException("Directory already exists.");
        if(src.createNewFile())
            System.out.print("");
    }

    public void cp(String source,String dest)throws IOException
    {
        File infile = makeAbsolute(source);
        File outfile = makeAbsolute(dest);
        if (infile.isDirectory())
            throw new IOException("cp : can't work with directory ; you can use cp -r") ;
        if (!outfile.isDirectory()){
            if(!infile.exists() || !outfile.exists())
                throw new IOException("No such file exists.");
            copyContent(infile, outfile);
        }else {
            File f = currentDir ;
            changeDirectory(dest);
            touch(source);
            outfile = makeAbsolute(source) ;
            copyContent(infile, outfile);
            currentDir = f ;
        }

    }

    private void copyContent(File infile, File outfile) throws IOException {
        FileInputStream reader = new FileInputStream(infile);
        FileOutputStream writer = new FileOutputStream(outfile,true);

        byte[] buffer = new byte[1024];

        int length;
        while ((length = reader.read(buffer)) > 0)
        {
            writer.write(buffer, 0, length);
        }
        reader.close();
        writer.close();
    }

    public void cpr(File src , File dest) throws IOException {

        if(src.isDirectory()){
            if(!dest.exists()){
                dest.mkdir();
            }

            String[] myFileList = src.list();
            if (myFileList == null)
                throw new IOException("No such file in this directory !!");

            for (String file : myFileList) {
                File srcFile = new File(src,file);
                File destFile = new File(dest, file);
                cpr(srcFile,destFile);
            }

        } else {
            copyContent(src, dest);
        }
    }

    //deletes file given specific path
    public void remove(String sourcePath) throws IOException{
        File myFile = makeAbsolute(sourcePath);
        if(!myFile.exists())
            throw new IOException("no such file!");
        else if(myFile.isDirectory())
            throw new IOException("Cannot delete directory. you can use 'rmdir' ");
        else if (!myFile.delete())
            throw  new IOException("Cannot delete file.");
    }

    public void cat(String f1) throws IOException {
        File file = makeAbsolute(f1);
        if (file.isDirectory())
            throw new IOException("cat : " +f1 + " : Is a Directory") ;
        if(file.exists()) {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
            String line;
            while ((line = in.readLine()) != null) {
                echo(line);
            }
            in.close();
        }
        else
            throw new IOException(file.getAbsolutePath()+" does not exist");
    }

    //This method will choose the suitable command method to be called
    public void chooseCommandAction(String command) throws IOException {
        if (parser.parse(command)){
            if (parser.getCommandName().equalsIgnoreCase("pwd")){

                if (parser.getArgs()[0] == null)
                    echo(pwd());
                else
                    echo(ERROR_MSG_ARG);

            }else if (parser.getCommandName().equalsIgnoreCase("ls")){

                if (parser.getArgs()[0] == null)
                    ls(true);
                else if (parser.getArgs()[0].equalsIgnoreCase("-r"))
                    ls(false);
                else
                    echo(ERROR_MSG_ARG);

            }else if (parser.getCommandName().equalsIgnoreCase("cd")){
                if (parser.getArgs()[0] == null){
                    changeDirectory();
                }else {
                    changeDirectory(parser.getArgs()[0]);
                }
            }else if (parser.getCommandName().equalsIgnoreCase("rm")){
                for (String arg : parser.getArgs()){
                    remove(arg);
                }
            }else if (parser.getCommandName().equalsIgnoreCase("mkdir")){
                mkdir(parser.getArgs());
            }else if (parser.getCommandName().equalsIgnoreCase("rmdir")){
                for (String arg : parser.getArgs()){
                    rmdir(arg);
                }
            }else if (parser.getCommandName().equalsIgnoreCase("cat")){

                for (String i : parser.getArgs()){
                    cat(i);
                }
            }else if (parser.getCommandName().equalsIgnoreCase("cp")){
                if (parser.getArgs()[0].equalsIgnoreCase("-r")){
                    if (parser.getArgs().length == 3){
                        cpr(makeAbsolute(parser.getArgs()[1]), makeAbsolute(parser.getArgs()[2]+"\\"+parser.getArgs()[1]));
                    }else
                        echo(ERROR_MSG_ARG);
                }else {
                    if (parser.getArgs().length == 2){
                        cp(parser.getArgs()[0],parser.getArgs()[1]);
                    }else
                        echo(ERROR_MSG_ARG);
                }
            }else if (parser.getCommandName().equalsIgnoreCase("touch")){
                touch(parser.getArgs()[0]);
            }else if (parser.getCommandName().equalsIgnoreCase("echo")){

                if (parser.getArgs()[0] == null)
                    echo(ERROR_MSG_ARG);
                else
                    echo(parser.getArgs()[0]);
            }
        } else {
            echo(ERROR_MSG_NAME);
        }

    }


    public static void main(String[] args)  {
        Terminal terminal = new Terminal() ;
        Scanner input = new Scanner(System.in) ;
        String x ;
        while (true){
            System.out.print("~$ ");
            x = input.nextLine();
            if (x.equalsIgnoreCase("exit"))
                break;
            try {
                terminal.chooseCommandAction(x.trim());
            }catch (Exception exception){
                System.out.println(exception.getMessage());
                //exception.printStackTrace();
            }
        }

    }

}
