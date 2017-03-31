package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.ClosedIssue;

import io.dropwizard.hibernate.AbstractDAO;

public class ClosedIssueDAO extends AbstractDAO<ClosedIssue> {

	public ClosedIssueDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public ClosedIssue save(ClosedIssue closedIssue) throws Exception {
		return persist(closedIssue);
	}

	public ClosedIssue getById(long closedIssueId) throws Exception {
		return get(closedIssueId);
	}

	public ClosedIssue findByClosedIssueId(String closedIssueId) {
		return uniqueResult(namedQuery("com.sparkonix.entity.ClosedIssue.findByClosedIssueId")
				.setParameter("closedIssueId", closedIssueId));
	}

	public List<ClosedIssue> findAll() {
		return list(namedQuery("com.sparkonix.entity.ClosedIssue.findAll"));
	}

}
