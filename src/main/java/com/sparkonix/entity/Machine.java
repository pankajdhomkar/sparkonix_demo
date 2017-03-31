package com.sparkonix.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.sparkonix.entity.dialect.StringJsonUserType;

@Entity
@Table(name = "machines")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.Machine.findAll", query = "SELECT m FROM Machine m"),
		@NamedQuery(name = "com.sparkonix.entity.Machine.getMachineByQrCode", 
		query = "SELECT m FROM Machine m WHERE m.qrCode= :QR_CODE"),
		@NamedQuery(name = "com.sparkonix.entity.Machine.findAllByCustomerIdAndOnBoardedId",
		query = "SELECT m FROM Machine m WHERE m.customerId= :CUSTOMER_ID AND m.onBoardedBy = :ON_BOARDED_BY"),
		@NamedQuery(name = "com.sparkonix.entity.Machine.findMachineByQrCode", 
		query = "SELECT m FROM Machine m WHERE m.qrCode= :QRCODE"),
		@NamedQuery(name = "com.sparkonix.entity.Machine.findAllByCustomerId", 
		query = "SELECT m FROM Machine m WHERE m.customerId= :CUSTOMER_ID ")

})
@TypeDefs({ @TypeDef(name = "CustomJsonObject", typeClass = StringJsonUserType.class) })
public class Machine implements Serializable {

	private static final long serialVersionUID = 8736782676950590497L;

	public static enum CUR_SUBSCRIPTION_TYPE {
		BASIC, PREMIUM
	};

	public static enum CUR_SUBSCRIPTION_STATUS {
		ACTIVE, PAYMENT_DUE, INACTIVE, EXPIRED
	};

	public static enum CUR_AMC_TYPE {
		BASIC, PREMIUM
	};

	public static enum CUR_AMC_STATUS {
		ACTIVE, PAYMENT_DUE, INACTIVE, EXPIRED
	};

	public static enum SUPPORT_ASSISTANCE {
		MANUFACTURER, RESELLER
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/* QR code */
	@Column(name = "qr_code")
	private String qrCode;

	@Column(name = "serial_number")
	private String serialNumber;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "model_number")
	private String modelNumber;

	@Column(name = "machine_year")
	private String machineYear;

	@Column(name = "customer_id", nullable = false)
	private long customerId;

	@Column(name = "manufacturer_id", nullable = false)
	private long manufacturerId;

	@Column(name = "reseller_id")
	private long resellerId;

	@Column(name = "installation_date")
	private Date installationDate;

	@Column(name = "warranty_expiry_date")
	private Date warrantyExpiryDate;

	@Column(name = "location_id", nullable = false)
	private long locationId;

	@Column(name = "cur_amc_type")
	private String curAmcType;

	@Column(name = "cur_amc_startdate")
	private Date curAmcStartDate;

	@Column(name = "cur_amc_enddate")
	private Date curAmcEndDate;

	@Column(name = "cur_amc_status")
	private String curAmcStatus;

	@Type(type = "CustomJsonObject")
	@Column(name = "cur_amc_documents")
	private String curAmcDocuments;

	@Column(name = "cur_subscription_type")
	private String curSubscriptionType;

	@Column(name = "cur_subscription_startdate")
	private Date curSubscriptionStartDate;

	@Column(name = "cur_subscription_enddate")
	private Date curSubscriptionEndDate;

	@Column(name = "cur_subscription_status")
	private String curSubscriptionStatus;

	@Column(name = "on_boarded_by", nullable = false)
	private long onBoardedBy;

	@Column(name = "support_assistance")
	private String supportAssistance;

	// @JsonBackReference("machineIdRef")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "machine")
	private List<MachineAmcServiceHistory> machineAmcServiceHistories;

	public List<MachineAmcServiceHistory> getMachineAmcServiceHistories() {
		return machineAmcServiceHistories;
	}

	public void setMachineAmcServiceHistories(List<MachineAmcServiceHistory> machineAmcServiceHistories) {
		this.machineAmcServiceHistories = machineAmcServiceHistories;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getMachineYear() {
		return machineYear;
	}

	public void setMachineYear(String machineYear) {
		this.machineYear = machineYear;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
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

	public Date getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(Date installationDate) {
		this.installationDate = installationDate;
	}

	public Date getWarrantyExpiryDate() {
		return warrantyExpiryDate;
	}

	public void setWarrantyExpiryDate(Date warrantyExpiryDate) {
		this.warrantyExpiryDate = warrantyExpiryDate;
	}

	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
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

	public Date getCurSubscriptionEndDate() {
		return curSubscriptionEndDate;
	}

	public void setCurSubscriptionEndDate(Date curSubscriptionEndDate) {
		this.curSubscriptionEndDate = curSubscriptionEndDate;
	}

	public String getCurSubscriptionStatus() {
		return curSubscriptionStatus;
	}

	public void setCurSubscriptionStatus(String curSubscriptionStatus) {
		this.curSubscriptionStatus = curSubscriptionStatus;
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

	public String getCurAmcDocuments() {
		return curAmcDocuments;
	}

	public void setCurAmcDocuments(String curAmcDocuments) {
		this.curAmcDocuments = curAmcDocuments;
	}

	public long getOnBoardedBy() {
		return onBoardedBy;
	}

	public void setOnBoardedBy(long onBoardedBy) {
		this.onBoardedBy = onBoardedBy;
	}

	public String getSupportAssistance() {
		return supportAssistance;
	}

	public void setSupportAssistance(String supportAssistance) {
		this.supportAssistance = supportAssistance;
	}

}
