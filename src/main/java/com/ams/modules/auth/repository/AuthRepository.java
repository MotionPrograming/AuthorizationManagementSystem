package com.ams.modules.auth.repository;

import java.util.Optional;

import com.ams.modules.auth.entity.Session;

public interface AuthRepository {
	Optional<Session> findSessionByToken(String sessionToken);

	boolean saveSession(Session session);

	boolean deleteSession(String sessionToken);

	boolean updatePassword(Long userId, String newHashedPassword);
}