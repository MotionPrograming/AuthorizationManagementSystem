package com.ams.modules.accessrequest.repository;

import java.util.List;
import java.util.Optional;

import com.ams.modules.accessrequest.entity.AccessRequest;

public interface AccessRequestRepository {
	Optional<AccessRequest> findById(Long requestId);

	List<AccessRequest> findByUserId(Long userId);

	List<AccessRequest> findAll();

	boolean save(AccessRequest request);

	boolean updateStatus(Long requestId, String status);
}