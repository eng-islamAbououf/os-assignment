import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Scanner;

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

    protected boolean hasExtension(String src) {
        return src.lastIndexOf('.') != -1;
    }

    protected void echo(String x){
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
        if (status){
            for(String str : myFileList) {
                System.out.println(str);
            }
        }
        else {
            for(int i= myFileList.length-1; i>=0 ; i--) {
                System.out.println(myFileList[i]);
            }
        }
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

    public void mkdir(String[] args) throws IOException {
        File f = currentDir ;
        boolean x = true ;
        if (args[args.length-1].contains(":\\")){
            changeDirectory(args[args.length-1]);
            x = false ;
        }
        for (int i =0 ; i < args.length-1 ; i++){
            createDir(args[i]);
        }
        if (x)
            createDir(args[args.length-1]);
        currentDir = f ;
    }

    //deletes empty directory
    public void rmdir(String sourcePath)throws IOException{
        File myFile = makeAbsolute(sourcePath);
        if(!myFile.exists())
            throw new IOException(myFile.getAbsolutePath()+" does not exist");
        if(myFile.isFile())
            throw new IOException("Cannot delete file");
        else if(!myFile.delete())
            throw new IOException("Cannot delete non-empty directory.");
    }

    public void touch(String source) throws IOException {
        if (!hasExtension(source))
            source += ".txt";
        File src = makeAbsolute(source);
        if(src.exists())
            throw new IOException("Directory already exists.");
        if(src.createNewFile())
            System.out.println(src.getAbsolutePath().substring(src.getAbsolutePath().lastIndexOf('\\')+1) + " created!!");
    }

    public void cp(String sourcePath, String destinationPath )throws IOException{
        File src = makeAbsolute(sourcePath);
        if(!src.exists())
            throw new IOException(src.getAbsolutePath()+" does not exist");
        File dst = makeAbsolute(destinationPath);
        if(!dst.exists()){
            if(dst.isDirectory())
                throw new IOException(dst.getAbsolutePath()+" does not exist");
        }
        else
            Files.copy(src.toPath(),dst.toPath().resolve(src.toPath().getFileName()),StandardCopyOption.REPLACE_EXISTING);
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
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                cpr(srcFile,destFile);
            }

        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }

    //deletes file given specific path
    public void remove(String sourcePath) throws IOException{
        File myFile = makeAbsolute(sourcePath);
        if(!myFile.exists())
            throw new IOException("no such file!");
        else if(myFile.isDirectory())
            throw new IOException("Cannot delete directory.");
        else if (!myFile.delete())
            throw  new IOException("Cannot delete file.");
    }

    public void cat(String f1) throws IOException {
        File file = makeAbsolute(f1);
        if(file.exists()) {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
        }
        else
            throw new NoSuchFileException(file.getAbsolutePath()+" does not exist");
    }

    public void cat(String source,String dest)throws IOException
    {
        FileInputStream reader;
        FileOutputStream writer ;

        File infile = makeAbsolute(source);
        File outfile = makeAbsolute(dest);
        if(!infile.exists() || !outfile.exists())
            throw new IOException("No such file exists.");
        reader = new FileInputStream(infile);
        writer = new FileOutputStream(outfile,true);

        byte[] buffer = new byte[1024];

        int length;
        while ((length = reader.read(buffer)) > 0)
        {
            writer.write(buffer, 0, length);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(outfile)))
        {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
        reader.close();
        writer.close();
    }


    //This method will choose the suitable command method to be called
    public void chooseCommandAction(String command) throws IOException {
        if (parser.parse(command)){
            if (parser.getCommandName().equalsIgnoreCase("pwd")){

                if (parser.getArgs()[0] == null)
                    System.out.println(pwd());
                else
                    System.out.println(ERROR_MSG_ARG);

            }else if (parser.getCommandName().equalsIgnoreCase("ls")){

                if (parser.getArgs()[0] == null)
                    ls(true);
                else if (parser.getArgs()[0].equalsIgnoreCase("-r"))
                    ls(false);
                else
                    System.out.println(ERROR_MSG_ARG);

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
                if (parser.getArgs().length == 1)
                    cat(parser.getArgs()[0]);
                else if (parser.getArgs().length == 2)
                    cat(parser.getArgs()[0],parser.getArgs()[1]);
                else
                    System.out.println(ERROR_MSG_ARG);
            }else if (parser.getCommandName().equalsIgnoreCase("cp")){
                if (parser.getArgs()[0].equalsIgnoreCase("-r")){
                    if (parser.getArgs().length == 3){
                        cpr(makeAbsolute(parser.getArgs()[1]), makeAbsolute(parser.getArgs()[2]));
                    }else
                        System.out.println(ERROR_MSG_ARG);
                }else {
                    if (parser.getArgs().length == 2){
                        cp(parser.getArgs()[0],parser.getArgs()[1]);
                    }else
                        System.out.println(ERROR_MSG_ARG);
                }
            }else if (parser.getCommandName().equalsIgnoreCase("touch")){
                touch(parser.getArgs()[0]);
            }else if (parser.getCommandName().equalsIgnoreCase("echo")){

                if (parser.getArgs()[0] == null)
                    System.out.println(ERROR_MSG_ARG);
                else
                    echo(parser.getArgs()[0]);
            }
        } else {
            System.out.println(ERROR_MSG_NAME);
        }

    }


    public static void main(String[] args)  {
        Terminal terminal = new Terminal() ;
        Scanner input = new Scanner(System.in) ;
        String x ;
        while (true){
            System.out.print("> ");
            x = input.nextLine();
            if (x.equalsIgnoreCase("exit"))
                break;
            try {
                terminal.chooseCommandAction(x.trim());
            }catch (Exception exception){
                System.out.println(exception.getMessage());
                exception.printStackTrace();
            }
        }

    }

}
