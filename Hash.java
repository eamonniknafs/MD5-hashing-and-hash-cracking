import java.math.BigInteger;
import java.security.MessageDigest;

public class Hash {
    public static String hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes(), 0, input.length());
        StringBuilder hash = new StringBuilder(new BigInteger(1, md.digest()).toString(16));
        while (hash.length() < 32) {
            hash.insert(0, '0');
        }
        return "" + hash;
    }

    public static void main(String args[]) throws Exception {
        String str = "13244";
        System.out.println("MD5 hash of " + str + ": " + hash(str));
    }
}