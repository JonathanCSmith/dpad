package jonathansmith.dpad.server.engine.user;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * User verification functions
 */
public class UserVerification {

    private static final String ALG           = "PBKDF2WithHmacSHA1";
    private static final int    SALT_BYTES    = 16;
    private static final int    HASH_SIZE     = 32;
    private static final int    PBKDF2_CYCLES = 775;
    private static final int    ITER_PART     = 0;
    private static final int    SALT_PART     = 1;
    private static final int    HASH_PART     = 2;

    public static boolean validateUserPassword(String password, String correctAnswer) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String[] parts = correctAnswer.split(":::");
        int cycles = Integer.parseInt(parts[ITER_PART]);
        byte[] salt = fromHex(parts[SALT_PART]);
        byte[] hash = fromHex(parts[HASH_PART]);
        byte[] testHash = getHash(password.toCharArray(), salt, cycles, hash.length);
        return safeEquals(hash, testHash);
    }

    public static String createSaltedHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);

        byte[] hash = getHash(password.toCharArray(), salt, PBKDF2_CYCLES, HASH_SIZE);
        return PBKDF2_CYCLES + ":::" + toHex(salt) + ":::" + toHex(hash);
    }

    private static byte[] getHash(char[] password, byte[] salt, int cyles, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, cyles, bytes * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALG);
        return factory.generateSecret(keySpec).getEncoded();
    }

    private static byte[] fromHex(String source) {
        byte[] bin = new byte[source.length() / 2];
        for (int i = 0; i < bin.length; i++) {
            bin[i] = (byte) Integer.parseInt(source.substring(i << 1, (i << 1) + 2), 16);
        }

        return bin;
    }

    private static String toHex(byte[] source) {
        BigInteger i = new BigInteger(1, source);
        String hex = i.toString(16);
        int padding = (source.length << 1) - hex.length();
        if (padding > 0) {
            return String.format("%0" + padding + "d", 0) + hex;
        }

        return hex;
    }

    private static boolean safeEquals(byte[] b1, byte[] b2) {
        int diff = b1.length ^ b2.length;
        for (int i = 0; i < b1.length && i < b2.length; i++) {
            diff |= b1[i] ^ b2[i];
        }

        return diff == 0;
    }
}
