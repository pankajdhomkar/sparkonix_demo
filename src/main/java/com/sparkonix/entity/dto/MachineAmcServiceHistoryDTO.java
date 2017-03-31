package com.sparkonix.entity.dto;

import java.util.Date;

import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.CompanyLocation;

public class MachineAmcServiceHistoryDTO {

	private long id; // machineAmcServiceHistoryId;
	private long machineId;
	private String details;
	private String modelNumber;
	private Date servicingAssignedDate;
	private Date servicingDoneDate;
	private String assignedToName;
	private String assignedToEmail;
	private String assignedToMobile;
	private String actionTaken;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMachineId() {
		return machineId;
	}

	public void setMachineId(long machineId) {
		this.machineId = machineId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public Date getServicingAssignedDate() {
		return servicingAssignedDate;
	}

	public void setServicingAssignedDate(Date servicingAssignedDate) {
		this.servicingAssignedDate = servicingAssignedDate;
	}

	public Date getServicingDoneDate() {
		return servicingDoneDate;
	}

	public void setServicingDoneDate(Date servicingDoneDate) {
		this.servicingDoneDate = servicingDoneDate;
	}

	public String getAssignedToName() {
		return assignedToName;
	}

	public void setAssignedToName(String assignedToName) {
		this.assignedToName = assignedToName;
	}

	public String getAssignedToEmail() {
		return assignedToEmail;
	}

	public void setAssignedToEmail(String assignedToEmail) {
		this.assignedToEmail = assignedToEmail;
	}

	public String getAssignedToMobile() {
		return assignedToMobile;
	}

	public void setAssignedToMobile(String assignedToMobile) {
		this.assignedToMobile = assignedToMobile;
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

}
