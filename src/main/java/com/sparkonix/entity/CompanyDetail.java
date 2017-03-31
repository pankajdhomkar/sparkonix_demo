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

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.sparkonix.entity.dialect.StringJsonUserType;

@Entity
@Table(name = "company_details")
@NamedQueries({
		@NamedQuery(name = "com.sparkonix.entity.CompanyDetail.findAll", 
		query = "SELECT cd FROM CompanyDetail cd"),

		@NamedQuery(name = "com.sparkonix.entity.CompanyDetail.findAllByOnBoardedId", 
		query = "SELECT cd FROM CompanyDetail cd "
				+ "WHERE cd.onBoardedBy= :ON_BOARDED_BY AND cd.companyType= :COMPANY_TYPE"),

		@NamedQuery(name = "com.sparkonix.entity.CompanyDetail.findByLocationOnBoardedId", 
		query = "SELECT DISTINCT cd FROM CompanyDetail cd, CompanyLocation loc WHERE "
				+ "cd.onBoardedBy= :ON_BOARDED_BY AND " + "cd.companyType= :COMPANY_TYPE OR "
				+ "(loc.onBoardedBy= :ON_BOARDED_BY AND cd.id = loc. companyDetailsId)"),

		@NamedQuery(name = "com.sparkonix.entity.CompanyDetail.findAllByCompanyType", 
		query = "SELECT cd FROM CompanyDetail cd WHERE cd.companyType= :COMPANY_TYPE"),
		
		@NamedQuery(name = "com.sparkonix.entity.CompanyDetail.findCompanyDetailByPanAndCompanyType", 
		query = "SELECT cd FROM CompanyDetail cd WHERE cd.pan= :PAN AND cd.companyType= :COMPANY_TYPE"),		

		@NamedQuery(name = "com.sparkonix.entity.CompanyDetail.findCompanyDetailByPan", 
		query = "SELECT cd FROM CompanyDetail cd WHERE cd.pan= :PAN") })
@TypeDefs({ @TypeDef(name = "CustomJsonObject", typeClass = StringJsonUserType.class) })
public class CompanyDetail implements Serializable {

	private static final long serialVersionUID = -4130159596465108809L;

	public static enum COMPANY_TYPE {
		MANUFACTURER, RESELLER, CUSTOMER
	};

	public static enum CUR_SUBSCRIPTION_TYPE {
		BASIC, PREMIUM
	};

	public static enum CUR_SUBSCRIPTION_STATUS {
		ACTIVE, PAYMENT_DUE, INACTIVE, EXPIRED
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "company_type", nullable = false)
	private String companyType;

	@Column(name = "company_name", nullable = false)
	private String companyName;

	@Column(name = "pan", nullable = false)
	private String pan;

	@Column(name = "cust_support_name")
	private String custSupportName;

	@Column(name = "cust_support_phone")
	private String custSupportPhone;

	@Column(name = "cust_support_email")
	private String custSupportEmail;

	@Column(name = "cur_subscription_type")
	private String curSubscriptionType;

	@Column(name = "cur_subscription_startdate")
	private Date curSubscriptionStartDate;

	@Column(name = "cur_subscription_enddate")
	private Date curSubscriptionEndDate;

	@Column(name = "cur_subscription_status")
	private String curSubscriptionStatus;

	@Column(name = "on_boarded_by")
	private long onBoardedBy;

	@Type(type = "CustomJsonObject")
	@Column(name = "metadata")
	private String metadata;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public Date getCurSubscriptionEndDate() {
		return curSubscriptionEndDate;
	}

	public void setCurSubscriptionEndDate(Date curSubscriptionEndDate) {
		this.curSubscriptionEndDate = curSubscriptionEndDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getCurSubscriptionType() {
		return curSubscriptionType;
	}

	public void setCurSubscriptionType(String curSubscriptionType) {
		this.curSubscriptionType = curSubscriptionType;
	}

	public Date getCurSubscriptionStartDate() {
		return curSubscriptionStartDate;
	}

	public void setCurSubscriptionStartDate(Date curSubscriptionStartDate) {
		this.curSubscriptionStartDate = curSubscriptionStartDate;
	}

	public String getCurSubscriptionStatus() {
		return curSubscriptionStatus;
	}

	public void setCurSubscriptionStatus(String curSubscriptionStatus) {
		this.curSubscriptionStatus = curSubscriptionStatus;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getCustSupportName() {
		return custSupportName;
	}

	public void setCustSupportName(String custSupportName) {
		this.custSupportName = custSupportName;
	}

	public String getCustSupportPhone() {
		return custSupportPhone;
	}

	public void setCustSupportPhone(String custSupportPhone) {
		this.custSupportPhone = custSupportPhone;
	}

	public String getCustSupportEmail() {
		return custSupportEmail;
	}

	public void setCustSupportEmail(String custSupportEmail) {
		this.custSupportEmail = custSupportEmail;
	}

	public long getOnBoardedBy() {
		return onBoardedBy;
	}

	public void setOnBoardedBy(long onBoardedBy) {
		this.onBoardedBy = onBoardedBy;
	}

}
