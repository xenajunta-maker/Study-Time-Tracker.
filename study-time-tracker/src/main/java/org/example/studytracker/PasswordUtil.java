package org.example.studytracker;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Salted PBKDF2 password hashing using only the JDK. */
final class PasswordUtil {

    private static final String ALGORITHM  = "PBKDF2WithHmacSHA256";
    private static final int    ITERATIONS = 120_000;
    private static final int    KEY_BITS   = 256;
    private static final int    SALT_BYTES = 16;

    private PasswordUtil() { }

    static String newSalt() {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    static String hash(String password, String saltBase64) {
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(), Base64.getDecoder().decode(saltBase64), ITERATIONS, KEY_BITS);
        try {
            byte[] key = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(key);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing is not available", e);
        } finally {
            spec.clearPassword();
        }
    }

    static boolean matches(String password, String saltBase64, String expectedHashBase64) {
        byte[] actual   = Base64.getDecoder().decode(hash(password, saltBase64));
        byte[] expected = Base64.getDecoder().decode(expectedHashBase64);
        return MessageDigest.isEqual(actual, expected);   // constant-time comparison
    }
}
