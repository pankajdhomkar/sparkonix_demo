package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.MachineSubscriptionHistory;

import io.dropwizard.hibernate.AbstractDAO;

public class MachineSubscriptionHistoryDAO extends AbstractDAO<MachineSubscriptionHistory> {

	public MachineSubscriptionHistoryDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public MachineSubscriptionHistory save(MachineSubscriptionHistory machineSubscriptionHistory) throws Exception {
		return persist(machineSubscriptionHistory);
	}

	public MachineSubscriptionHistory getById(long machineSubscriptionHistoryId) throws Exception {
		return get(machineSubscriptionHistoryId);
	}

	public List<MachineSubscriptionHistory> findAll() {
		return list(namedQuery("com.sparkonix.entity.MachineSubscriptionHistory.findAll"));
	}

}
