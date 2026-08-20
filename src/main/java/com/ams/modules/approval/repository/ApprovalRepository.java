package com.ams.modules.approval.repository;

import java.util.List;
import java.util.Optional;

import com.ams.modules.approval.entity.Approval;

public interface ApprovalRepository {
	Optional<Approval> findById(Long approvalId);

	Optional<Approval> findByRequestId(Long requestId);

	List<Approval> findByApproverId(Long approverId);

	List<Approval> findAll();

	boolean save(Approval approval);
}