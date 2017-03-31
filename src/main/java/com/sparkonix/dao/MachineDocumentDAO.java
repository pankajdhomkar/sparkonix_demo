package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.MachineDocument;

import io.dropwizard.hibernate.AbstractDAO;

public class MachineDocumentDAO extends AbstractDAO<MachineDocument> {

	public MachineDocumentDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public MachineDocument save(MachineDocument machineDocument) throws Exception {
		return persist(machineDocument);
	}

	public MachineDocument getById(long machineDocumentId) throws Exception {
		return get(machineDocumentId);
	}

	public List<MachineDocument> findAll() {
		return list(namedQuery("com.sparkonix.entity.MachineDocument.findAll"));
	}

	public Object findByManufacturer(long companyID) {
		return list(namedQuery("com.sparkonix.entity.MachineDocument.findByManufacturer").setParameter("companyID",
				companyID));
	}

	public List<MachineDocument> findAllByManIdAndModelNumber(long manufacturerId, String modelNumber) {
		return list(namedQuery("com.sparkonix.entity.MachineDocument.findAllByManIdAndModelNumber")
				.setParameter("MANUFACTURER_ID", manufacturerId).setParameter("MODEL_NUMBER", modelNumber));
	}	
}
