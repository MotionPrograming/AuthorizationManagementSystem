package com.ams.modules.passwordreset.repository;
import java.util.Optional;
public interface PasswordResetRepository { boolean save(Long userId,String token,String hashToken,java.time.LocalDateTime expiry); Optional<Long> findValidUserId(String hashToken); boolean markUsed(String hashToken); }
