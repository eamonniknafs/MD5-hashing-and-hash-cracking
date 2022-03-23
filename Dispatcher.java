import java.util.List;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

public class Dispatcher {

    public static void main(String[] args) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(args[0]));
            for (String line : lines) {
                System.out.println(UnHash.unhash(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}