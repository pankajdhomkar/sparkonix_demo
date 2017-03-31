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
@Table(name = "closed_issues")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.ClosedIssue.findAll", query = "SELECT ci FROM ClosedIssue ci")

})
public class ClosedIssue implements Serializable {

	private static final long serialVersionUID = 873741536839604710L;

	public static enum ISSUE_STATUS {
		OPEN, ASSIGNED, CLOSED
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "issueNumber", nullable = false)
	private String issueNumber;

	@Column(name = "machine_id", nullable = false)
	private long machineId;

	@Column(name = "reporting_device" )
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

}
