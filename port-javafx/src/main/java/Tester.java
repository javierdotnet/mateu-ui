import java.io.File;

public class Tester {


    public static void main(String[] args) {
        File theDir = new File("aaa/bbb/new folder");

// if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("creating directory: " + theDir.getName());
            boolean result = false;

            try{
                theDir.mkdirs();
                result = true;
            }
            catch(SecurityException se){
                //handle it
            }
            if(result) {
                System.out.println("DIR created");
            }
        } else System.out.println(theDir.getAbsolutePath());
    }
}
