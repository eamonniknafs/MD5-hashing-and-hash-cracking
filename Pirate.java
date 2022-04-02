import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Pirate {
    public ArrayList<ArrayList<String>> treasureChest;

    public Pirate(int threads, long timeout, List<String> hashes) {
        Dispatcher dispatcher = new Dispatcher(hashes);
        treasureChest = dispatcher.dispatch(threads, timeout, true);
        System.out.print(treasureChest.get(0));
        System.out.print(treasureChest.get(1));
    }

    public static void main(String[] args) {
        try {
            List<String> hashes = Files.readAllLines(Paths.get(args[0]));
            int N = Integer.parseInt(args[1]);
            long timeout = -1;
            if (args.length > 2)
                timeout = Long.parseLong(args[2]);
            Pirate pirate = new Pirate(N, timeout, hashes);
        } catch (IOException e) {
            System.out.println("Failed reading file");
        }
    }
}