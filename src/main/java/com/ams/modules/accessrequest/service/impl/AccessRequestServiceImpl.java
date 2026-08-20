package com.ams.modules.accessrequest.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ams.common.exception.ValidationException;
import com.ams.modules.accessrequest.dto.AccessRequestRequest;
import com.ams.modules.accessrequest.dto.AccessRequestResponse;
import com.ams.modules.accessrequest.entity.AccessRequest;
import com.ams.modules.accessrequest.mapper.AccessRequestMapper;
import com.ams.modules.accessrequest.repository.AccessRequestRepository;
import com.ams.modules.accessrequest.service.AccessRequestService;
import com.ams.modules.accessrequest.validator.AccessRequestValidator;

public class AccessRequestServiceImpl implements AccessRequestService {

	private final AccessRequestRepository repository;
	private final AccessRequestValidator validator;

	public AccessRequestServiceImpl(AccessRequestRepository repository, AccessRequestValidator validator) {
		this.repository = repository;
		this.validator = validator;
	}

	@Override
	public AccessRequestResponse createRequest(AccessRequestRequest request) {
		validator.validate(request);

		AccessRequest accessRequest = new AccessRequest();
		accessRequest.setUserId(request.getUserId());
		accessRequest.setRequestType(request.getRequestType());
		accessRequest.setRequestReason(request.getRequestReason());
		accessRequest.setRequestStatus("PENDING");
		accessRequest.setCreatedAt(LocalDateTime.now());

		repository.save(accessRequest);

		return AccessRequestMapper.toResponse(accessRequest);
	}

	@Override
	public AccessRequestResponse getRequestById(Long requestId) {
		AccessRequest request = repository.findById(requestId)
				.orElseThrow(() -> new ValidationException("Access request not found with id: " + requestId));
		return AccessRequestMapper.toResponse(request);
	}

	@Override
	public List<AccessRequestResponse> getRequestsByUserId(Long userId) {
		return repository.findByUserId(userId).stream().map(AccessRequestMapper::toResponse)
				.collect(Collectors.toList());
	}

	@Override
	public List<AccessRequestResponse> getAllRequests() {
		return repository.findAll().stream().map(AccessRequestMapper::toResponse).collect(Collectors.toList());
	}

	@Override
	public boolean updateRequestStatus(Long requestId, String status) {
		if (!"PENDING".equals(status) && !"APPROVED".equals(status) && !"REJECTED".equals(status)) {
			throw new ValidationException("Invalid status provided.");
		}
		return repository.updateStatus(requestId, status);
	}
}