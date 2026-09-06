package com.ams.security.session;

public interface SessionManager {
	String createSession(Long userId, String username);

	void invalidateSession(String sessionId);
}