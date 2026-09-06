package com.ams.security.twofactor;

public class TwoFactorResponse {
	private String secretKey;
	private String qrCodeUrl;

	public TwoFactorResponse(String secretKey, String qrCodeUrl) {
		this.secretKey = secretKey;
		this.qrCodeUrl = qrCodeUrl;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getQrCodeUrl() {
		return qrCodeUrl;
	}
}