package com.sparkonix.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "issues")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.Issue.findAll", query = "SELECT i FROM Issue i"),
		@NamedQuery(name = "com.sparkonix.entity.Issue.findByCompanyID", query = "SELECT i FROM Issue i where i.machineId in "
				+ "(SELECT id FROM Machine m where m.manufacturerId =:COMPANYID or m.resellerId =:COMPANYID)"),
		@NamedQuery(name = "com.sparkonix.entity.Issue.findByTechnicianID", query = "SELECT i FROM Issue i "
				+ "WHERE i.assignedTo = :TECHID"),

		@NamedQuery(name = "com.sparkonix.entity.Issue.findAllByAssignedToId", query = "SELECT i FROM Issue i "
				+ "WHERE i.assignedTo = :ASSIGNEDTO_ID"),
		@NamedQuery(name = "com.sparkonix.entity.Issue.findAllBySupportAssitanaceAndManId", query = "SELECT i FROM Issue i "
				+ "WHERE i.machineSupportAssistance = :SUPPORT_ASSISTANCE AND manufacturerId =:MANUFACTURER_ID"),
		@NamedQuery(name = "com.sparkonix.entity.Issue.findAllBySupportAssitanaceAndResId", query = "SELECT i FROM Issue i "
				+ "WHERE i.machineSupportAssistance = :SUPPORT_ASSISTANCE AND resellerId =:RESELLER_ID"),

		@NamedQuery(name = "com.sparkonix.entity.Issue.findByMachineID", query = "SELECT i FROM Issue i "
				+ "WHERE i.machineId = :MACHINEID") })

public class Issue implements Serializable {

	private static final long serialVersionUID = -1014068216430547375L;

	public static enum ISSUE_STATUS {
		OPEN, ASSIGNED, CLOSED
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "issue_number", nullable = false)
	private String issueNumber;

	@Column(name = "machine_id", nullable = false)
	private long machineId;

	@Column(name = "reporting_device")
	private long reportingDevice;

	@Column(name = "details")
	private String details;

	@Column(name = "status")
	private String status;

	@Column(name = "date_reported")
	private Date dateReported;

	@Column(name = "date_assigned")
	private Date dateAssigned;

	@Column(name = "date_closed")
	private Date dateClosed;

	// fk to users table
	@Column(name = "assigned_to")
	private long assignedTo;

	@Column(name = "failure_reason")
	private String failureReason;

	@Column(name = "action_taken")
	private String actionTaken;

	@Column(name = "phonedevice_operator_name")
	private String phonedeviceOperatorName;

	@Column(name = "machine_location_id")
	private long machineLocationId;

	@Column(name = "machine_location_name_address")
	private String machineLocationNameAddress;

	@Column(name = "customer_id")
	private long customerId;

	@Column(name = "customer_company_name")
	private String customerCompanyName;

	@Column(name = "manufacturer_id")
	private long manufacturerId;

	@Column(name = "reseller_id")
	private long resellerId;

	@Column(name = "machine_support_assistance")
	private String machineSupportAssistance;

	@Column(name = "machine_model_number")
	private String machineModelNumber;

	@Column(name = "machine_serial_number")
	private String machineSerialNumber;

	@Column(name = "machine_installation_date")
	private Date machineInstallationDate;

	@Column(name = "technician_name")
	private String technicianName;

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

	public long getReportingDevice() {
		return reportingDevice;
	}

	public void setReportingDevice(long reportingDevice) {
		this.reportingDevice = reportingDevice;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateReported() {
		return dateReported;
	}

	public void setDateReported(Date dateReported) {
		this.dateReported = dateReported;
	}

	public Date getDateAssigned() {
		return dateAssigned;
	}

	public void setDateAssigned(Date dateAssigned) {
		this.dateAssigned = dateAssigned;
	}

	public Date getDateClosed() {
		return dateClosed;
	}

	public void setDateClosed(Date dateClosed) {
		this.dateClosed = dateClosed;
	}

	public long getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(long assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	public String getIssueNumber() {
		return issueNumber;
	}

	public void setIssueNumber(String issueNumber) {
		this.issueNumber = issueNumber;
	}

	public String getPhonedeviceOperatorName() {
		return phonedeviceOperatorName;
	}

	public void setPhonedeviceOperatorName(String phonedeviceOperatorName) {
		this.phonedeviceOperatorName = phonedeviceOperatorName;
	}

	public long getMachineLocationId() {
		return machineLocationId;
	}

	public void setMachineLocationId(long machineLocationId) {
		this.machineLocationId = machineLocationId;
	}

	public String getMachineLocationNameAddress() {
		return machineLocationNameAddress;
	}

	public void setMachineLocationNameAddress(String machineLocationNameAddress) {
		this.machineLocationNameAddress = machineLocationNameAddress;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getCustomerCompanyName() {
		return customerCompanyName;
	}

	public void setCustomerCompanyName(String customerCompanyName) {
		this.customerCompanyName = customerCompanyName;
	}

	public long getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(long manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public long getResellerId() {
		return resellerId;
	}

	public void setResellerId(long resellerId) {
		this.resellerId = resellerId;
	}

	public String getMachineSupportAssistance() {
		return machineSupportAssistance;
	}

	public void setMachineSupportAssistance(String machineSupportAssistance) {
		this.machineSupportAssistance = machineSupportAssistance;
	}

	public String getMachineModelNumber() {
		return machineModelNumber;
	}

	public void setMachineModelNumber(String machineModelNumber) {
		this.machineModelNumber = machineModelNumber;
	}

	public String getMachineSerialNumber() {
		return machineSerialNumber;
	}

	public void setMachineSerialNumber(String machineSerialNumber) {
		this.machineSerialNumber = machineSerialNumber;
	}

	public Date getMachineInstallationDate() {
		return machineInstallationDate;
	}

	public void setMachineInstallationDate(Date machineInstallationDate) {
		this.machineInstallationDate = machineInstallationDate;
	}

	public String getTechnicianName() {
		return technicianName;
	}

	public void setTechnicianName(String technicianName) {
		this.technicianName = technicianName;
	}

}
