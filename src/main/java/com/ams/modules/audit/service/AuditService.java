package com.ams.modules.audit.service;

import java.util.List;

import com.ams.modules.audit.dto.AuditLogResponse;
import com.ams.modules.audit.dto.CreateAuditLogRequest;

public interface AuditService {
	AuditLogResponse logAction(CreateAuditLogRequest request);

	AuditLogResponse getAuditById(Long auditId);

	List<AuditLogResponse> getAuditsByUserId(Long userId);

	List<AuditLogResponse> getAllAudits();
}