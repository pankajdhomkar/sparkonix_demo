package com.sparkonix.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "qr_codes")
@NamedQueries({ @NamedQuery(name = "com.sparkonix.entity.QRCode.findAll", query = "SELECT qrc FROM QRCode qrc")

})
public class QRCode implements Serializable {

	private static final long serialVersionUID = -42118806087206310L;

	public static enum QRCODE_STATUS {
		ASSIGNED, UNASSIGNED
	};

	@Id
	@Column(name = "qr_code", nullable = false)
	private String qrCode;

	@Column(name = "batch_name", nullable = false)
	private String batchName;

	@Column(name = "status", nullable = false)
	private String status;

	/*
	 * @Column(name = "created_by", nullable = false) private long createdBy;
	 */

	// @JsonManagedReference("createdByRef")
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
