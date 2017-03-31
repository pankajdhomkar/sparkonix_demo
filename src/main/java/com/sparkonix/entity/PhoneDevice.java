package com.sparkonix.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "phone_devices")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.PhoneDevice.findAll", 
		query = "SELECT pd FROM PhoneDevice pd"),
		@NamedQuery(name = "com.sparkonix.entity.PhoneDevice.getOperatorByPhoneNumber", 
		query = "SELECT pd FROM PhoneDevice pd WHERE pd.phoneNumber= :PHONE_NUMBER"),
		
		@NamedQuery(name = "com.sparkonix.entity.PhoneDevice.getOperatorByMobileNumCustomerIdLocationId", 
		query = "SELECT pd FROM PhoneDevice pd WHERE "
				+ " pd.phoneNumber= :PHONE_NUMBER AND "
				+ " pd.customerId= :CUSTOMER_ID AND "
				+ " pd.locationId= :LOCATION_ID "),
		
		@NamedQuery(name = "com.sparkonix.entity.PhoneDevice.findAllByCustomerId", 
		query = "SELECT pd FROM PhoneDevice pd WHERE pd.customerId= :CUSTOMER_ID"),		
		@NamedQuery(name = "com.sparkonix.entity.PhoneDevice.findAllByCustomerIdAndOnBoardedId", 
		query = "SELECT pd FROM PhoneDevice pd WHERE pd.customerId= :CUSTOMER_ID AND pd.onBoardedBy = :ON_BOARDED_BY") })
public class PhoneDevice implements Serializable {

	private static final long serialVersionUID = 4659113523154060505L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@Column(name = "device_id")
	private String deviceId;

	@Column(name = "type")
	private String type;

	@Column(name = "operator_name", nullable = false)
	private String operatorName;

	@Column(name = "customer_id", nullable = false)
	private long customerId;

	@Column(name = "location_id")
	private long locationId;

	@Column(name = "on_boarded_by", nullable = false)
	private long onBoardedBy;

	@Column(name = "otp")
	private String otp;
	
	@Column(name = "fcm_token")
	private String fcmToken;
	
	

	public String getFcmToken() {
		return fcmToken;
	}

	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	private transient CompanyLocation companyLocation;

	public CompanyLocation getCompanyLocation() {
		return companyLocation;
	}

	public void setCompanyLocation(CompanyLocation companyLocation) {
		this.companyLocation = companyLocation;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}

	public long getOnBoardedBy() {
		return onBoardedBy;
	}

	public void setOnBoardedBy(long onBoardedBy) {
		this.onBoardedBy = onBoardedBy;
	}

}
