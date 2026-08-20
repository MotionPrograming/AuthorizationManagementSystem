package com.ams.modules.approval.service;

import java.util.List;

import com.ams.modules.approval.dto.ApprovalResponse;
import com.ams.modules.approval.dto.CreateApprovalRequest;

public interface ApprovalService {
	ApprovalResponse processApproval(CreateApprovalRequest request);

	ApprovalResponse getApprovalById(Long approvalId);

	ApprovalResponse getApprovalByRequestId(Long requestId);

	List<ApprovalResponse> getApprovalsByApproverId(Long approverId);

	List<ApprovalResponse> getAllApprovals();
}