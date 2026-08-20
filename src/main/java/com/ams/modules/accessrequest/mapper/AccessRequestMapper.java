package com.ams.modules.accessrequest.mapper;

import com.ams.modules.accessrequest.dto.AccessRequestResponse;
import com.ams.modules.accessrequest.dto.AccessRequestSummaryResponse;
import com.ams.modules.accessrequest.entity.AccessRequest;

public class AccessRequestMapper {

	public static AccessRequestResponse toResponse(AccessRequest request) {
		if (request == null)
			return null;
		return new AccessRequestResponse(request.getRequestId(), request.getUserId(), request.getRequestType(),
				request.getRequestStatus(), request.getRequestReason(), request.getCreatedAt());
	}

	public static AccessRequestSummaryResponse toSummaryResponse(AccessRequest request) {
		if (request == null)
			return null;
		return new AccessRequestSummaryResponse(request.getRequestId(), request.getRequestType(),
				request.getRequestStatus());
	}
}