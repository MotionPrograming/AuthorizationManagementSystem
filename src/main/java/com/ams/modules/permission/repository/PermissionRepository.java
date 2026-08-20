package com.ams.modules.permission.repository;

import java.util.List;
import java.util.Optional;

import com.ams.modules.permission.entity.Permission;

public interface PermissionRepository {
	Optional<Permission> findById(Long permissionId);

	Optional<Permission> findByPermissionName(String permissionName);

	List<Permission> findAll();

	boolean save(Permission permission);

	boolean update(Permission permission);

	boolean deleteById(Long permissionId);
}