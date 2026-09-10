package com.ams.modules.user.repository;

import java.util.List;
import java.util.Optional;
import com.ams.modules.user.entity.User;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    boolean save(User user);
    boolean update(User user);
    boolean updateLastLogin(Long userId);
    long countUsers();
    boolean deleteById(Long id);
    void assignRoleToUser(Long userId, Long roleId);
}
