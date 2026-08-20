package com.ams.modules.user.service;

import java.util.List;

import com.ams.modules.user.dto.CreateUserRequest;
import com.ams.modules.user.dto.UpdateUserRequest;
import com.ams.modules.user.dto.UserResponse;

public interface UserService {
	UserResponse createUser(CreateUserRequest request);

	UserResponse getUserById(Long id);

	List<UserResponse> getAllUsers();

	boolean updateUser(Long id, UpdateUserRequest request);

	boolean deleteUser(Long id);
}