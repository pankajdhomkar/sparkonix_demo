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
@Table(name = "machine_amc_history")
@NamedQueries({
		@NamedQuery(name = "com.sparkonix.entity.MachineAmcHistory.findAll", query = "SELECT m FROM MachineAmcHistory m") })
public class MachineAmcHistory implements Serializable {

	private static final long serialVersionUID = -511611144022705026L;

	public static enum AMC_STATUS {
		EXPIRED, CANCELED
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "machine_id", nullable = false)
	private long machineId;

	@Column(name = "amc_type")
	private String amcType;

	@Column(name = "amc_startdate")
	private Date amcStartDate;

	@Column(name = "amc_enddate")
	private Date amcEndDate;

	@Column(name = "amc_status")
	private String amcStatus;

	@Column(name = "amc_document")
	private String amcDocument;

	@Column(name = "payment_metadata")
	private String paymentMetadata;

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

	public String getAmcType() {
		return amcType;
	}

	public void setAmcType(String amcType) {
		this.amcType = amcType;
	}

	public Date getAmcStartDate() {
		return amcStartDate;
	}

	public void setAmcStartDate(Date amcStartDate) {
		this.amcStartDate = amcStartDate;
	}

	public Date getAmcEndDate() {
		return amcEndDate;
	}

	public void setAmcEndDate(Date amcEndDate) {
		this.amcEndDate = amcEndDate;
	}

	public String getAmcStatus() {
		return amcStatus;
	}

	public void setAmcStatus(String amcStatus) {
		this.amcStatus = amcStatus;
	}

	public String getAmcDocument() {
		return amcDocument;
	}

	public void setAmcDocument(String amcDocument) {
		this.amcDocument = amcDocument;
	}

	public String getPaymentMetadata() {
		return paymentMetadata;
	}

	public void setPaymentMetadata(String paymentMetadata) {
		this.paymentMetadata = paymentMetadata;
	}

}
