package com.ams.modules.user.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;

import com.ams.common.exception.ValidationException;
import com.ams.modules.user.dto.CreateUserRequest;
import com.ams.modules.user.dto.UpdateUserRequest;
import com.ams.modules.user.dto.UserResponse;
import com.ams.modules.user.entity.User;
import com.ams.modules.user.mapper.UserMapper;
import com.ams.modules.user.repository.UserRepository;
import com.ams.modules.user.service.UserService;
import com.ams.modules.user.validator.UserValidator;

public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserValidator userValidator;

	public UserServiceImpl(UserRepository userRepository, UserValidator userValidator) {
		this.userRepository = userRepository;
		this.userValidator = userValidator;
	}

	@Override
	public UserResponse createUser(CreateUserRequest request) {
		userValidator.validateCreateUser(request);

		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new ValidationException("Username is already taken.");
		}

		String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

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
}