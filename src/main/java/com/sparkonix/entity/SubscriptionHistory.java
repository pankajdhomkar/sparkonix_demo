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
@Table(name = "subscription_history")
@NamedQueries({
		@NamedQuery(name = "com.sparkonix.entity.SubscriptionHistory.findAll", query = "SELECT sh FROM SubscriptionHistory sh") })
public class SubscriptionHistory implements Serializable {

	private static final long serialVersionUID = -7873384835021448462L;

	public static enum SUBSCRIPTION_STATUS {
		EXPIRED, CANCELED
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "company_details_id", nullable = false)
	private long companyDetailsId;

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

	public long getCompanyDetailsId() {
		return companyDetailsId;
	}

	public void setCompanyDetailsId(long companyDetailsId) {
		this.companyDetailsId = companyDetailsId;
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
