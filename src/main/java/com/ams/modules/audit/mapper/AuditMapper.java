package com.ams.modules.audit.mapper;

import com.ams.modules.audit.dto.AuditLogResponse;
import com.ams.modules.audit.entity.AuditLog;

public class AuditMapper {

	public static AuditLogResponse toAuditLogResponse(AuditLog auditLog) {
		if (auditLog == null)
			return null;
		return new AuditLogResponse(auditLog.getAuditId(), auditLog.getUserId(), auditLog.getAction(),
				auditLog.getDescription(), auditLog.getIpAddress(), auditLog.getCreatedAt());
	}
}