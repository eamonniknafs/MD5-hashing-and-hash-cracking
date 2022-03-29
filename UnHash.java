class UnHash extends Thread {
    String hash;

    public UnHash(String hash) {
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
        return "Failed unhashing hash: " + hash;
    }

    public static void main(String args[]) throws Exception {
        System.out.print(unhash(args[0]));
    }
}