package com.ams.modules.audit.repository;

import java.util.List;
import java.util.Optional;

import com.ams.modules.audit.entity.AuditLog;

public interface AuditRepository {
	Optional<AuditLog> findById(Long auditId);

	List<AuditLog> findByUserId(Long userId);

	List<AuditLog> findAll();

	boolean save(AuditLog auditLog);
}