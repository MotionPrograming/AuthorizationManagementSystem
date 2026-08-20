package com.ams.modules.auth.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import com.ams.common.exception.AuthenticationException;
import com.ams.modules.auth.dto.LoginRequest;
import com.ams.modules.auth.dto.LoginResponse;
import com.ams.modules.auth.entity.Session;
import com.ams.modules.auth.mapper.AuthMapper;
import com.ams.modules.auth.repository.AuthRepository;
import com.ams.modules.auth.service.AuthService;
import com.ams.modules.auth.validator.AuthValidator;
import com.ams.modules.user.entity.User;
import com.ams.modules.user.repository.UserRepository;

public class AuthServiceImpl implements AuthService {

	private final AuthRepository authRepository;
	private final UserRepository userRepository;
	private final AuthValidator authValidator;

	public AuthServiceImpl(AuthRepository authRepository, UserRepository userRepository, AuthValidator authValidator) {
		this.authRepository = authRepository;
		this.userRepository = userRepository;
		this.authValidator = authValidator;
	}

	@Override
	public LoginResponse login(LoginRequest request) {
		// ১. ইনপুট ডাটা ভ্যালিডেশন
		authValidator.validateLogin(request);

		// ২. ইউজার ডাটাবেজে আছে কিনা চেক
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AuthenticationException("Invalid username or password."));

		// ৩. পাসওয়ার্ড ম্যাচ করা (BCrypt)
		if (!BCrypt.checkpw(request.getPassword(), user.getPasswordHash())) {
			throw new AuthenticationException("Invalid username or password.");
		}

		// ৪. সেশন টোকেন জেনারেট এবং সেভ করা (user.getUserId() আপডেট করা হয়েছে)
		String token = UUID.randomUUID().toString();
		Session session = new Session(user.getUserId(), token, LocalDateTime.now(), LocalDateTime.now().plusHours(8));

		authRepository.saveSession(session);

		// ৫. রেসপন্স রিটার্ন
		return AuthMapper.toLoginResponse(session, user.getUsername(), "Login successful");
	}

	@Override
	public boolean logout(String sessionToken) {
		if (sessionToken == null || sessionToken.trim().isEmpty()) {
			return false;
		}
		return authRepository.deleteSession(sessionToken);
	}
}