package com.ams.security.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class HashUtils {

	private static final int SALT_LENGTH = 16;

	// Salt জেনারেট করার মেথড
	public static String generateSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[SALT_LENGTH];
		random.nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}

	// SHA-256 দিয়ে পাসওয়ার্ড হ্যাশ করার মেথড (Password + Salt)
	public static String hashPassword(String password, String salt) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			// Salt যোগ করা
			digest.update(Base64.getDecoder().decode(salt));

			// Password হ্যাশ করা
			byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

			return Base64.getEncoder().encodeToString(hashedBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error hashing password with SHA-256", e);
		}
	}

	// ডাটা ইন্টিগ্রিটির জন্য প্লেইন SHA-256 হ্যাশ (Salt ছাড়া)
	public static String hashData(String data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hashedBytes);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Error hashing data with SHA-256", e);
		}
	}

	// পাসওয়ার্ড ভ্যালিডেশন মেথড
	public static boolean verifyPassword(String password, String storedHash, String salt) {
		String newHash = hashPassword(password, salt);
		return newHash.equals(storedHash);
	}
}