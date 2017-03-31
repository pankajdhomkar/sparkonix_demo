package com.sparkonix.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.sparkonix.entity.Issue;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.dto.IssueSearchFilterPayloadDTO;

import io.dropwizard.hibernate.AbstractDAO;

public class IssueDAO extends AbstractDAO<Issue> {

	public IssueDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public Issue save(Issue issue) throws Exception {
		return persist(issue);
	}

	public Issue getById(long issueId) throws Exception {
		return get(issueId);
	}

	public List<Issue> findAll() {
		return list(namedQuery("com.sparkonix.entity.Issue.findAll"));
	}

	/*
	 * public List<Issue> getIssuesByCategory(String category, long parameter) {
	 * if (category.equalsIgnoreCase("company")) return
	 * list(namedQuery("com.sparkonix.entity.Issue.findByCompanyID").
	 * setParameter("COMPANYID", parameter)); if
	 * (category.equalsIgnoreCase("technician")) return
	 * list(namedQuery("com.sparkonix.entity.Issue.findByTechnicianID").
	 * setParameter("TECHID", parameter)); if
	 * (category.equalsIgnoreCase("machine")) return
	 * list(namedQuery("com.sparkonix.entity.Issue.findByMachineID").
	 * setParameter("MACHINEID", parameter)); return Collections.emptyList(); }
	 */

	public List<Issue> findAllByAssignedToId(long assignedToId) {
		return list(namedQuery("com.sparkonix.entity.Issue.findAllByAssignedToId").setParameter("ASSIGNEDTO_ID",
				assignedToId));
	}

	public List<Issue> findAllBySupportAssitanaceAndCompanyId(String supportAssistance, long companyId) {
		if (supportAssistance.equals(Machine.SUPPORT_ASSISTANCE.MANUFACTURER.toString())) {

			return list(namedQuery("com.sparkonix.entity.Issue.findAllBySupportAssitanaceAndManId")
					.setParameter("SUPPORT_ASSISTANCE", supportAssistance).setParameter("MANUFACTURER_ID", companyId));
		} else if (supportAssistance.equals(Machine.SUPPORT_ASSISTANCE.RESELLER.toString())) {

			return list(namedQuery("com.sparkonix.entity.Issue.findAllBySupportAssitanaceAndResId")
					.setParameter("SUPPORT_ASSISTANCE", supportAssistance).setParameter("RESELLER_ID", companyId));
		} else {
			return Collections.emptyList();
		}
	}

	public List<Issue> findAllByMachineId(long machineId) {
		return list(namedQuery("com.sparkonix.entity.Issue.findByMachineID").setParameter("MACHINEID", machineId));
	}

	public List<Issue> findAllBySearchFilter(String supportAssistance,
			IssueSearchFilterPayloadDTO issueSearchFilterPayloadDTO) {

		Criteria criteria = currentSession().createCriteria(Issue.class, "issue");

		if (issueSearchFilterPayloadDTO.getCustomerId() != 0) {
			criteria.add(Restrictions.eq("customerId", issueSearchFilterPayloadDTO.getCustomerId()));
		}
		if (issueSearchFilterPayloadDTO.getStartDate() != null) {
			criteria.add(Restrictions.ge("dateReported", issueSearchFilterPayloadDTO.getStartDate()));
		}
		if (issueSearchFilterPayloadDTO.getEndDate() != null) {
			criteria.add(Restrictions.le("dateClosed", issueSearchFilterPayloadDTO.getEndDate()));
		}
		//for manufacturer/reseller
		if (supportAssistance.equals(Machine.SUPPORT_ASSISTANCE.MANUFACTURER.toString())) {
			criteria.add(Restrictions.eq("manufacturerId", issueSearchFilterPayloadDTO.getManResId()));
		}
		if (supportAssistance.equals(Machine.SUPPORT_ASSISTANCE.RESELLER.toString())) {
			criteria.add(Restrictions.eq("resellerId", issueSearchFilterPayloadDTO.getManResId()));
		}
		
		//for superadmin
		if (!supportAssistance.equals("")) {
			criteria.add(Restrictions.eq("machineSupportAssistance", supportAssistance));
		}

		return criteria.list();

	}

}
