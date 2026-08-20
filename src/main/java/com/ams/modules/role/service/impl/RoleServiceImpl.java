package com.ams.modules.role.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ams.common.exception.ValidationException;
import com.ams.modules.role.dto.CreateRoleRequest;
import com.ams.modules.role.dto.RoleResponse;
import com.ams.modules.role.dto.UpdateRoleRequest;
import com.ams.modules.role.entity.Role;
import com.ams.modules.role.mapper.RoleMapper;
import com.ams.modules.role.repository.RoleRepository;
import com.ams.modules.role.service.RoleService;
import com.ams.modules.role.validator.RoleValidator;

public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;
	private final RoleValidator roleValidator;

	public RoleServiceImpl(RoleRepository roleRepository, RoleValidator roleValidator) {
		this.roleRepository = roleRepository;
		this.roleValidator = roleValidator;
	}

	@Override
	public RoleResponse createRole(CreateRoleRequest request) {
		roleValidator.validateCreateRole(request);

		if (roleRepository.findByRoleName(request.getRoleName()).isPresent()) {
			throw new ValidationException("Role name already exists.");
		}

		Role role = new Role();
		role.setRoleName(request.getRoleName());
		role.setDescription(request.getDescription());
		role.setCreatedAt(LocalDateTime.now());

		roleRepository.save(role);

		return RoleMapper.toRoleResponse(role);
	}

	@Override
	public RoleResponse getRoleById(Long roleId) {
		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new ValidationException("Role not found with id: " + roleId));
		return RoleMapper.toRoleResponse(role);
	}

	@Override
	public List<RoleResponse> getAllRoles() {
		return roleRepository.findAll().stream().map(RoleMapper::toRoleResponse).collect(Collectors.toList());
	}

	@Override
	public boolean updateRole(Long roleId, UpdateRoleRequest request) {
		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new ValidationException("Role not found with id: " + roleId));

		role.setRoleName(request.getRoleName());
		role.setDescription(request.getDescription());

		return roleRepository.update(role);
	}

	@Override
	public boolean deleteRole(Long roleId) {
		return roleRepository.deleteById(roleId);
	}
}