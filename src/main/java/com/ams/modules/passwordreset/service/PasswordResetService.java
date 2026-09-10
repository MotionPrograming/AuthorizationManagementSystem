package com.ams.modules.passwordreset.service;
public interface PasswordResetService { String requestReset(String email); void resetPassword(String token,String newPassword); }
