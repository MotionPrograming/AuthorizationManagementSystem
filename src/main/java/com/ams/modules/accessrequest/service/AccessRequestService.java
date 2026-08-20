package com.ams.modules.accessrequest.service;

import java.util.List;

import com.ams.modules.accessrequest.dto.AccessRequestRequest;
import com.ams.modules.accessrequest.dto.AccessRequestResponse;

public interface AccessRequestService {
	AccessRequestResponse createRequest(AccessRequestRequest request);

	AccessRequestResponse getRequestById(Long requestId);

	List<AccessRequestResponse> getRequestsByUserId(Long userId);

	List<AccessRequestResponse> getAllRequests();

	boolean updateRequestStatus(Long requestId, String status);
}