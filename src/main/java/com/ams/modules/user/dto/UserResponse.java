package com.ams.modules.user.dto;

import java.time.LocalDateTime;

public class UserResponse {
	private Long userId;
	private String username;
	private String email;
	private String fullName;
	private String status;
	private LocalDateTime createdAt;

	public UserResponse(Long userId, String username, String email, String fullName, String status,
			LocalDateTime createdAt) {
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.fullName = fullName;
		this.status = status;
		this.createdAt = createdAt;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getFullName() {
		return fullName;
	}

	public String getStatus() {
		return status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}