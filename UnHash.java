import java.util.concurrent.Callable;

class UnHash implements Callable<String> {
    String hash;
    long timeout;
    long endTime;
    Boolean print = true;

    public UnHash(String hash, long timeout, Boolean print) {
        super();
        this.hash = hash;
        this.timeout = timeout;
        this.print = print;
    }

    @Override
    public String call() {
        if (timeout == -1) {
            String out = unhash(hash);
            System.out.println(out);
            return out;
        } else {
            this.endTime = timeout + System.currentTimeMillis();
            String out = unhash(hash, endTime);
            if (print == true) {
                System.out.println(unhash(hash, endTime));
            }
            return out;
        }
    }

    public static String unhash(String hash) {
        Boolean found = false;
        String md5 = null;
        int i = 0;
        while (!found) {
            try {
                md5 = Hash.hash(i + "");
            } catch (Exception e) {
                return "Failed generating hash" + hash;
            }
            if (md5.equals(hash)) {
                found = true;
                return i + "";
            }
            i++;
        }
        return "" + hash;
    }

    public static String unhash(String hash, long endTime) {
        Boolean found = false;
        String md5 = null;
        int i = 0;
        while (!found && endTime > System.currentTimeMillis()) {
            try {
                md5 = Hash.hash(i + "");
            } catch (Exception e) {
                return "Failed generating hash" + hash;
            }
            if (md5.equals(hash)) {
                found = true;
                return i + "";
            }
            i++;
        }
        return "" + hash;
    }

    public static void main(String args[]) throws Exception {
        System.out.print(unhash("136c2f0599b3a0175c544b72e4861b9f"));
    }
}