import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Terminal {
    Parser parser ;
    File currentDir ;
    //File homeDir ;
    Path currentPath ;


    public Terminal() {
        this.parser = new Parser();
        currentPath = Paths.get("").toAbsolutePath() ;
       // homeDir = new File(System.getProperty("user.home"));
        currentDir = new File(currentPath.toString()) ;
    }


    //Implement each command in a method, for example:
    public String pwd(){

        return currentPath.toString();
    }
    public void echo(String x){

        System.out.println(x);;
    }
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
        File f = new File(sourcePath);
        if(!f.isAbsolute()) {
            f = new File(currentDir.getAbsolutePath(), sourcePath);
        }
        return f.getAbsoluteFile();
    }
    //deletes file given specific path
    public void rm(String sourcePath) throws IOException, NoSuchFileException {
        File f = makeAbsolute(sourcePath);
        if(!f.exists())
            throw new NoSuchFileException(sourcePath,null,"no such file!");
        else if(f.isDirectory())
            throw new IOException("Cannot delete directory.");
        else if (!f.delete())
            throw  new IOException("Cannot delete file.");
    }

    public void lsReverse(){
        File file = new File(currentDir.getAbsolutePath());

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
            String parent = currentDir.getParent();
            File f = new File(parent);
            currentDir = f.getAbsoluteFile();
        }
        else{
            File f = makeAbsolute(sourcePath);
            if(!f.exists()){
                throw new NoSuchFileException(f.getAbsolutePath(),null,"does not exist");
            }
            if(f.isFile()){
                throw new IOException("Can't cd into file");
            }
            else currentDir = f.getAbsoluteFile();
        }
        currentPath=currentDir.toPath();
    }

    //changes into default directory
    public void cd(){

        currentDir = new File(System.getProperty("user.home"));
        currentPath = currentDir.toPath();
    }

    //creates a new directory with the given name in a given directory
    public void mkdir(String newDir)throws NoSuchFileException,IOException{
        File f = makeAbsolute(newDir);
        if(!f.getParentFile().exists())
            throw new NoSuchFileException(newDir,null,"does not exist.");
        if(f.exists())
            throw new IOException("Directory already exists.");
        boolean created = f.mkdir();
        if(!created)
            throw new IOException("Cannot create directory.");
    }

    //deletes empty directory
    public void rmdir(String sourcePath)throws DirectoryNotEmptyException,NoSuchFileException,IOException{
        File f = makeAbsolute(sourcePath);
        if(!f.exists())
            throw new NoSuchFileException(f.getAbsolutePath(),null,"does not exist");
        if(f.isFile())
            throw new IOException("Cannot delete file");
        else if(!f.delete())
            throw new DirectoryNotEmptyException("Cannot delete non-empty directory.");
    }

    //concatenates files(?) and displays their content
    public void cat(String f1) throws NoSuchFileException,IOException {
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
            throw new NoSuchFileException(file.getAbsolutePath(),null,"does not exist");
    }

    public void cat(String source,String dest)throws IOException
    {
        FileInputStream instream = null;
        FileOutputStream outstream = null;

        File infile = makeAbsolute(source);
        File outfile = makeAbsolute(dest);
        if(!infile.exists() || !outfile.exists())
            throw new IOException("No such file exists.");
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
            }else if (parser.getCommandName().equalsIgnoreCase("cp")){
                echo(parser.getArgs()[0]);
            }else if (parser.getCommandName().equalsIgnoreCase("cp -r")){
                echo(parser.getArgs()[0]);
            }
        } else {
            System.out.println("flase");
        }

    }

}
