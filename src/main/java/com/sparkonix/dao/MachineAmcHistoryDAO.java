package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.MachineAmcHistory;

import io.dropwizard.hibernate.AbstractDAO;

public class MachineAmcHistoryDAO extends AbstractDAO<MachineAmcHistory> {

	public MachineAmcHistoryDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public MachineAmcHistory save(MachineAmcHistory machineAmcHistory) throws Exception {
		return persist(machineAmcHistory);
	}

	public MachineAmcHistory getById(long machineAmcHistoryId) throws Exception {
		return get(machineAmcHistoryId);
	}

	public List<MachineAmcHistory> findAll() {
		return list(namedQuery("com.sparkonix.entity.MachineAmcHistory.findAll"));
	}

}
