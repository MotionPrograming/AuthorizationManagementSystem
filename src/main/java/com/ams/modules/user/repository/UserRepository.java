package com.ams.modules.user.repository;

import java.util.List;
import java.util.Optional;

import com.ams.modules.user.entity.User;

public interface UserRepository {
	Optional<User> findById(Long id);

	Optional<User> findByUsername(String username);

	List<User> findAll();

	boolean save(User user);

	boolean update(User user);

	boolean deleteById(Long id);
}