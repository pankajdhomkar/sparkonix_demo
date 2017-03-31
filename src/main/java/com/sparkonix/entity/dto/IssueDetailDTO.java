package com.sparkonix.entity.dto;

import java.util.Date;

public class IssueDetailDTO {

	private String issueNumber;             // issues
	private String machineSerialNumber;     // issue - machine
	private String manufacturerName;        // issue - machine - company_details table
	private String complaintDetail;         // issues
	private Date loggedDate;              // issues
	private String operatorName;            // issues - phone_device 
	private String operatorContactNumber;   // issues- phone_device
	private String attendedBy;               // issues - users   
	private String attendedByContactNumber;  // issues - users
	
	public String getIssueNumber() {
		return issueNumber;
	}
	public void setIssueNumber(String issueNumber) {
		this.issueNumber = issueNumber;
	}
	public String getMachineSerialNumber() {
		return machineSerialNumber;
	}
	public void setMachineSerialNumber(String machineSerialNumber) {
		this.machineSerialNumber = machineSerialNumber;
	}
	public String getManufacturerName() {
		return manufacturerName;
	}
	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}
	public String getComplaintDetail() {
		return complaintDetail;
	}
	public void setComplaintDetail(String complaintDetail) {
		this.complaintDetail = complaintDetail;
	}
	public Date getLoggedDate() {
		return loggedDate;
	}
	public void setLoggedDate(Date loggedDate) {
		this.loggedDate = loggedDate;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getOperatorContactNumber() {
		return operatorContactNumber;
	}
	public void setOperatorContactNumber(String operatorContactNumber) {
		this.operatorContactNumber = operatorContactNumber;
	}
	public String getAttendedBy() {
		return attendedBy;
	}
	public void setAttendedBy(String attendedBy) {
		this.attendedBy = attendedBy;
	}
	public String getAttendedByContactNumber() {
		return attendedByContactNumber;
	}
	public void setAttendedByContactNumber(String attendedByContactNumber) {
		this.attendedByContactNumber = attendedByContactNumber;
	}


}
