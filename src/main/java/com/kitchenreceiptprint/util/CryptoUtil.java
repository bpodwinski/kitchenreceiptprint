package com.kitchenreceiptprint.util;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String KEY = "MaCleSecrete1234";

    public static String encrypt(String value) {
        try {
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
            byte[] encryptedValue = Objects.requireNonNull(cipher).doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encryptedValue);
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Error during encryption");
            return null;
        }
    }

    public static String decrypt(String value) {
        try {
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
            byte[] decryptedValue = Objects.requireNonNull(cipher).doFinal(Base64.getDecoder().decode(value));
            return new String(decryptedValue);
        } catch (Exception e) {
            ExceptionUtil.handleException(e, "Error during decryption");
            return null;
        }
    }

    private static Cipher initCipher(int mode) {
        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(mode, key);
            return cipher;
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            // Handle specific cryptography-related exceptions
            ExceptionUtil.handleException(e, "Error initializing Cipher");
            // You can choose to return null, throw a custom exception, or handle the error differently
            return null; // Or throw a custom exception
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            ExceptionUtil.handleException(e, "Unexpected error during Cipher initialization");
            return null; // Or throw a custom exception
        }
    }

    private static Key generateKey() throws NoSuchAlgorithmException {
        return new SecretKeySpec(KEY.getBytes(), ALGORITHM);
    }
}
