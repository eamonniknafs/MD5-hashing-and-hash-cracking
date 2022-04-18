import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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
    List<String> cypher;

    public Pirate(int threads, long timeout, List<String> hashes, List<String> cypher) {
        Dispatcher dispatcher = new Dispatcher(hashes);
        ArrayList<ArrayList<String>> treasureChest = dispatcher.dispatch(threads, timeout, true);
        this.cypher = cypher;
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
        if (cypher == null) {
            for (String hint : hintList) {
                System.out.println(hint);
            }
        }
    }

    public static String crackCypher(List<String> cypher, LinkedList<String> hints) {
        String crackedCypher = "";
        for (String cyph : cypher) {
            for (String hint : hints) {
                crackedCypher = crackedCypher + cyph.charAt(Integer.parseInt(hint));
            }
            if (cypher.size() > 1) {
                crackedCypher = crackedCypher + "\n";
            }
        }
        return "" + crackedCypher;
    }

    public void findTreasure(int threads, long timeout) {
        WorkerFutures = new LinkedList<Future<String>>();
        executor = new ThreadPoolExecutor(threads, threads, 0L, timeUnit, WorkerQueue);
        while (!WorkQueue.isEmpty()) {
            WorkerFutures.add(executor.submit(new HintUnHash(WorkQueue.poll(), hintList, timeout * 13)));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, timeUnit);
            for (Future<String> future : WorkerFutures) {
                try {
                    String out = future.get();
                    if (cypher == null) {
                        System.out.println(out);
                    }
                    if (cypher != null) {
                        if (out.length() == 32) {
                            WorkQueue.add(out);
                        } else {
                            String[] split = out.split(";");
                            for (String value : split) {
                                hintList.add(value);
                            }
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            if (!WorkQueue.isEmpty()) {
                findTreasure(threads, timeout);
            }
            if (!hintList.isEmpty()) {
                hintList.removeAll(Collections.singleton(null));
                Set<String> set = new HashSet<>(hintList);
                hintList.clear();
                hintList.addAll(set);
                hintList.sort(new StringIntComparator());
                System.out.println(crackCypher(cypher, hintList));
                hintList.clear();
            }
        } catch (InterruptedException e) {
            System.out.println("\n\nInterrupted\n\n");
        }
    }

    public static void main(String[] args) {
        try {
            List<String> hashes = Files.readAllLines(Paths.get(args[0]));
            int N = Integer.parseInt(args[1]);
            long timeout = -1;
            List<String> cypherIn = null;
            if (args.length > 2)
                timeout = Long.parseLong(args[2]);
            if (args.length > 3)
                cypherIn = Files.readAllLines(Paths.get(args[3]));
            Pirate pirate = new Pirate(N, timeout, hashes, cypherIn);
            pirate.findTreasure(N, timeout);
        } catch (IOException e) {
            System.out.println("Failed reading file");
        }
    }
}