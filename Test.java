import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Runtime;

public class Test {
    public static void main(String[] args) {
        try {
            File myObj = new File("test.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }

            FileWriter myWriter = new FileWriter("test.txt");
            int idx = new Integer(args[1]);
            int offset = new Integer(args[2]);
            int i = 0;
            while (i < new Integer(args[0])) {
                String hash = "";
                try {
                    hash = Hash.hash(idx + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                myWriter.write(hash + "\n");
                i++;
                idx += offset;
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            Dispatcher.main(new String[] { "test.txt",  Integer.toString(Runtime.getRuntime().availableProcessors()) });
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
