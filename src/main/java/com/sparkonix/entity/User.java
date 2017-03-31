package com.sparkonix.entity;

import java.io.Serializable;
import java.security.Principal;
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
@Table(name = "users")
@TypeDefs({ @TypeDef(name = "CustomJsonObject", typeClass = StringJsonUserType.class) })
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.User.findAll", query = "SELECT u FROM User u"),
		@NamedQuery(name = "com.sparkonix.entity.User.findByRole", query = "SELECT u FROM User u WHERE u.role = :ROLE"),
		@NamedQuery(name = "com.sparkonix.entity.User.findByID", query = "SELECT u FROM User u WHERE u.id = :ID"),
		@NamedQuery(name = "com.sparkonix.entity.User.findUserByUsernameAndPassword", query = "SELECT u FROM User u WHERE u.email = :USERNAME and u.password = :PASSWORD"),
		@NamedQuery(name = "com.sparkonix.entity.User.checkSuperAdminByUsername", query = "SELECT u FROM User u WHERE u.email = :USERNAME and u.role= :ROLE "),
		@NamedQuery(name = "com.sparkonix.entity.User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :EMAIL"),
		@NamedQuery(name = "com.sparkonix.entity.User.findByCompanyDetailsIdAndRole", query = "SELECT u FROM User u WHERE u.companyDetailsId = :COMPANY_DETAIL_ID AND u.role = :ROLE") })

public class User implements Serializable, Principal {

	private static final long serialVersionUID = -234346468969928079L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "alt_email")
	private String altEmail;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "mobile", nullable = false)
	private String mobile;

	@Column(name = "company_details_id")
	private long companyDetailsId;

	@Column(name = "role", nullable = false)
	private String role;

	@Column(name = "notification_type")
	private String notificationType;

	@Type(type = "CustomJsonObject")
	@Column(name = "metadata")
	private String metadata;

	private transient String token; // transient variable not serialized

	// @JsonBackReference("assignedToRef")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "assignedTo")
	private List<MachineAmcServiceHistory> machineAmcServiceHistories;

	// @JsonBackReference("createdByRef")
	/*@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "createdBy")
	private List<QRCode> qrCodeList;

	public List<QRCode> getQrCodeList() {
		return qrCodeList;
	}

	public void setQrCodeList(List<QRCode> qrCodeList) {
		this.qrCodeList = qrCodeList;
	}*/

	public static enum ROLE_TYPE {
		SUPERADMIN, SALESTEAM, MANUFACTURERADMIN, RESELLERADMIN, TECHNICIAN, OPERATOR
	}

	public static enum NOTIFICATION_TYPE {
		EMAIL, SMS, BOTH
	}

	public User() {
		//
	}

	public User(String username, String password, String role) {
		this.email = username;
		this.password = password;
		this.role = role;
	}

	public boolean isUserInRole(String roleToCheck) {
		return true;
	}

	public void setCompanyDetailsId(long companyDetailsId) {
		this.companyDetailsId = companyDetailsId;
	}

	public List<MachineAmcServiceHistory> getMachineAmcServiceHistories() {
		return machineAmcServiceHistories;
	}

	public void setMachineAmcServiceHistories(List<MachineAmcServiceHistory> machineAmcServiceHistories) {
		this.machineAmcServiceHistories = machineAmcServiceHistories;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAltEmail() {
		return altEmail;
	}

	public void setAltEmail(String altEmail) {
		this.altEmail = altEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Long getCompanyDetailsId() {
		return companyDetailsId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

}
