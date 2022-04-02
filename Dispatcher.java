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

    public Dispatcher(int threads, long timeout, List<String> hashes) {
        WorkerQueue = new ArrayBlockingQueue<Runnable>(hashes.size());
        fillQueue(hashes);
        dispatch(threads, timeout);
    }

    public void fillQueue(List<String> hashes) {
        for (Object hash : hashes) {
            WorkQueue.add((String) hash);
        }
    }

    public void dispatch(int threads, long timeout) {
        executor = new ThreadPoolExecutor(threads, threads, 0L, timeUnit, WorkerQueue);
        while (!WorkQueue.isEmpty()) {
            executor.submit(new UnHash(WorkQueue.poll(), timeout));
        }
        executor.shutdown();
    }

    public static void main(String[] args) {
        try {
            List<String> hashes = Files.readAllLines(Paths.get(args[0]));
            int N = Integer.parseInt(args[1]);
            long timeout = -1;
            if (args.length > 2)
                timeout = Long.parseLong(args[2]);
            new Dispatcher(N, timeout, hashes);
        } catch (IOException e) {
            System.out.println("Failed reading file");
        }
    }
}