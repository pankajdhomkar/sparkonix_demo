package com.sparkonix.entity.dto;

import java.util.Date;

import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.CompanyLocation;

public class MachineDetailsDTO {

	private long machineId;
	private String serialNumber;
	private String modelNumber;
	private Date installationDate;
	private Date lastServiceDate;
	private String curAmcType;
	private Date curAmcStartDate;
	private Date curAmcEndDate;
	private String curAmcStatus;
	private String machineDocuments;//
	private CompanyDetail manufacturer;
	private CompanyDetail reseller;
	private CompanyLocation location;

	public long getMachineId() {
		return machineId;
	}

	public void setMachineId(long machineId) {
		this.machineId = machineId;
	}

	public CompanyDetail getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(CompanyDetail manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public Date getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(Date installationDate) {
		this.installationDate = installationDate;
	}

	public Date getLastServiceDate() {
		return lastServiceDate;
	}

	public void setLastServiceDate(Date lastServiceDate) {
		this.lastServiceDate = lastServiceDate;
	}

	public String getMachineDocuments() {
		return machineDocuments;
	}

	public void setMachineDocuments(String machineDocuments) {
		this.machineDocuments = machineDocuments;
	}

	public String getCurAmcType() {
		return curAmcType;
	}

	public void setCurAmcType(String curAmcType) {
		this.curAmcType = curAmcType;
	}

	public Date getCurAmcStartDate() {
		return curAmcStartDate;
	}

	public void setCurAmcStartDate(Date curAmcStartDate) {
		this.curAmcStartDate = curAmcStartDate;
	}

	public Date getCurAmcEndDate() {
		return curAmcEndDate;
	}

	public void setCurAmcEndDate(Date curAmcEndDate) {
		this.curAmcEndDate = curAmcEndDate;
	}

	public String getCurAmcStatus() {
		return curAmcStatus;
	}

	public void setCurAmcStatus(String curAmcStatus) {
		this.curAmcStatus = curAmcStatus;
	}

	public CompanyDetail getReseller() {
		return reseller;
	}

	public void setReseller(CompanyDetail reseller) {
		this.reseller = reseller;
	}

	public CompanyLocation getLocation() {
		return location;
	}

	public void setLocation(CompanyLocation location) {
		this.location = location;
	}

}
