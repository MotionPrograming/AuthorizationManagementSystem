package com.ams.security.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtils {

	private static final String ALGORITHM = "HmacSHA256";

	public static String calculateHmac(String data, String secretKey) {
		try {
			Mac mac = Mac.getInstance(ALGORITHM);
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
			mac.init(secretKeySpec);
			byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
		} catch (Exception e) {
			throw new RuntimeException("Error generating HMAC signature", e);
		}
	}

	public static boolean verifyHmac(String data, String secretKey, String expectedHmac) {
		String calculatedHmac = calculateHmac(data, secretKey);
		return MessageDigest.isEqual(calculatedHmac.getBytes(StandardCharsets.UTF_8), expectedHmac.getBytes(StandardCharsets.UTF_8));
	}
}