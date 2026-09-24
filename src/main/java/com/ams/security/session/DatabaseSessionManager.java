package com.ams.security.session;

import java.time.LocalDateTime;
import java.util.Optional;

import com.ams.modules.auth.entity.Session;
import com.ams.modules.auth.repository.AuthRepository;
import com.ams.modules.auth.repository.impl.AuthRepositoryImpl;
import com.ams.security.crypto.JwtTokenProvider;

/**
 * Database-backed session manager used by the reusable security service.
 */
public class DatabaseSessionManager implements SessionManager {

	private final AuthRepository repository;
	private final int timeoutHours;

	public DatabaseSessionManager() {
		this(new AuthRepositoryImpl(), 8);
	}

	public DatabaseSessionManager(AuthRepository repository, int timeoutHours) {
		this.repository = repository;
		this.timeoutHours = timeoutHours;
	}

	@Override
	public String createSession(Long userId, String username) {
		String token = JwtTokenProvider.generateToken(username, userId);
		LocalDateTime now = LocalDateTime.now();

		// 기존 4-arg Constructor ব্যবহার: Session(userId, token, createdAt, expiresAt)
		Session session = new Session(userId, token, now, now.plusHours(timeoutHours));
		repository.saveSession(session);

		return token;
	}

	@Override
	public void invalidateSession(String token) {
		if (token != null && !token.isBlank()) {
			String cleanToken = cleanBearerToken(token);
			repository.deleteSession(cleanToken);
		}
	}

	public Optional<Session> getValidSession(String token) {
		if (token == null || token.isBlank()) {
			return Optional.empty();
		}

		String cleanToken = cleanBearerToken(token);
		Optional<Session> sessionOpt = repository.findSessionByToken(cleanToken);

		if (sessionOpt.isEmpty()) {
			return Optional.empty();
		}

		Session session = sessionOpt.get();

		// isExpired() এর বদলে ম্যানুয়ালি Expiry চেক করা
		if (session.getExpiresAt() == null || LocalDateTime.now().isAfter(session.getExpiresAt())) {
			return Optional.empty();
		}

		return sessionOpt;
	}

	private String cleanBearerToken(String token) {
		return token.replaceFirst("^Bearer\\s+", "").trim();
	}
}