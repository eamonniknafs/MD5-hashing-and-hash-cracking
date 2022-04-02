import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
public class Dispatcher {
    Queue<String> WorkQueue = new LinkedList<String>();
    BlockingQueue<Runnable> WorkerQueue;
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    ThreadPoolExecutor executor;

    public Dispatcher(List<String> hashes) {
        WorkerQueue = new ArrayBlockingQueue<Runnable>(hashes.size());
        fillQueue(hashes);
    }

    public void fillQueue(List<String> hashes) {
        for (Object hash : hashes) {
            WorkQueue.add((String) hash);
        }
    }

    public ArrayList<ArrayList<String>> dispatch(int threads, long timeout, Boolean outputList) {
        ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>(2);
        output.add(new ArrayList<String>());
        output.add(new ArrayList<String>());
        executor = new ThreadPoolExecutor(threads, threads, 0L, timeUnit, WorkerQueue);
        if (outputList) {
            while (!WorkQueue.isEmpty()) {
                executor.submit(new UnHash(WorkQueue.poll(), timeout, output));
            }
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    return output;
                }
            } catch (InterruptedException ex) {
                return output;
            }
        } else {
            while (!WorkQueue.isEmpty()) {
                executor.submit(new UnHash(WorkQueue.poll(), timeout));
            }
            executor.shutdown();
        }
        return output;
    }

    public static void main(String[] args) {
        try {
            List<String> hashes = Files.readAllLines(Paths.get(args[0]));
            int N = Integer.parseInt(args[1]);
            long timeout = -1;
            if (args.length > 2)
                timeout = Long.parseLong(args[2]);
            Dispatcher dispatcher = new Dispatcher(hashes);
            dispatcher.dispatch(N, timeout, false);
        } catch (IOException e) {
            System.out.println("Failed reading file");
        }
    }
}