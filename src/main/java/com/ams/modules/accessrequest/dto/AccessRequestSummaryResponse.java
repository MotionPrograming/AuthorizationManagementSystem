package com.ams.modules.accessrequest.dto;

public class AccessRequestSummaryResponse {
	private Long requestId;
	private String requestType;
	private String requestStatus;

	public AccessRequestSummaryResponse(Long requestId, String requestType, String requestStatus) {
		this.requestId = requestId;
		this.requestType = requestType;
		this.requestStatus = requestStatus;
	}

	public Long getRequestId() {
		return requestId;
	}

	public String getRequestType() {
		return requestType;
	}

	public String getRequestStatus() {
		return requestStatus;
	}
}