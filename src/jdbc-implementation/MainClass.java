package jdbc-implementation;
import java.io.IOException;

public class MainClass {

    static private DatabaseInterface di = null;
  
    public static void main(String[] args) {
        di = new DatabaseInterface();
        di.createDatabase();
        TransfileProcessor tp = new TransfileProcessor ("transfile", di);
        try {
            tp.openFile();
        }
        catch (IOException e) {
            System.out.println(e);
        }
        di.dropSchema();
    }
}
