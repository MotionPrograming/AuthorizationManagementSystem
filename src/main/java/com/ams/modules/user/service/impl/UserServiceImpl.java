package com.ams.modules.user.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ams.common.exception.ValidationException;
import com.ams.modules.user.dto.CreateUserRequest;
import com.ams.modules.user.dto.UpdateUserRequest;
import com.ams.modules.user.dto.UserResponse;
import com.ams.modules.user.entity.User;
import com.ams.modules.user.mapper.UserMapper;
import com.ams.modules.user.repository.UserRepository;
import com.ams.modules.user.service.UserService;
import com.ams.modules.user.validator.UserValidator;
import com.ams.security.password.BCryptPasswordEncoder;
import com.ams.security.password.PasswordEncoder;

public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserValidator userValidator;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, UserValidator userValidator) {
		this.userRepository = userRepository;
		this.userValidator = userValidator;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	public UserServiceImpl(UserRepository userRepository, UserValidator userValidator,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.userValidator = userValidator;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserResponse createUser(CreateUserRequest request) {
		userValidator.validateCreateUser(request);

		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new ValidationException("Username is already taken.");
		}

		// Centralized PasswordEncoder used for hashing
		String hashedPassword = passwordEncoder.encode(request.getPassword());

		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPasswordHash(hashedPassword);
		user.setFullName(request.getFullName());
		user.setStatus("ACTIVE");
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(LocalDateTime.now());

		userRepository.save(user);

		return UserMapper.toUserResponse(user);
	}

	@Override
	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ValidationException("User not found with id: " + id));
		return UserMapper.toUserResponse(user);
	}

	@Override
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream().map(UserMapper::toUserResponse).collect(Collectors.toList());
	}

	@Override
	public boolean updateUser(Long id, UpdateUserRequest request) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ValidationException("User not found with id: " + id));

		user.setEmail(request.getEmail());
		user.setFullName(request.getFullName());
		user.setStatus(request.getStatus());

		return userRepository.update(user);
	}

	@Override
	public boolean deleteUser(Long id) {
		return userRepository.deleteById(id);
	}

	@Override
	public void assignRoleToUser(Long userId, Long roleId) {
		if (userRepository.findById(userId).isEmpty()) {
			throw new ValidationException("User not found with id: " + userId);
		}
		userRepository.assignRoleToUser(userId, roleId);
	}
}