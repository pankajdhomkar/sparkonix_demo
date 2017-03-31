package com.sparkonix.entity.dto;

import java.util.Date;

public class IssueSearchFilterPayloadDTO {

	private long customerId;
	private Date startDate;
	private Date endDate;
	private long manResId;
	private String manResRole;

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public long getManResId() {
		return manResId;
	}

	public void setManResId(long manResId) {
		this.manResId = manResId;
	}

	public String getManResRole() {
		return manResRole;
	}

	public void setManResRole(String manResRole) {
		this.manResRole = manResRole;
	}

}
