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
                        // hints.set(i, null);
                        // hints.set(i + offset, null);
                        return h1 + ";" + mid + ";" + h2;
                    }
                }
                i++;
            }
            offset++;
        }
        return "" + hash;
    }

    public static void main(String args[]){
        // [null, null, null, null, null, null, null, null, null, null, null, null,
        // null, null, null, null, null, null, null, null, null, null, 17, null, 778,
        // 157, null, 982, 361, null, 554, 462, null, 618, 626, null, 795, 911, 957,
        // 972, 254, null, 854, 209, 637, 931, 400, null, 899, 283, null, 907, 47, 144,
        // 581, 466, null, 548, 463, null, 698, 607, 610, 643, 184, 237, 764, 534, 572,
        // 673]
        LinkedList<String> hints = new LinkedList<String>();
        hints.add("17");
        hints.add("778");
        hints.add("157");
        hints.add("982");
        hints.add("361");
        hints.add("554");
        hints.add("462");
        hints.add("618");
        hints.add("626");
        hints.add("795");
        hints.add("911");
        hints.add("957");
        hints.add("972");
        hints.add("254");
        hints.add("854");
        hints.add("209");
        hints.add("637");
        hints.add("931");
        hints.add("400");
        hints.add("899");
        hints.add("283");
        hints.add("907");
        hints.add("47");
        hints.add("144");
        hints.add("581");
        hints.add("466");
        hints.add("548");
        hints.add("463");
        hints.add("698");
        hints.add("607");
        hints.add("610");
        hints.add("643");
        hints.add("184");
        hints.add("237");
        hints.add("764");
        hints.add("534");
        hints.add("572");
        hints.add("673");

        System.out.println(unhash("964d2f2739c51ada0113a3cfa7f94939", Integer.MAX_VALUE, hints));

        
    }
}