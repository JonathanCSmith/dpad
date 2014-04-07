package jonathansmith.dpad.common.crypto;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import jonathansmith.dpad.DPAD;

/**
 * Created by Jon on 08/04/14.
 * <p/>
 * Main class for controlling encryption between the client and server
 */
public class CryptographyManager {

    public static KeyPair createNewKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            return keyPairGenerator.generateKeyPair();
        }

        catch (NoSuchAlgorithmException ex) {
            DPAD.getInstance().handleError("Invalid key generation algorithm. This should not happen!", ex, true);
            return null;
        }
    }

    public static PublicKey decodePublicKey(byte[] bytes) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(keySpec);
        }

        catch (InvalidKeySpecException ex) {
            DPAD.getInstance().handleError("Invalid key spec for crypto key decryption, this should not happen!", ex, true);
        }

        catch (NoSuchAlgorithmException ex) {
            DPAD.getInstance().handleError("Invalid key generation algorithm. This should not happen!", ex, true);
        }

        return null;
    }

    public static SecretKey createNewSharedKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            return keyGenerator.generateKey();
        }

        catch (NoSuchAlgorithmException ex) {
            DPAD.getInstance().handleError("Invalid key generation algorithm. This should not happen!", ex, true);
            return null;
        }
    }

    public static byte[] encryptData(Key key, byte[] unencoded) {
        return cipherOperation(1, key, unencoded);
    }

    public static byte[] decryptData(Key key, byte[] encoded) {
        return cipherOperation(2, key, encoded);
    }

    private static byte[] cipherOperation(int i, Key key, byte[] source) {
        try {
            return createCipher(i, key.getAlgorithm(), key).doFinal(source);
        }

        catch (BadPaddingException ex) {
            DPAD.getInstance().handleError("Invalid padding in cipher operation. This should not happen!", ex, true);
        }

        catch (IllegalBlockSizeException ex) {
            DPAD.getInstance().handleError("Invalid block size in cipher operation. This should not happen!", ex, true);
        }

        return null;
    }

    private static Cipher createCipher(int i, String algorithm, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(i, key);
            return cipher;
        }

        catch (InvalidKeyException ex) {
            DPAD.getInstance().handleError("Invalid key for cipher instancing. This should not happen!", ex, true);
        }

        catch (NoSuchAlgorithmException ex) {
            DPAD.getInstance().handleError("Invalid algorithm. This should not happen!", ex, true);
        }

        catch (NoSuchPaddingException ex) {
            DPAD.getInstance().handleError("No such padding. This should not happend!", ex, true);
        }

        return null;
    }

    public static SecretKey decryptSharedKey(PrivateKey key, byte[] encodedSecretKey) {
        return new SecretKeySpec(decryptData(key, encodedSecretKey), "AES");
    }

    public static Cipher getCipher(int padding, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(padding, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        }

        catch (GeneralSecurityException ex) {
            DPAD.getInstance().handleError("Error creating cipher! This should not happen!", ex, true);
        }

        return null;
    }
}
