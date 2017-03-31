package com.sparkonix.entity.dto;

import java.util.Date;

public class SubscriptionReportDTO {

	private String machineModel;
	private String machineSerial;
	private String customerCompanyName;
	private String customerCompanyPan;
	private String customerLocation;

	private Date attendMeSubStartDate;
	private Date attendMeSubEndDate;

	private Date machineInstallationDate;
	private Date machineWarrantyExpiryDate;

	private Date machineAmcStartDate;
	private Date machineAmcEndDate;

	public String getMachineModel() {
		return machineModel;
	}

	public void setMachineModel(String machineModel) {
		this.machineModel = machineModel;
	}

	public String getMachineSerial() {
		return machineSerial;
	}

	public void setMachineSerial(String machineSerial) {
		this.machineSerial = machineSerial;
	}

	public String getCustomerCompanyName() {
		return customerCompanyName;
	}

	public void setCustomerCompanyName(String customerCompanyName) {
		this.customerCompanyName = customerCompanyName;
	}

	public String getCustomerCompanyPan() {
		return customerCompanyPan;
	}

	public void setCustomerCompanyPan(String customerCompanyPan) {
		this.customerCompanyPan = customerCompanyPan;
	}

	public Date getAttendMeSubStartDate() {
		return attendMeSubStartDate;
	}

	public void setAttendMeSubStartDate(Date attendMeSubStartDate) {
		this.attendMeSubStartDate = attendMeSubStartDate;
	}

	public Date getAttendMeSubEndDate() {
		return attendMeSubEndDate;
	}

	public void setAttendMeSubEndDate(Date attendMeSubEndDate) {
		this.attendMeSubEndDate = attendMeSubEndDate;
	}

	public Date getMachineInstallationDate() {
		return machineInstallationDate;
	}

	public void setMachineInstallationDate(Date machineInstallationDate) {
		this.machineInstallationDate = machineInstallationDate;
	}

	public Date getMachineWarrantyExpiryDate() {
		return machineWarrantyExpiryDate;
	}

	public void setMachineWarrantyExpiryDate(Date machineWarrantyExpiryDate) {
		this.machineWarrantyExpiryDate = machineWarrantyExpiryDate;
	}

	public Date getMachineAmcStartDate() {
		return machineAmcStartDate;
	}

	public void setMachineAmcStartDate(Date machineAmcStartDate) {
		this.machineAmcStartDate = machineAmcStartDate;
	}

	public Date getMachineAmcEndDate() {
		return machineAmcEndDate;
	}

	public void setMachineAmcEndDate(Date machineAmcEndDate) {
		this.machineAmcEndDate = machineAmcEndDate;
	}

	public String getCustomerLocation() {
		return customerLocation;
	}

	public void setCustomerLocation(String customerLocation) {
		this.customerLocation = customerLocation;
	}

}
