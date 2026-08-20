package com.ams.modules.auth.entity;

import java.time.LocalDateTime;

public class Session {
	private Long id;
	private Long userId;
	private String sessionToken;
	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;

	public Session() {
	}

	public Session(Long userId, String sessionToken, LocalDateTime createdAt, LocalDateTime expiresAt) {
		this.userId = userId;
		this.sessionToken = sessionToken;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
}