import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Scanner;

public class Terminal {
    Parser parser ;
    File currentDirectory;
    Path currentPath ;


    public Terminal() {
        this.parser = new Parser();
        currentPath = Paths.get("").toAbsolutePath() ;
        currentDirectory = new File(currentPath.toString()) ;
    }
    //Implement each command in a method, for example:

    //Display the current path
    public String pwd(){

        return currentPath.toString();
    }

    //Display the given argument
    public void echo(String Input){

        System.out.println(Input);;
    }

    //Display the content of the directory sorted alphabetically
    public void ls(){
        File file = new File(currentPath.toString());

        // returns an array of all files
        String[] fileList = file.list();
        Arrays.sort(fileList);
        for(String str : fileList) {
            System.out.println(str);
        }

    }

    //given a relative path changed into absolute, if directory exists
    public File makeAbsolute(String sourcePath){
        File file = new File(sourcePath);
        if(!file.isAbsolute()) {
            file = new File(currentDirectory.getAbsolutePath(), sourcePath);
        }
        return file.getAbsoluteFile();
    }

    //deletes file given specific path
    public void rm(String sourcePath) throws IOException, NoSuchFileException {
        File file = makeAbsolute(sourcePath);
        if(!file.exists())
            throw new NoSuchFileException(sourcePath,null,"No File Founded.");
        else if(file.isDirectory())
            throw new IOException("Can't Delete the Directory.");
        else if (!file.delete())
            throw  new IOException("Can't Delete the File");
    }

    //Display the content of the directory in reverse order
    public void lsReverse(){
        File file = new File(currentDirectory.getAbsolutePath());

        // returns an array of all files
        String[] fileList = file.list();
        Arrays.sort(fileList);
        for(int i= fileList.length-1; i>=0 ; i--) {
            System.out.println(fileList[i]);
        }
    }


    //changes the current directory to the given one
    public void cd(String sourcePath)throws NoSuchFileException,IOException{
        if(sourcePath.equals("..")){
            String parent = currentDirectory.getParent();
            File file = new File(parent);
            currentDirectory = file.getAbsoluteFile();
        }
        else{
            File file = makeAbsolute(sourcePath);
            if(!file.exists()){
                throw new NoSuchFileException(file.getAbsolutePath(),null,"No File Exist.");
            }
            if(file.isFile()){
                throw new IOException("Can't cd into file.");
            }
            else currentDirectory = file.getAbsoluteFile();
        }
        currentPath= currentDirectory.toPath();
    }

    //changes into default directory
    public void cd(){

        currentDirectory = new File(System.getProperty("user.home"));
        currentPath = currentDirectory.toPath();
    }

    //creates a new directory with the given name in a given directory
    public void mkdir(String newDirectory)throws NoSuchFileException,IOException{
        File file = makeAbsolute(newDirectory);
        if(!file.getParentFile().exists())
            throw new NoSuchFileException(newDirectory,null,"Doesn't Exist.");
        if(file.exists())
            throw new IOException("Directory Already Exists.");
        boolean created = file.mkdir();
        if(!created)
            throw new IOException("Can't Create Directory.");
    }

    //deletes empty directory
    public void rmdir(String sourcePath)throws DirectoryNotEmptyException,NoSuchFileException,IOException{
        File file = makeAbsolute(sourcePath);
        if(!file.exists())
            throw new NoSuchFileException(file.getAbsolutePath(),null,"Doesn't Exist.");
        if(file.isFile())
            throw new IOException("Can't Delete The File");
        else if(!file.delete())
            throw new DirectoryNotEmptyException("Can't Delete a non empty Directory.");
    }
    // Display the content of the file
    public void cat(String fileName) throws NoSuchFileException,IOException {
        File file = makeAbsolute(fileName);
        if(file.exists()) {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsolutePath()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
        }
        else
            throw new NoSuchFileException(file.getAbsolutePath(),null,"Doesn't Exist.");
    }

    //concatenates files and displays their content
    public void cat(String source,String destination)throws IOException
    {
        FileInputStream instream = null;
        FileOutputStream outstream = null;

        File infile = makeAbsolute(source);
        File outfile = makeAbsolute(destination);
        if(!infile.exists() || !outfile.exists())
            throw new IOException("No File Exists.");
        instream = new FileInputStream(infile);
        outstream = new FileOutputStream(outfile,true);

        byte[] buffer = new byte[1024];

        int length;
        while ((length = instream.read(buffer)) > 0)
        {
            outstream.write(buffer, 0, length);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(outfile)))
        {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
        instream.close();
        outstream.close();
    }

    //Copies the first file into the second one
    public void cp(String sourcePath, String destinationPath )throws IOException,NoSuchFileException{
        File source = makeAbsolute(sourcePath);
        if(!source.exists())
            throw new NoSuchFileException(source.getAbsolutePath(),null,"Doesn't Exist.");
        File destination = makeAbsolute(destinationPath);
        if(!destination.exists()){
            if(destination.isDirectory())
                throw new NoSuchFileException(destination.getAbsolutePath(),null,"Doesn't Exist.");
        }
        else
            Files.copy(source.toPath(),destination.toPath().resolve(source.toPath().getFileName()),StandardCopyOption.REPLACE_EXISTING);
    }

    //Create a File from a given path
    public void touch(String source) throws IOException {
        File src = makeAbsolute(source);
        if(src.exists())
            throw new IOException("Directory Exists.");
        if(src.createNewFile())
            System.out.println(src.getAbsolutePath().substring(src.getAbsolutePath().lastIndexOf('\\')+1) + "Created The new File.");
    }

    //Copies the first directory into the second one
    public void cpr(Path source , Path destination) throws IOException {
        Files.copy(source, destination);
    }

    //This method will choose the suitable command method to be called
    public void chooseCommandAction(String command) throws IOException {
        if (parser.parse(command)){
            if (parser.getCommandName().equalsIgnoreCase("pwd")){
                System.out.println(pwd());
            }else if (parser.getCommandName().equalsIgnoreCase("ls")){
                ls();
            }else if (parser.getCommandName().equalsIgnoreCase("ls -r")){
                lsReverse();
            }else if (parser.getCommandName().equalsIgnoreCase("cd")){
                if (parser.getArgs().length == 1){
                    cd() ;
                }else {
                    cd(parser.getArgs()[0]);
                }
            }else if (parser.getCommandName().equalsIgnoreCase("rm")){
                rm(parser.getArgs()[0]);
            }else if (parser.getCommandName().equalsIgnoreCase("mkdir")){
                mkdir(parser.getArgs()[0]);
            }else if (parser.getCommandName().equalsIgnoreCase("rmdir")){
                rmdir(parser.getArgs()[0]);
            }else if (parser.getCommandName().equalsIgnoreCase("cat")){
                if (parser.getArgs().length == 2)
                    cat(parser.getArgs()[0]);
                else
                    cat(parser.getArgs()[0],parser.getArgs()[1]);
            }else if (parser.getCommandName().equalsIgnoreCase("cp")){
                cp(parser.getArgs()[0],parser.getArgs()[1]);
            }else if (parser.getCommandName().equalsIgnoreCase("cp -r")){
                cp(parser.getArgs()[0], parser.getArgs()[1]);
            }else if (parser.getCommandName().equalsIgnoreCase("touch")){
                touch(parser.getArgs()[0]);
            }else if (parser.getCommandName().equalsIgnoreCase("echo")){
                echo(parser.getArgs()[0]);
            }
        } else {
            System.out.println("Error : Command name Not Found !");
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
