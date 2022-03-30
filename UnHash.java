import java.util.concurrent.TimeUnit;

class UnHash extends Thread {
    String hash;
    TimeUnit time = TimeUnit.MILLISECONDS;

    public UnHash(String hash) {
        super();
        this.hash = hash;
    }

    @Override
    public void run() {
        System.out.println(unhash(hash));
    }

    public static String unhash(String hash) {
        Boolean found = false;
        String md5 = null;
        int i = 0;
        while (!found && !interrupted()) {
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
        return "Failed unhashing hash: " + hash;
    }

    public TimeUnit getTime() {
        return time;
    }

    public static void main(String args[]) throws Exception {
        System.out.print(new UnHash(args[0]));
    }
}