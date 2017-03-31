package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.SubscriptionHistory;

import io.dropwizard.hibernate.AbstractDAO;

public class SubscriptionHistoryDAO extends AbstractDAO<SubscriptionHistory> {

	public SubscriptionHistoryDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public SubscriptionHistory save(SubscriptionHistory subscriptionHistory) throws Exception {
		return persist(subscriptionHistory);
	}

	public SubscriptionHistory getById(long subscriptionHistoryId) throws Exception {
		return get(subscriptionHistoryId);
	}

	public List<SubscriptionHistory> findAll() {
		return list(namedQuery("com.sparkonix.entity.SubscriptionHistory.findAll"));
	}

}
