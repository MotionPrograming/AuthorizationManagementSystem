package com.ams.modules.auth.service;

import com.ams.modules.auth.dto.LoginRequest;
import com.ams.modules.auth.dto.LoginResponse;

public interface AuthService {
	LoginResponse login(LoginRequest request);

	boolean logout(String sessionToken);
}