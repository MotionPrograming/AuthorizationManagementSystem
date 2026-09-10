package com.ams.modules.auth.service.impl;

import java.time.LocalDateTime;

import com.ams.common.exception.AuthenticationException;
import com.ams.common.exception.ValidationException;
import com.ams.modules.auth.dto.LoginRequest;
import com.ams.modules.auth.dto.LoginResponse;
import com.ams.modules.auth.entity.Session;
import com.ams.modules.auth.mapper.AuthMapper;
import com.ams.modules.auth.repository.AuthRepository;
import com.ams.modules.auth.service.AuthService;
import com.ams.modules.auth.validator.AuthValidator;
import com.ams.modules.user.entity.User;
import com.ams.modules.user.repository.UserRepository;
import com.ams.security.crypto.JwtTokenProvider;
import com.ams.security.password.BCryptPasswordEncoder;
import com.ams.security.password.PasswordEncoder;

public class AuthServiceImpl implements AuthService {
	private final AuthRepository authRepository;
	private final UserRepository userRepository;
	private final AuthValidator authValidator;
	private final PasswordEncoder passwordEncoder;

	public AuthServiceImpl(AuthRepository authRepository, UserRepository userRepository, AuthValidator authValidator) {
		this(authRepository, userRepository, authValidator, new BCryptPasswordEncoder());
	}

	public AuthServiceImpl(AuthRepository authRepository, UserRepository userRepository, AuthValidator authValidator,
			PasswordEncoder passwordEncoder) {
		this.authRepository = authRepository;
		this.userRepository = userRepository;
		this.authValidator = authValidator;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		authValidator.validateLogin(request);
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AuthenticationException("Invalid username or password."));
		if (!"ACTIVE".equalsIgnoreCase(user.getStatus()))
			throw new AuthenticationException("User account is inactive or locked.");
		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
			throw new AuthenticationException("Invalid username or password.");
		if (user.getIs2faEnabled() != null && user.getIs2faEnabled() == 1)
			throw new AuthenticationException("2FA_REQUIRED:" + user.getUserId());

		return createLoginSession(user);
	}

	private LoginResponse createLoginSession(User user) {
		LocalDateTime now = LocalDateTime.now();
		String token = JwtTokenProvider.generateToken(user.getUsername(), user.getUserId());
		Session session = new Session(user.getUserId(), token, now, now.plusHours(8));
		if (!authRepository.saveSession(session))
			throw new AuthenticationException("Unable to create session.");
		userRepository.updateLastLogin(user.getUserId());
		return AuthMapper.toLoginResponse(session, user.getUsername(), "Login successful");
	}

	@Override
	public LoginResponse completeTwoFactorLogin(Long userId, int code) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new AuthenticationException("Invalid authentication request."));
		if (!"ACTIVE".equalsIgnoreCase(user.getStatus()) || user.getIs2faEnabled() == null
				|| user.getIs2faEnabled() != 1)
			throw new AuthenticationException("2FA is not enabled for this account.");
		if (!com.ams.security.twofactor.TOTPProvider.validateOTP(user.getTwoFactorSecret(), code))
			throw new AuthenticationException("Invalid 2FA verification code.");
		return createLoginSession(user);
	}

	@Override
	public boolean logout(String sessionToken) {
		if (sessionToken == null || sessionToken.trim().isEmpty())
			return false;
		return authRepository.deleteSession(sessionToken.replaceFirst("^Bearer\\s+", ""));
	}

	@Override
	public void changePassword(Long userId, String oldPassword, String newPassword) {
		if (userId == null || oldPassword == null || newPassword == null || newPassword.length() < 8)
			throw new ValidationException(
					"Valid current and new passwords are required; new password must be at least 8 characters.");
		User user = userRepository.findById(userId).orElseThrow(() -> new AuthenticationException("User not found."));
		if (!passwordEncoder.matches(oldPassword, user.getPasswordHash()))
			throw new AuthenticationException("Current password is incorrect.");
		if (oldPassword.equals(newPassword))
			throw new ValidationException("New password must be different.");
		if (!authRepository.updatePassword(userId, passwordEncoder.encode(newPassword)))
			throw new ValidationException("Password update failed.");
	}
}
