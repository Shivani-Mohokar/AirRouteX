package com.airroutex.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * PasswordUtil.java
 * ------------------------------------------------------------------
 * Thin wrapper around jBCrypt so the rest of the codebase never
 * touches the BCrypt API directly - if we ever swapped hashing
 * libraries, only this file would change.
 *
 * BCrypt (not plain SHA-256/MD5) is used because it is deliberately
 * SLOW and includes a random salt baked into the hash itself. That
 * makes brute-force and rainbow-table attacks impractical, which is
 * exactly what you want for password storage (as opposed to, say,
 * hashing a file for integrity checking, where speed is a feature).
 * ------------------------------------------------------------------
 */
public class PasswordUtil {

    private static final int BCRYPT_COST_FACTOR = 10; // higher = slower = more brute-force resistant

    private PasswordUtil() {
        // utility class - no instances
    }

    /** Hashes a plain-text password. The returned string already contains the salt. */
    public static String hash(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(BCRYPT_COST_FACTOR));
    }

    /** Checks a plain-text password against a previously stored BCrypt hash. */
    public static boolean verify(String plainTextPassword, String storedHash) {
        return BCrypt.checkpw(plainTextPassword, storedHash);
    }
}
