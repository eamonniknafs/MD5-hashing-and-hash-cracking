import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
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

    public void dispatch(int threads, long timeout) {
        executor = new TPE(threads, threads, 0L,
                java.util.concurrent.TimeUnit.MILLISECONDS, WorkerQueue, timeout);
        while (!WorkQueue.isEmpty()) {
            Future<?> task;
            task = executor.submit(new UnHash(WorkQueue.poll()));
            try {
                task.get();
            } catch (Exception e) {
            }
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
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.fillQueue(hashes);
            dispatcher.dispatch(N, timeout);
        } catch (IOException e) {
            System.out.println("Failed reading file");
        }
    }
}