import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author:  andy.xwt
 * Date:    2019-07-02 23:42
 * Description:
 */

public class UrlUtils {

    public static String hashKeyFromUrl(String url) {
        String cacheKey;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            cacheKey = bytesToHexString(digest.digest());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;

    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
