package com.ams.modules.auth.service;

import com.ams.modules.auth.dto.LoginRequest;
import com.ams.modules.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse completeTwoFactorLogin(Long userId, int code);
    boolean logout(String sessionToken);
    void changePassword(Long userId, String oldPassword, String newPassword);
}
