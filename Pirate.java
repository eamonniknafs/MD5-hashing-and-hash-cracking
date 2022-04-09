import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Pirate {
    Queue<String> WorkQueue = new LinkedList<String>();
    LinkedList<String> hintList = new LinkedList<String>();
    Queue<Future<String>> WorkerFutures;
    BlockingQueue<Runnable> WorkerQueue;
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    ThreadPoolExecutor executor;

    public Pirate(int threads, long timeout, List<String> hashes) {
        Dispatcher dispatcher = new Dispatcher(hashes);
        ArrayList<ArrayList<String>> treasureChest = dispatcher.dispatch(threads, timeout, true);
        fillData(treasureChest);
        WorkerQueue = new ArrayBlockingQueue<Runnable>(WorkQueue.size());
    }

    static class StringIntComparator implements Comparator<String> {
        public int compare(String c1, String c2) {
            return new Integer(c1).compareTo(new Integer(c2));
        }
    }

    public void fillData(ArrayList<ArrayList<String>> treasureChest) {
        for (Object hash : treasureChest.get(0)) {
            WorkQueue.add((String) hash);
        }
        for (Object hint : treasureChest.get(1)) {
            hintList.add((String) hint);
        }
        hintList.sort(new StringIntComparator());
        for (String hint : hintList) {
            System.out.println(hint);
        }
    }

    public void findTreasure(int threads, long timeout, boolean firstRun) {
        WorkerFutures = new LinkedList<Future<String>>();
        executor = new ThreadPoolExecutor(threads, threads, 0L, timeUnit, WorkerQueue);
        while (!WorkQueue.isEmpty()) {
            if (firstRun) {
                WorkerFutures.add(executor.submit(new HintUnHash(WorkQueue.poll(), hintList, timeout * 13)));
            } else {
                WorkerFutures.add(executor.submit(new CrackCypher(WorkQueue.poll(), hintList, timeout)));
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, timeUnit);
            for (Future<String> future : WorkerFutures) {
                try {
                    String out = future.get();
                    if (out.length() == 32){
                        WorkQueue.add(out);
                    } else {
                        String[] split = out.split(";");
                        for (String value : split) {
                            hintList.add(value);
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            Set<String> set = new HashSet<>(hintList);
            hintList.clear();
            hintList.addAll(set);
            hintList.sort(new StringIntComparator());
            findTreasure(threads, timeout, false);
        } catch (InterruptedException e) {
            System.out.println("\n\nInterrupted\n\n");
        }
    }

    public static void main(String[] args) {
        try {
            List<String> hashes = Files.readAllLines(Paths.get(args[0]));
            int N = Integer.parseInt(args[1]);
            long timeout = -1;
            if (args.length > 2)
                timeout = Long.parseLong(args[2]);
            Pirate pirate = new Pirate(N, timeout, hashes);
            pirate.findTreasure(N, timeout, true);
        } catch (IOException e) {
            System.out.println("Failed reading file");
        }
    }
}