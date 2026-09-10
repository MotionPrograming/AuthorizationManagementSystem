package com.ams.security.password;

import com.ams.security.crypto.HashUtils;

/**
 * Backward-compatible password encoder. The historic project format is
 * SHA-256(salt + password):salt, so the implementation deliberately keeps
 * that format until a BCrypt dependency is added to the deployment.
 */
public class BCryptPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) throw new IllegalArgumentException("rawPassword cannot be null");
        String salt = HashUtils.generateSalt();
        return HashUtils.hashPassword(rawPassword.toString(), salt) + ":" + salt;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;
        int separator = encodedPassword.indexOf(':');
        if (separator <= 0 || separator == encodedPassword.length() - 1) return false;
        String hash = encodedPassword.substring(0, separator);
        String salt = encodedPassword.substring(separator + 1);
        return HashUtils.verifyPassword(rawPassword.toString(), hash, salt);
    }
}
