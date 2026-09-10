package com.ams.security.twofactor;

import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TOTPProvider {// Hardened HMAC-SHA256/512 Version

	// Cryptographically Hardened Standards
	private static final int SECRET_SIZE = 32; // 256 bits (SHA-256 match)
	private static final int TIME_STEP_SECONDS = 30;
	private static final int DIGITS = 6;
	private static final String CRYPTO_ALGORITHM = "HmacSHA256"; // HMAC-SHA256 for higher security

	/**
	 * 256-bit Strong Secure Random Secret Key (Base32 Encoded) জেনারেট করে
	 */
	public static String generateSecretKey() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[SECRET_SIZE];
		random.nextBytes(bytes);
		return encodeBase32(bytes);
	}

	/**
	 * Timing-Attack Resistant Constant-Time OTP Validation (30s Window)
	 */
	public static boolean validateOTP(String secretKeyBase32, int inputCode) {
		if (secretKeyBase32 == null || inputCode < 0 || inputCode > 999999) return false;
		long currentInterval = System.currentTimeMillis() / 1000 / TIME_STEP_SECONDS;

		// Clock Skew (Current, Previous, Next)
		for (int i = -1; i <= 1; i++) {
			int generatedCode = generateTOTP(secretKeyBase32, currentInterval + i);

			// Constant-time integer comparison
			if (constantTimeEquals(generatedCode, inputCode)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Strong HMAC-SHA256 base otpauth:// QR Code URL
	 */
	public static String getQRCodeUrl(String username, String issuer, String secretKeyBase32) {
		String encodedIssuer = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
		String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);

		return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA256&digits=6&period=30",
				encodedIssuer, encodedUsername, secretKeyBase32, encodedIssuer);
	}

	/**
	 * HMAC-SHA256 Dynamic Truncation দিয়ে TOTP কোড তৈরি
	 */
	private static int generateTOTP(String secretKeyBase32, long interval) {
		byte[] key = decodeBase32(secretKeyBase32);
		byte[] data = ByteBuffer.allocate(8).putLong(interval).array();

		try {
			Mac mac = Mac.getInstance(CRYPTO_ALGORITHM);
			SecretKeySpec signKey = new SecretKeySpec(key, CRYPTO_ALGORITHM);
			mac.init(signKey);
			byte[] hash = mac.doFinal(data);

			int offset = hash[hash.length - 1] & 0xF;
			int binary = ((hash[offset] & 0x7F) << 24) | ((hash[offset + 1] & 0xFF) << 16)
					| ((hash[offset + 2] & 0xFF) << 8) | (hash[offset + 3] & 0xFF);

			return binary % (int) Math.pow(10, DIGITS);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException("Error generating hardened TOTP code", e);
		}
	}

	/**
	 * Timing Attack প্রতিরোধে Constant-Time Compare
	 */
	private static boolean constantTimeEquals(int a, int b) {
		byte[] aBytes = ByteBuffer.allocate(4).putInt(a).array();
		byte[] bBytes = ByteBuffer.allocate(4).putInt(b).array();
		return MessageDigest.isEqual(aBytes, bBytes);
	}

	// RFC 4648 Base32 encoder/decoder
	private static String encodeBase32(byte[] data) {
		final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();
		StringBuilder out = new StringBuilder((data.length * 8 + 4) / 5);
		int buffer = 0, bits = 0;
		for (byte b : data) {
			buffer = (buffer << 8) | (b & 0xFF);
			bits += 8;
			while (bits >= 5) {
				bits -= 5;
				out.append(chars[(buffer >>> bits) & 31]);
			}
		}
		if (bits > 0) out.append(chars[(buffer << (5 - bits)) & 31]);
		return out.toString();
	}

	private static byte[] decodeBase32(String value) {
		if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("Invalid Base32 secret");
		String input = value.replace("=", "").replaceAll("\\s+", "").toUpperCase();
		byte[] out = new byte[input.length() * 5 / 8];
		int buffer = 0, bits = 0, index = 0;
		for (char c : input.toCharArray()) {
			int val = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".indexOf(c);
			if (val < 0) throw new IllegalArgumentException("Invalid Base32 secret");
			buffer = (buffer << 5) | val;
			bits += 5;
			if (bits >= 8) {
				bits -= 8;
				out[index++] = (byte) ((buffer >>> bits) & 0xFF);
			}
		}
		return out;
	}
}