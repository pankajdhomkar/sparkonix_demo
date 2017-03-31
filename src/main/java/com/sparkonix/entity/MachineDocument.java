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
@Table(name = "machine_documents")
@NamedQueries({
		@NamedQuery(name = "com.sparkonix.entity.MachineDocument.findAll", 
				query = "SELECT md FROM MachineDocument md"),
		@NamedQuery(name = "com.sparkonix.entity.MachineDocument.findByManufacturer", 
		query = "SELECT md FROM MachineDocument md WHERE md.manufacturerId = :companyID"),
		@NamedQuery(name = "com.sparkonix.entity.MachineDocument.findAllByManIdAndModelNumber", 
		query = "SELECT md FROM MachineDocument md WHERE md.manufacturerId = :MANUFACTURER_ID AND md.modelNumber=:MODEL_NUMBER")		
})

public class MachineDocument implements Serializable {

	private static final long serialVersionUID = -8441913984745839437L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "manufacturer_id", nullable = false)
	private long manufacturerId;

	@Column(name = "model_number")
	private String modelNumber;

	@Column(name = "document_path")
	private String documentPath;

	@Column(name = "description")
	private String description;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(long manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
