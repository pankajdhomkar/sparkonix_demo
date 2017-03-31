package com.sparkonix.dao;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;

import com.sparkonix.entity.MachineAmcServiceHistory;
import com.sparkonix.entity.User;

import io.dropwizard.hibernate.AbstractDAO;

public class MachineAmcServiceHistoryDAO extends AbstractDAO<MachineAmcServiceHistory> {

	public MachineAmcServiceHistoryDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public MachineAmcServiceHistory save(MachineAmcServiceHistory machineAmcServiceHistory) throws Exception {
		return persist(machineAmcServiceHistory);
	}

	public MachineAmcServiceHistory getById(long machineSubscriptionHistoryId) throws Exception {
		return get(machineSubscriptionHistoryId);
	}

	public List<MachineAmcServiceHistory> findAll() {		 
		List<MachineAmcServiceHistory> machineAmcServiceHistories;
		machineAmcServiceHistories = list(namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findAll"));
		for (MachineAmcServiceHistory machineAmcServiceHistory : machineAmcServiceHistories) {
			initialize(machineAmcServiceHistory.getMachine());
			initialize(machineAmcServiceHistory.getAssignedTo());
		}
		return machineAmcServiceHistories;		
	}

	public Date getLastServiceDate(long machineId) {
		Criteria criteria = currentSession().createCriteria(MachineAmcServiceHistory.class)
				.setProjection(Projections.max("servicingDoneDate"));

		return (Date) criteria.uniqueResult();
	}

	public List<MachineAmcServiceHistory> getServicesForTechnician(User userObj) {
		List<MachineAmcServiceHistory> machineAmcServiceHistories = list(
				namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findByTechnician").setParameter("TECHID",
						userObj));
		for (MachineAmcServiceHistory machineAmcServiceHistory : machineAmcServiceHistories) {
			initialize(machineAmcServiceHistory.getMachine());
			initialize(machineAmcServiceHistory.getAssignedTo());
		}
		return machineAmcServiceHistories;
	}

	public List<MachineAmcServiceHistory> getServicesByCategory(String category, long parameter) {
		List<MachineAmcServiceHistory> machineAmcServiceHistories;
		if ("company".equalsIgnoreCase(category)) {
			machineAmcServiceHistories = list(
					namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findByCompanyID")
							.setParameter("COMPANYID", parameter));
		} else if ("technician".equalsIgnoreCase(category)) {
			machineAmcServiceHistories = list(
					namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findByTechnician").setParameter("TECHID",
							parameter));
		} else if ("machine".equalsIgnoreCase(category)) {
			/*
			 * machineAmcServiceHistories = list( namedQuery(
			 * "com.sparkonix.entity.MachineAmcServiceHistory.findByMachineID")
			 * .setParameter("MACHINEID", parameter));
			 */
			machineAmcServiceHistories = list(
					namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findAllByMachineId")
							.setParameter("MACHINE_ID", parameter));
		} else {
			return Collections.emptyList();
		}

		for (MachineAmcServiceHistory machineAmcServiceHistory : machineAmcServiceHistories) {
			initialize(machineAmcServiceHistory.getMachine());
			initialize(machineAmcServiceHistory.getAssignedTo());
		}

		return machineAmcServiceHistories;

	}

	public List<MachineAmcServiceHistory> findAllByMachineId(long machineId) {
		return list(namedQuery("com.sparkonix.entity.MachineAmcServiceHistory.findAllByMachineId")
				.setParameter("MACHINE_ID", machineId));
	}
}
