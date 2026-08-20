package com.ams.modules.role.repository;

import java.util.List;
import java.util.Optional;

import com.ams.modules.role.entity.Role;

public interface RoleRepository {
	Optional<Role> findById(Long roleId);

	Optional<Role> findByRoleName(String roleName);

	List<Role> findAll();

	boolean save(Role role);

	boolean update(Role role);

	boolean deleteById(Long roleId);
}