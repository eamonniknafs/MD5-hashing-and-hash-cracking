import java.util.LinkedList;
import java.util.concurrent.Callable;

class CrackCypher implements Callable<String> {
    String cypher;
    long timeout;
    long endTime;
    LinkedList<String> hints;

    public CrackCypher(String cypher, LinkedList<String> hints, long timeout) {
        super();
        this.cypher = cypher;
        this.timeout = timeout;
        this.hints = hints;
    }

    @Override
    public String call() {
        this.endTime = timeout + System.currentTimeMillis();
        return crack(cypher, endTime, hints);
    }

    public static String crack(String cypher, long endTime, LinkedList<String> hints) {
        String crackedCypher = "";
        for (String hint : hints) {
            if (endTime < System.currentTimeMillis()) {
                return "";
            }
            crackedCypher = crackedCypher + cypher.charAt(Integer.parseInt(hint)-1);
        }
        return "" + crackedCypher;
    }
}