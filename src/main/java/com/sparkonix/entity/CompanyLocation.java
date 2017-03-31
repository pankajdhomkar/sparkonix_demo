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

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.sparkonix.entity.dialect.StringJsonUserType;
import com.sparkonix.entity.jsonb.CompanyLocationAddress;

@Entity
@Table(name = "company_locations")
@NamedQueries({
		@NamedQuery(name = "com.sparkonix.entity.CompanyLocation.findAll", query = "SELECT cl FROM CompanyLocation cl"),

		@NamedQuery(name = "com.sparkonix.entity.CompanyLocation.findAllByCompanyId", 
			query = "SELECT cl FROM CompanyLocation cl "
					+ "WHERE cl.companyDetailsId= :COMPANY_DETAILS_ID"),

		@NamedQuery(name = "com.sparkonix.entity.CompanyLocation.findAllByCompanyIdAndOnBoardedId", 
			query = "SELECT cl FROM CompanyLocation cl "
					+ "WHERE cl.companyDetailsId= :COMPANY_DETAILS_ID "
					+ "AND cl.onBoardedBy = :ON_BOARDED_BY")

})
@TypeDefs({ @TypeDef(name = "CustomJsonObject", typeClass = StringJsonUserType.class) })
public class CompanyLocation implements Serializable {

	private static final long serialVersionUID = -5138810094316744450L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "company_details_id", nullable = false)
	private long companyDetailsId;

	@Type(type = "CustomJsonObject")
	@Column(name = "address")
	private String address;

	private transient CompanyLocationAddress companyLocationAddress;

	@Column(name = "contact_person", nullable = false)
	private String contactPerson;

	@Column(name = "contact_mobile", nullable = false)
	private String contactMobile;

	@Column(name = "on_boarded_by", nullable = false)
	private long onBoardedBy;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public long getOnBoardedBy() {
		return onBoardedBy;
	}

	public void setOnBoardedBy(long onBoardedBy) {
		this.onBoardedBy = onBoardedBy;
	}

	public CompanyLocationAddress getCompanyLocationAddress() {
		return companyLocationAddress;
	}

	public void setCompanyLocationAddress(CompanyLocationAddress companyLocationAddress) {
		this.companyLocationAddress = companyLocationAddress;
	}

}
