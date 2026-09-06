package com.ams.security.password;

import com.ams.security.crypto.HashUtils;

public class BCryptPasswordEncoder implements PasswordEncoder {

	public BCryptPasswordEncoder() {
		// Default Constructor
	}

	@Override
	public String encode(CharSequence rawPassword) {
		if (rawPassword == null) {
			throw new IllegalArgumentException("rawPassword cannot be null");
		}

		String salt = HashUtils.generateSalt();
		return HashUtils.hashPassword(rawPassword.toString(), salt) + ":" + salt;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (rawPassword == null || encodedPassword == null || !encodedPassword.contains(":")) {
			return false;
		}

		String[] parts = encodedPassword.split(":");
		String storedHash = parts[0];
		String salt = parts[1];

		return HashUtils.verifyPassword(rawPassword.toString(), storedHash, salt);
	}
}