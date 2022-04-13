import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

public class Dispatcher {
    Queue<String> WorkQueue = new LinkedList<String>();
    Queue<Future<String>> WorkerFutures = new LinkedList<Future<String>>();;
    BlockingQueue<Runnable> WorkerQueue;
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    ThreadPoolExecutor executor;
    HashMap<Integer, String> hashes = new HashMap<Integer, String>();

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
        executor = new ThreadPoolExecutor(threads, threads, 0L, timeUnit, WorkerQueue);
        if (outputList) {
            ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>(2);
            output.add(new ArrayList<String>());
            output.add(new ArrayList<String>());
            while (!WorkQueue.isEmpty()) {
                WorkerFutures.add(executor.submit(new UnHash(WorkQueue.poll(), timeout, false, hashes)));
            }
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, timeUnit);
                for (Future<String> future : WorkerFutures) {
                    try {
                        String out = future.get();
                        if (out.length() == 32) {
                            output.get(0).add(out);
                        } else {
                            output.get(1).add(out);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("\n\nInterrupted\n\n");
            }
            return output;
        } else {
            while (!WorkQueue.isEmpty()) {
                executor.submit(new UnHash(WorkQueue.poll(), timeout, true));
            }
            executor.shutdown();
        }
        return new ArrayList<ArrayList<String>>();
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