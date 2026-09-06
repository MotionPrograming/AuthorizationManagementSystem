package com.ams.security.twofactor;

import java.util.Optional;

import com.ams.modules.user.entity.User;
import com.ams.modules.user.repository.UserRepository;

public class TwoFactorAuthService {

	private final UserRepository userRepository;
	private static final String ISSUER = "AMS_Security";

	public TwoFactorAuthService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * ইউজারের জন্য নতুন Secret Key এবং QR Code URL জেনারেট করে
	 */
	public TwoFactorResponse setup2FA(Long userId) {
		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			throw new RuntimeException("User not found");
		}

		User user = userOpt.get();
		String secretKey = TOTPProvider.generateSecretKey();
		String qrCodeUrl = TOTPProvider.getQRCodeUrl(user.getUsername(), ISSUER, secretKey);

		// সিক্রেট কী ডাটাবেজে স্টোর করা
		user.setTwoFactorSecret(secretKey);
		userRepository.update(user);

		return new TwoFactorResponse(secretKey, qrCodeUrl);
	}

	/**
	 * প্রথমবার ২FA একটিভেট করার জন্য OTP ভ্যালিডেশন
	 */
	public boolean enable2FA(Long userId, int code) {
		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			return false;
		}

		User user = userOpt.get();
		if (user.getTwoFactorSecret() == null) {
			return false;
		}

		boolean isValid = TOTPProvider.validateOTP(user.getTwoFactorSecret(), code);
		if (isValid) {
			user.setIs2faEnabled(1);
			userRepository.update(user);
			return true;
		}
		return false;
	}

	/**
	 * সাধারণ অথেন্টিকেশনে 2FA Code যাচাই করা
	 */
	public boolean authenticate2FA(String secretKey, int code) {
		if (secretKey == null || secretKey.isEmpty()) {
			return false;
		}
		return TOTPProvider.validateOTP(secretKey, code);
	}

	/**
	 * ২FA নিষ্ক্রিয় করার জন্য
	 */
	public boolean disable2FA(Long userId, int code) {
		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isEmpty()) {
			return false;
		}

		User user = userOpt.get();
		if (TOTPProvider.validateOTP(user.getTwoFactorSecret(), code)) {
			user.setIs2faEnabled(0);
			user.setTwoFactorSecret(null);
			userRepository.update(user);
			return true;
		}
		return false;
	}
}