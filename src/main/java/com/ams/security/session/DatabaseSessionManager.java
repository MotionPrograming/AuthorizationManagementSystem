package com.ams.security.session;

import java.time.LocalDateTime;
import com.ams.security.crypto.JwtTokenProvider;
import com.ams.modules.auth.entity.Session;
import com.ams.modules.auth.repository.impl.AuthRepositoryImpl;

/** Database-backed session manager used by the reusable security service. */
public class DatabaseSessionManager implements SessionManager {
    private final AuthRepositoryImpl repository;
    private final int timeoutHours;
    public DatabaseSessionManager() { this(new AuthRepositoryImpl(), 8); }
    public DatabaseSessionManager(AuthRepositoryImpl repository, int timeoutHours) { this.repository=repository; this.timeoutHours=timeoutHours; }
    @Override public String createSession(Long userId, String username) {
        String token=JwtTokenProvider.generateToken(username,userId); LocalDateTime now=LocalDateTime.now();
        repository.saveSession(new Session(userId,token,now,now.plusHours(timeoutHours))); return token;
    }
    @Override public void invalidateSession(String sessionId) { if(sessionId!=null) repository.deleteSession(sessionId.replaceFirst("^Bearer\\s+","")); }
}
