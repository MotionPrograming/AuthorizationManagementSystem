package com.ams.security.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtTokenProvider {

	// HMAC-SHA256 এর জন্য অন্তত ২৫৬-বিট (৩২ বাইট) সিক্রেট কী ব্যবহার করা উচিত
	private static final String SECRET_KEY = "AMS_ULTRA_SECURE_SECRET_KEY_FOR_JWT_HMAC256_SIGNING";
	private static final long EXPIRATION_TIME_MS = 3600000; // ১ ঘণ্টা

	/**
	 * HMAC-SHA256 ব্যবহার করে JWT টোকেন তৈরি করে
	 */
	public static String generateToken(String username, Long userId) {
		long now = System.currentTimeMillis();
		long exp = now + EXPIRATION_TIME_MS;

		// 1. Header
		String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

		// 2. Payload
		String payload = String.format("{\"sub\":\"%s\",\"userId\":%d,\"iat\":%d,\"exp\":%d}", username, userId,
				now / 1000, exp / 1000);

		// Base64URL Encoding
		String encodedHeader = Base64.getUrlEncoder().withoutPadding()
				.encodeToString(header.getBytes(StandardCharsets.UTF_8));
		String encodedPayload = Base64.getUrlEncoder().withoutPadding()
				.encodeToString(payload.getBytes(StandardCharsets.UTF_8));

		// 3. Signature (HMAC-SHA256 via HmacUtils)
		String dataToSign = encodedHeader + "." + encodedPayload;
		String signature = HmacUtils.calculateHmac(dataToSign, SECRET_KEY);

		return dataToSign + "." + signature;
	}

	/**
	 * HMAC-SHA256 সিগনেচার এবং এক্সপায়ারেশন টাইম চেক করে টোকেন ভ্যালিডেট করে
	 */
	public static boolean validateToken(String token) {
		try {
			String[] parts = token.split("\\.");
			if (parts.length != 3) {
				return false;
			}

			String dataToSign = parts[0] + "." + parts[1];
			String providedSignature = parts[2];

			// HMAC-SHA256 সিগনেচার ভ্যালিডেশন
			if (!HmacUtils.verifyHmac(dataToSign, SECRET_KEY, providedSignature)) {
				return false;
			}

			// টোকেন এক্সপায়ারেশন চেক
			Long expTimeSeconds = getClaimFromToken(token, "exp");
			if (expTimeSeconds != null) {
				long currentSeconds = System.currentTimeMillis() / 1000;
				return expTimeSeconds > currentSeconds;
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * টোকেন থেকে সাবজেক্ট (Username) এক্সট্র্যাক্ট করে
	 */
	public static String getUsernameFromToken(String token) {
		try {
			String[] parts = token.split("\\.");
			String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

			int subIndex = payloadJson.indexOf("\"sub\":\"") + 7;
			int endIndex = payloadJson.indexOf("\"", subIndex);
			return payloadJson.substring(subIndex, endIndex);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * টোকেন থেকে প্লেইন Claim মান বের করে
	 */
	private static Long getClaimFromToken(String token, String claimKey) {
		try {
			String[] parts = token.split("\\.");
			String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

			String searchKey = "\"" + claimKey + "\":";
			int keyIndex = payloadJson.indexOf(searchKey);
			if (keyIndex == -1)
				return null;

			int startIndex = keyIndex + searchKey.length();
			int endIndex = payloadJson.indexOf(",", startIndex);
			if (endIndex == -1) {
				endIndex = payloadJson.indexOf("}", startIndex);
			}

			return Long.parseLong(payloadJson.substring(startIndex, endIndex).trim());
		} catch (Exception e) {
			return null;
		}
	}
}