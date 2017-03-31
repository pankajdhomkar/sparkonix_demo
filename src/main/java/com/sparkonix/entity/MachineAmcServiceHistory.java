package com.sparkonix.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.sparkonix.entity.dialect.StringJsonUserType;

@Entity
@Table(name = "machine_amc_service_history")
@NamedQueries({
		@NamedQuery(name = "com.sparkonix.entity.MachineAmcServiceHistory.findAll", query = "SELECT m FROM MachineAmcServiceHistory m"),
		@NamedQuery(name = "com.sparkonix.entity.MachineAmcServiceHistory.findByCompanyID", query = "SELECT m FROM MachineAmcServiceHistory m "
				+ "WHERE m.companyId = :COMPANYID"),
		@NamedQuery(name = "com.sparkonix.entity.MachineAmcServiceHistory.findByTechnician", query = "SELECT m FROM MachineAmcServiceHistory m "
				+ "WHERE m.assignedTo = :TECHID"),
		@NamedQuery(name = "com.sparkonix.entity.MachineAmcServiceHistory.findByMachineID", query = "SELECT m FROM MachineAmcServiceHistory m "
				+ "WHERE m.machine = :MACHINEID"), 
		@NamedQuery(name = "com.sparkonix.entity.MachineAmcServiceHistory.findAllByMachineId", query = "SELECT m FROM MachineAmcServiceHistory m "
				+ "WHERE m.machine.id = :MACHINE_ID")
		
})
@TypeDefs({ @TypeDef(name = "CustomJsonObject", typeClass = StringJsonUserType.class) })
public class MachineAmcServiceHistory implements Serializable {

	private static final long serialVersionUID = 938010620587582558L;

	public static enum AMC_STATUS {
		EXPIRED, CANCELED
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "amc_id")
	private long amcId;
	
	@Column(name = "details")
	private String details;

	@Column(name = "servicing_assigned_date")
	private Date servicingAssignedDate;

	@Column(name = "servicing_done_date")
	private Date servicingDoneDate;

	@Column(name = "action_taken")
	private String actionTaken;

	@Column(name = "status")
	private String status;

	@Type(type = "CustomJsonObject")
	@Column(name = "metadata")
	private String metadata;

	@Column(name = "company_id", nullable = false)
	private long companyId;

	//@JsonManagedReference("machineIdRef")
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "machine_id", nullable = false)
	private Machine machine;
	
	//@JsonManagedReference("assignedToRef")
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "assigned_to", nullable = false)
	private  User assignedTo;

	
	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	public long getAmcId() {
		return amcId;
	}

	public void setAmcId(long amcId) {
		this.amcId = amcId;
	}

	public String getDetails() {
		return details;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Date getServicingAssignedDate() {
		return servicingAssignedDate;
	}

	public void setServicingAssignedDate(Date servicingAssignedDate) {
		this.servicingAssignedDate = servicingAssignedDate;
	}

	public Date getServicingDoneDate() {
		return servicingDoneDate;
	}

	public void setServicingDoneDate(Date servicingDoneDate) {
		this.servicingDoneDate = servicingDoneDate;
	}


	public User getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(User assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

}
