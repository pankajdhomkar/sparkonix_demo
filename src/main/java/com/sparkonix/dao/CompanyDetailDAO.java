package com.sparkonix.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.User;

import io.dropwizard.hibernate.AbstractDAO;

public class CompanyDetailDAO extends AbstractDAO<CompanyDetail> {

	public CompanyDetailDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public CompanyDetail save(CompanyDetail companyDetail) throws Exception {
		return persist(companyDetail);
	}

	public CompanyDetail getById(long companyDetailId) throws Exception {
		return get(companyDetailId);
	}

	public List<CompanyDetail> findAll() {
		return list(namedQuery("com.sparkonix.entity.CompanyDetail.findAll"));
	}

	/**
	 * get list of {@link CompanyDetail} by onBoarded & companyType
	 * 
	 * @param onBoardedById:
	 * @param userRole
	 * @param companyType
	 */
	public List<CompanyDetail> findAllByOnBoardedId(long onBoardedById, String userRole, String companyType) {
		// for salesteam/manRes we need to get all the customers onboarded by
		// him AND
		// customers not onboarded by him but whose new factory locations were
		// onboarded by him
		if (userRole.equalsIgnoreCase(User.ROLE_TYPE.SALESTEAM.toString())
				|| userRole.equalsIgnoreCase(User.ROLE_TYPE.MANUFACTURERADMIN.toString())
				|| userRole.equalsIgnoreCase(User.ROLE_TYPE.RESELLERADMIN.toString())) {
			return list(namedQuery("com.sparkonix.entity.CompanyDetail.findByLocationOnBoardedId")
					.setParameter("ON_BOARDED_BY", onBoardedById)
					.setParameter("COMPANY_TYPE", companyType.toUpperCase()));
		} else {
			return list(namedQuery("com.sparkonix.entity.CompanyDetail.findAllByOnBoardedId")
					.setParameter("ON_BOARDED_BY", onBoardedById)
					.setParameter("COMPANY_TYPE", companyType.toUpperCase()));
		}
	}

	public List<CompanyDetail> findAllByOnBoardedAndType(long onBoardedById, String companyType) {
		return list(namedQuery("com.sparkonix.entity.CompanyDetail.findAllByOnBoardedId")
				.setParameter("ON_BOARDED_BY", onBoardedById).setParameter("COMPANY_TYPE", companyType.toUpperCase()));

	}

	public CompanyDetail getCustSupportInfo(long manufacturerId) {
		try {
			Criteria criteria = currentSession().createCriteria(CompanyDetail.class, "CompanyDetail");
			criteria.add(Restrictions.eq("id", manufacturerId));

			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("custSupportName"), "custSupportName")
					.add(Projections.property("id"), "id").add(Projections.property("companyName"), "companyName")
					.add(Projections.property("custSupportPhone"), "custSupportPhone")
					.add(Projections.property("custSupportEmail"), "custSupportEmail"));

			criteria.setResultTransformer(Transformers.aliasToBean(CompanyDetail.class));

			return (CompanyDetail) criteria.uniqueResult();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<CompanyDetail> findAllByCompanyType(String companyType) {
		return list(namedQuery("com.sparkonix.entity.CompanyDetail.findAllByCompanyType").setParameter("COMPANY_TYPE",
				companyType.toUpperCase()));
	}

	/*
	 * public CompanyDetail findCompanyDetailByPan(String pan) { return
	 * uniqueResult(
	 * namedQuery("com.sparkonix.entity.CompanyDetail.findCompanyDetailByPan").
	 * setParameter("PAN", pan)); }
	 */

	public CompanyDetail findCompanyDetailByPanAndCompanyType(String pan, String companyType) {
		return uniqueResult(namedQuery("com.sparkonix.entity.CompanyDetail.findCompanyDetailByPanAndCompanyType")
				.setParameter("PAN", pan).setParameter("COMPANY_TYPE", companyType));
	}

}
