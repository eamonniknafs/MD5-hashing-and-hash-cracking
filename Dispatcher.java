import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
public class Dispatcher {
    Queue<String> WorkQueue = new LinkedList<String>();
    BlockingQueue<Runnable> WorkerQueue = new ArrayBlockingQueue<Runnable>(100);
    ThreadPoolExecutor executor;
    public void fillQueue(List<String> hashes) {
        for (Object hash : hashes) {
            WorkQueue.add((String) hash);
        }
    }
    public void dispatch(int threads) {
        executor = new ThreadPoolExecutor(threads, threads, 0L,
                java.util.concurrent.TimeUnit.MILLISECONDS, WorkerQueue);
        while (!WorkQueue.isEmpty()) {
            executor.execute(new UnHash(WorkQueue.poll()));
        }
        executor.shutdown();
    }
    public static void main(String[] args) {
        try {
            List<String> hashes = Files.readAllLines(Paths.get(args[0]));
            int N = Integer.parseInt(args[1]);
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.fillQueue(hashes);
            dispatcher.dispatch(N);
            // for (String line : lines) {
            //     System.out.println(UnHash.unhash(line));
            // }
        } catch (IOException e) {
            System.out.println("Failed reading file");
        }
    }
}