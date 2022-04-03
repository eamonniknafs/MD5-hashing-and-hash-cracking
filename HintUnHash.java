import java.util.LinkedList;
import java.util.concurrent.Callable;

class HintUnHash implements Callable<String> {
    String hash;
    long timeout;
    long endTime;
    LinkedList<String> hints;

    public HintUnHash(String hash, LinkedList<String> hints, long timeout) {
        super();
        this.hash = hash;
        this.timeout = timeout;
        this.hints = hints;
    }

    @Override
    public String call() {
        this.endTime = timeout + System.currentTimeMillis();
        return unhash(hash, endTime, hints);
    }

    public static String unhash(String hash, long endTime, LinkedList<String> hints) {
        Boolean found = false;
        String md5 = null;
        int i = 0;
        while (!found && endTime > System.currentTimeMillis() && i < hints.size()) {
            String h1 = hints.get(i);
            for (int j = i + 1; j < hints.size(); j++) {
                String h2 = hints.get(j);
                for (int mid = new Integer(h1); mid < new Integer(h2); mid++) {
                    try {
                        md5 = Hash.hash(h1 + ";" + mid + ";" + h2);
                    } catch (Exception e) {
                        return "Failed generating hash" + hash;
                    }
                    if (md5.equals(hash)) {
                        found = true;
                        return h1 + ";" + mid + ";" + h2;
                    }
                }
            }
            i++;
        }
        return "" + hash;
    }
}