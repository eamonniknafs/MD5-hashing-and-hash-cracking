import java.util.LinkedList;
import java.util.concurrent.Callable;

class HintUnHash implements Callable<String> {
    String hash;
    long timeout;
    long endTime;
    LinkedList<String> hints;
    boolean cypher;

    public HintUnHash(String hash, LinkedList<String> hints, long timeout, boolean cypher) {
        super();
        this.hash = hash;
        this.timeout = timeout;
        this.hints = hints;
        this.cypher = cypher;
    }

    @Override
    public String call() {
        this.endTime = timeout + System.currentTimeMillis();
        return unhash(hash, endTime, hints, cypher);
    }

    public static String unhash(String hash, long endTime, LinkedList<String> hints, boolean cypher) {
        Boolean found = false;
        String md5 = null;
        int offset = 1;
        while (!found && endTime > System.currentTimeMillis() && offset < hints.size()) {
            int i = 0;
            while (!found && endTime > System.currentTimeMillis() && i < hints.size() - offset) {
                if (hints.get(i) == null || hints.get(i + offset) == null) {
                    i++;
                    continue;
                }
                String h1 = hints.get(i);
                String h2 = hints.get(i + offset);
                for (int mid = new Integer(h1); mid < new Integer(h2); mid++) {
                    try {
                        md5 = Hash.hash(h1 + ";" + mid + ";" + h2);
                    } catch (Exception e) {
                        return "Failed generating hash" + hash;
                    }
                    if (md5.equals(hash)) {
                        found = true;
                        if (!cypher){
                        hints.set(i, null);
                        hints.set(i + offset, null);
                        }
                        return h1 + ";" + mid + ";" + h2;
                    }
                }
                i++;
            }
            offset++;
        }
        return "" + hash;
    }
}