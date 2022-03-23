class UnHash {
    public static String unhash(String hash) {
        Boolean found = false;
        String md5 = null;
        int i = 0;
        while (!found) {
            try {
                md5 = Hash.hash(i + "");
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed unhashing";
            }
            if (md5.equals(hash)) {
                found = true;
                return i + "";
            }
            i++;
        }
        return "Failed unhashing";
    }

    public static void main(String args[]) throws Exception {
        unhash(args[0]);
    }
}