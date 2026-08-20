package com.ams.modules.approval.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ams.common.exception.ValidationException;
import com.ams.modules.approval.dto.ApprovalResponse;
import com.ams.modules.approval.dto.CreateApprovalRequest;
import com.ams.modules.approval.entity.Approval;
import com.ams.modules.approval.mapper.ApprovalMapper;
import com.ams.modules.approval.repository.ApprovalRepository;
import com.ams.modules.approval.service.ApprovalService;
import com.ams.modules.approval.validator.ApprovalValidator;

public class ApprovalServiceImpl implements ApprovalService {

	private final ApprovalRepository approvalRepository;
	private final ApprovalValidator approvalValidator;

	public ApprovalServiceImpl(ApprovalRepository approvalRepository, ApprovalValidator approvalValidator) {
		this.approvalRepository = approvalRepository;
		this.approvalValidator = approvalValidator;
	}

	@Override
	public ApprovalResponse processApproval(CreateApprovalRequest request) {
		approvalValidator.validateCreateApproval(request);

		if (approvalRepository.findByRequestId(request.getRequestId()).isPresent()) {
			throw new ValidationException("An approval decision has already been recorded for this request ID.");
		}

		Approval approval = new Approval();
		approval.setRequestId(request.getRequestId());
		approval.setApproverId(request.getApproverId());
		approval.setDecision(request.getDecision());
		approval.setComments(request.getComments());
		approval.setApprovedAt(LocalDateTime.now());

		approvalRepository.save(approval);

		return ApprovalMapper.toApprovalResponse(approval);
	}

	@Override
	public ApprovalResponse getApprovalById(Long approvalId) {
		Approval approval = approvalRepository.findById(approvalId)
				.orElseThrow(() -> new ValidationException("Approval record not found with id: " + approvalId));
		return ApprovalMapper.toApprovalResponse(approval);
	}

	@Override
	public ApprovalResponse getApprovalByRequestId(Long requestId) {
		Approval approval = approvalRepository.findByRequestId(requestId)
				.orElseThrow(() -> new ValidationException("Approval record not found for request id: " + requestId));
		return ApprovalMapper.toApprovalResponse(approval);
	}

	@Override
	public List<ApprovalResponse> getApprovalsByApproverId(Long approverId) {
		return approvalRepository.findByApproverId(approverId).stream().map(ApprovalMapper::toApprovalResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<ApprovalResponse> getAllApprovals() {
		return approvalRepository.findAll().stream().map(ApprovalMapper::toApprovalResponse)
				.collect(Collectors.toList());
	}
}