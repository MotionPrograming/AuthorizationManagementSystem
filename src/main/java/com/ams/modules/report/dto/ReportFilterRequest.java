package com.ams.modules.report.dto;

import java.time.LocalDate;

public class ReportFilterRequest {
	private LocalDate startDate;
	private LocalDate endDate;

	public ReportFilterRequest() {
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}