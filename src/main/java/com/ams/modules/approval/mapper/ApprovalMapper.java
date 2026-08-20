package com.ams.modules.approval.mapper;

import com.ams.modules.approval.dto.ApprovalResponse;
import com.ams.modules.approval.entity.Approval;

public class ApprovalMapper {

	public static ApprovalResponse toApprovalResponse(Approval approval) {
		if (approval == null)
			return null;
		return new ApprovalResponse(approval.getApprovalId(), approval.getRequestId(), approval.getApproverId(),
				approval.getDecision(), approval.getComments(), approval.getApprovedAt());
	}
}