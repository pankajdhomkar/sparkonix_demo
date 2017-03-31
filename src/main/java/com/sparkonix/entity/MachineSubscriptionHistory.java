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
@Table(name = "machine_subscription_history")
@NamedQueries({
		@NamedQuery(name = "com.sparkonix.entity.MachineSubscriptionHistory.findAll", query = "SELECT m FROM MachineSubscriptionHistory m") })
public class MachineSubscriptionHistory implements Serializable {

	private static final long serialVersionUID = 2185439017935472520L;

	public static enum SUBSCRIPTION_STATUS {
		EXPIRED, CANCELED
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "machine_id", nullable = false)
	private long machineId;

	@Column(name = "subscription_type")
	private String subscriptionType;

	@Column(name = "subscription_startdate")
	private Date subscriptionStartDate;

	@Column(name = "subscription_enddate")
	private Date subscriptionEndDate;

	@Column(name = "subscription_status")
	private String subscriptionStatus;

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

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public Date getSubscriptionStartDate() {
		return subscriptionStartDate;
	}

	public void setSubscriptionStartDate(Date subscriptionStartDate) {
		this.subscriptionStartDate = subscriptionStartDate;
	}

	public Date getSubscriptionEndDate() {
		return subscriptionEndDate;
	}

	public void setSubscriptionEndDate(Date subscriptionEndDate) {
		this.subscriptionEndDate = subscriptionEndDate;
	}

	public String getSubscriptionStatus() {
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(String subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
	}

	public String getPaymentMetadata() {
		return paymentMetadata;
	}

	public void setPaymentMetadata(String paymentMetadata) {
		this.paymentMetadata = paymentMetadata;
	}

}
