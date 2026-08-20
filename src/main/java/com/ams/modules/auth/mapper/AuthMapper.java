package com.ams.modules.auth.mapper;

import com.ams.modules.auth.dto.LoginResponse;
import com.ams.modules.auth.entity.Session;

public class AuthMapper {
	public static LoginResponse toLoginResponse(Session session, String username, String message) {
		return new LoginResponse(session.getSessionToken(), username, message);
	}
}