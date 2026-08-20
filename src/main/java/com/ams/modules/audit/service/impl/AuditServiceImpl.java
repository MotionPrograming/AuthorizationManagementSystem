package com.ams.modules.audit.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ams.common.exception.ValidationException;
import com.ams.modules.audit.dto.AuditLogResponse;
import com.ams.modules.audit.dto.CreateAuditLogRequest;
import com.ams.modules.audit.entity.AuditLog;
import com.ams.modules.audit.mapper.AuditMapper;
import com.ams.modules.audit.repository.AuditRepository;
import com.ams.modules.audit.service.AuditService;
import com.ams.modules.audit.validator.AuditValidator;

public class AuditServiceImpl implements AuditService {

	private final AuditRepository auditRepository;
	private final AuditValidator auditValidator;

	public AuditServiceImpl(AuditRepository auditRepository, AuditValidator auditValidator) {
		this.auditRepository = auditRepository;
		this.auditValidator = auditValidator;
	}

	@Override
	public AuditLogResponse logAction(CreateAuditLogRequest request) {
		auditValidator.validateCreateAuditLog(request);

		AuditLog auditLog = new AuditLog();
		auditLog.setUserId(request.getUserId());
		auditLog.setAction(request.getAction());
		auditLog.setDescription(request.getDescription());
		auditLog.setIpAddress(request.getIpAddress());
		auditLog.setCreatedAt(LocalDateTime.now());

		auditRepository.save(auditLog);

		return AuditMapper.toAuditLogResponse(auditLog);
	}

	@Override
	public AuditLogResponse getAuditById(Long auditId) {
		AuditLog auditLog = auditRepository.findById(auditId)
				.orElseThrow(() -> new ValidationException("Audit log not found with id: " + auditId));
		return AuditMapper.toAuditLogResponse(auditLog);
	}

	@Override
	public List<AuditLogResponse> getAuditsByUserId(Long userId) {
		return auditRepository.findByUserId(userId).stream().map(AuditMapper::toAuditLogResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<AuditLogResponse> getAllAudits() {
		return auditRepository.findAll().stream().map(AuditMapper::toAuditLogResponse).collect(Collectors.toList());
	}
}