package com.arminapps.esms.utils;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Utility class for encrypting and decrypting strings using AES/GCM/NoPadding.
 * The provided key (a human-readable string) is hashed and stretched using PBKDF2
 * to derive a secure 256-bit AES key.
 *
 * Features:
 * - AES-256 in GCM mode (authenticated encryption)
 * - Random IV and salt for each encryption
 * - Base64-encoded output containing salt | IV | ciphertext | auth tag
 */
public class Crypto {

    private static final int AES_KEY_SIZE = 256;           // 256-bit key
    private static final int GCM_IV_LENGTH = 12;           // 96-bit IV (recommended for GCM)
    private static final int GCM_TAG_LENGTH = 128;         // 128-bit authentication tag
    private static final int PBKDF2_ITERATIONS = 100_000;  // Strong iteration count
    private static final int SALT_LENGTH = 16;             // 128-bit salt
    private static final String __ = "eSMS";

    /**
     * Encrypts the given plaintext using the provided password string.
     *
     * @param plaintext the string to encrypt
     * @param password  the password/key string
     * @return Base64-encoded string containing salt|IV|ciphertext|tag
     * @throws RuntimeException if encryption fails (e.g., algorithm not available)
     */
    public static String encrypt(String plaintext, String password) {
        try {
            // Generate random salt
            byte[] salt = generateRandomBytes(SALT_LENGTH);

            // Derive AES key from password + salt using PBKDF2
            SecretKey aesKey = deriveKey(password, salt);

            // Generate random IV
            byte[] iv = generateRandomBytes(GCM_IV_LENGTH);

            // Initialize cipher for encryption
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);

            // Perform encryption
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Combine: salt | IV | ciphertext (includes auth tag at the end)
            byte[] combined = combineArrays(salt, iv, ciphertext);

            // Encode to Base64 for easy storage/transport
            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypts a ciphertext produced by the encrypt() method.
     *
     * @param encryptedBase64 Base64 string from encrypt()
     * @param password        the same password used for encryption
     * @return the original plaintext
     * @throws RuntimeException if decryption fails (wrong password, corrupted data, etc.)
     */
    public static String decrypt(String encryptedBase64, String password) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedBase64);

            // Extract salt, IV, and ciphertext+tag
            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] ciphertext = new byte[combined.length - SALT_LENGTH - GCM_IV_LENGTH];

            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(combined, SALT_LENGTH, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, SALT_LENGTH + GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);

            // Derive the same AES key
            SecretKey aesKey = deriveKey(password, salt);

            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);

            // Decrypt and verify authenticity
            byte[] plaintextBytes = cipher.doFinal(ciphertext);

            return new String(plaintextBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Decryption failed (possibly wrong password or corrupted data)", e);
        }
    }

    /**
     * Derives a 256-bit AES key from the password and salt using PBKDF2WithHmacSHA256.
     */
    private static SecretKey deriveKey(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, AES_KEY_SIZE);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Generates cryptographically secure random bytes.
     */
    private static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    public static String stampMessage(String encryptedMessage) {
        byte[] data = (__ + encryptedMessage).getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(
                xor(data, __)
        );
    }

    public static byte[] xor(byte[] data, String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }

        int dataLen = data.length;
        int keyLen = key.length();
        byte[] result = new byte[dataLen];

        for (int i = 0; i < dataLen; i++) {
            char keyChar = key.charAt(i % keyLen);
            result[i] = (byte) (data[i] ^ keyChar);
        }

        return result;
    }

    /**
     * Concatenates multiple byte arrays.
     */
    private static byte[] combineArrays(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }
        byte[] result = new byte[totalLength];
        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static String get_() {
        return __;
    }
}