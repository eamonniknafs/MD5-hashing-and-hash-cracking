class UnHash extends Thread {
    String hash;
    long timeout;
    long endTime;

    public UnHash(String hash, long timeout) {
        super();
        this.hash = hash;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        if (timeout == -1) {
            System.out.println(unhash(hash));
        }
        else {
            this.endTime = timeout + System.currentTimeMillis();
            System.out.println(unhash(hash, endTime));
        }
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
        return ""+hash;
    }

    public static String unhash(String hash, long endTime) {
        Boolean found = false;
        String md5 = null;
        int i = 0;
        while (!found && !interrupted() && endTime > System.currentTimeMillis()) {
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