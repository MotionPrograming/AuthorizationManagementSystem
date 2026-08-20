package com.ams.modules.permission.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ams.common.exception.ValidationException;
import com.ams.modules.permission.dto.CreatePermissionRequest;
import com.ams.modules.permission.dto.PermissionResponse;
import com.ams.modules.permission.dto.UpdatePermissionRequest;
import com.ams.modules.permission.entity.Permission;
import com.ams.modules.permission.mapper.PermissionMapper;
import com.ams.modules.permission.repository.PermissionRepository;
import com.ams.modules.permission.service.PermissionService;
import com.ams.modules.permission.validator.PermissionValidator;

public class PermissionServiceImpl implements PermissionService {

	private final PermissionRepository permissionRepository;
	private final PermissionValidator permissionValidator;

	public PermissionServiceImpl(PermissionRepository permissionRepository, PermissionValidator permissionValidator) {
		this.permissionRepository = permissionRepository;
		this.permissionValidator = permissionValidator;
	}

	@Override
	public PermissionResponse createPermission(CreatePermissionRequest request) {
		permissionValidator.validateCreatePermission(request);

		if (permissionRepository.findByPermissionName(request.getPermissionName()).isPresent()) {
			throw new ValidationException("Permission name already exists.");
		}

		Permission permission = new Permission();
		permission.setPermissionName(request.getPermissionName());
		permission.setDescription(request.getDescription());
		permission.setCreatedAt(LocalDateTime.now());

		permissionRepository.save(permission);

		return PermissionMapper.toPermissionResponse(permission);
	}

	@Override
	public PermissionResponse getPermissionById(Long permissionId) {
		Permission permission = permissionRepository.findById(permissionId)
				.orElseThrow(() -> new ValidationException("Permission not found with id: " + permissionId));
		return PermissionMapper.toPermissionResponse(permission);
	}

	@Override
	public List<PermissionResponse> getAllPermissions() {
		return permissionRepository.findAll().stream().map(PermissionMapper::toPermissionResponse)
				.collect(Collectors.toList());
	}

	@Override
	public boolean updatePermission(Long permissionId, UpdatePermissionRequest request) {
		Permission permission = permissionRepository.findById(permissionId)
				.orElseThrow(() -> new ValidationException("Permission not found with id: " + permissionId));

		permission.setPermissionName(request.getPermissionName());
		permission.setDescription(request.getDescription());

		return permissionRepository.update(permission);
	}

	@Override
	public boolean deletePermission(Long permissionId) {
		return permissionRepository.deleteById(permissionId);
	}
}