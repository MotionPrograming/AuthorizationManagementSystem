package com.ams.security.authentication;

import java.util.Optional;

import com.ams.modules.user.entity.User;
import com.ams.modules.user.repository.UserRepository;
import com.ams.security.password.PasswordEncoder;
import com.ams.security.session.SessionManager;
import com.ams.security.twofactor.TwoFactorAuthService;

public class AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final SessionManager sessionManager;
	private final TwoFactorAuthService twoFactorAuthService;

	public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			SessionManager sessionManager, TwoFactorAuthService twoFactorAuthService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.sessionManager = sessionManager;
		this.twoFactorAuthService = twoFactorAuthService;
	}

	// প্রাথমিক লগইন ও ২FA স্ট্যাটাস চেকিং
	public LoginResult authenticate(String username, String rawPassword) {
		Optional<User> userOptional = userRepository.findByUsername(username);

		if (userOptional.isEmpty()) {
			return LoginResult.failure("Invalid username or password");
		}

		User user = userOptional.get();

		if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
			return LoginResult.failure("User account is inactive or disabled");
		}

		// BCrypt Password Check
		if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
			return LoginResult.failure("Invalid username or password");
		}

		// ২FA সক্রিয় থাকলে OTP ভ্যালিডেশন স্টেপে পাঠানো
		if (user.getIs2faEnabled() != null && user.getIs2faEnabled() == 1) {
			return LoginResult.requires2FA(user.getId(), user.getUsername());
		}

		// ২FA চালু না থাকলে সেশন ক্রিয়েট
		String sessionId = sessionManager.createSession(user.getId(), user.getUsername());
		return LoginResult.success(sessionId, user);
	}

	// ২FA OTP ভ্যালিডেশন করে চূড়ান্ত লগইন
	public LoginResult verify2FAAndLogin(Long userId, int code) {
		Optional<User> userOptional = userRepository.findById(userId);

		if (userOptional.isEmpty()) {
			return LoginResult.failure("User not found");
		}

		User user = userOptional.get();
		boolean isValidOtp = twoFactorAuthService.authenticate2FA(user.getTwoFactorSecret(), code);

		if (!isValidOtp) {
			return LoginResult.failure("Invalid 2FA verification code");
		}

		String sessionId = sessionManager.createSession(user.getId(), user.getUsername());
		return LoginResult.success(sessionId, user);
	}

	// লগআউট হ্যান্ডলার
	public void logout(String sessionId) {
		sessionManager.invalidateSession(sessionId);
		SecurityContextHolder.clearContext();
	}

	// Login Response Wrapper DTO
	public static class LoginResult {
		private final boolean success;
		private final boolean requires2FA;
		private final String message;
		private final String sessionId;
		private final Long userId;
		private final String username;
		private final User user;

		private LoginResult(boolean success, boolean requires2FA, String message, String sessionId, Long userId,
				String username, User user) {
			this.success = success;
			this.requires2FA = requires2FA;
			this.message = message;
			this.sessionId = sessionId;
			this.userId = userId;
			this.username = username;
			this.user = user;
		}

		public static LoginResult success(String sessionId, User user) {
			return new LoginResult(true, false, "Login successful", sessionId, user.getId(), user.getUsername(), user);
		}

		public static LoginResult requires2FA(Long userId, String username) {
			return new LoginResult(false, true, "2FA Verification Required", null, userId, username, null);
		}

		public static LoginResult failure(String message) {
			return new LoginResult(false, false, message, null, null, null, null);
		}

		public boolean isSuccess() {
			return success;
		}

		public boolean isRequires2FA() {
			return requires2FA;
		}

		public String getMessage() {
			return message;
		}

		public String getSessionId() {
			return sessionId;
		}

		public Long getUserId() {
			return userId;
		}

		public String getUsername() {
			return username;
		}

		public User getUser() {
			return user;
		}
	}
}