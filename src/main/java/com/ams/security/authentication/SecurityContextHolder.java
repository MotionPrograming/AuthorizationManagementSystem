package com.ams.security.authentication;

import com.ams.modules.user.dto.UserResponse;

public class SecurityContextHolder {

	private static final ThreadLocal<UserResponse> context = new ThreadLocal<>();

	public static void setContext(UserResponse user) {
		context.set(user);
	}

	public static UserResponse getContext() {
		return context.get();
	}

	public static void clearContext() {
		context.remove();
	}
}