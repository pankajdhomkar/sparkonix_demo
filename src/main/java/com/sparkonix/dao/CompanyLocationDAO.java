package com.sparkonix.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.jsonb.CompanyLocationAddress;

import io.dropwizard.hibernate.AbstractDAO;

public class CompanyLocationDAO extends AbstractDAO<CompanyLocation> {

	public CompanyLocationDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public CompanyLocation save(CompanyLocation companyLocation) throws Exception {
		return persist(companyLocation);
	}

	public CompanyLocation getById(long companyLocationId) throws Exception {
		return get(companyLocationId);
	}

	public List<CompanyLocation> findAll() {
		return list(namedQuery("com.sparkonix.entity.CompanyLocation.findAll"));
	}

	public List<CompanyLocation> findAllByCompanyIdAndOnBoardedId(long companyDetailsId, long onBoardedById)
			throws Exception {

		@SuppressWarnings("unchecked")
		List<CompanyLocation> list = namedQuery("com.sparkonix.entity.CompanyLocation.findAllByCompanyIdAndOnBoardedId")
				.setParameter("COMPANY_DETAILS_ID", companyDetailsId).setParameter("ON_BOARDED_BY", onBoardedById)
				.list();

		List<CompanyLocation> listCompanyLocations = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			CompanyLocation companyLocation = new CompanyLocation();
			String address = list.get(i).getAddress();

			companyLocation.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
			companyLocation.setId(list.get(i).getId());
			companyLocation.setOnBoardedBy(list.get(i).getOnBoardedBy());
			companyLocation.setCompanyDetailsId(list.get(i).getCompanyDetailsId());
			companyLocation.setContactPerson(list.get(i).getContactPerson());
			companyLocation.setContactMobile(list.get(i).getContactMobile());

			listCompanyLocations.add(companyLocation);
		}
		return listCompanyLocations;
	}

	public List<CompanyLocation> findAllByCompanyId(long companyDetailsId) throws Exception {
		@SuppressWarnings("unchecked")
		List<CompanyLocation> list = namedQuery("com.sparkonix.entity.CompanyLocation.findAllByCompanyId")
				.setParameter("COMPANY_DETAILS_ID", companyDetailsId).list();

		List<CompanyLocation> listCompanyLocations = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			CompanyLocation companyLocation = new CompanyLocation();
			String address = list.get(i).getAddress();
			companyLocation.setCompanyLocationAddress(CompanyLocationAddress.fromJson(address));
			companyLocation.setId(list.get(i).getId());
			companyLocation.setOnBoardedBy(list.get(i).getOnBoardedBy());
			companyLocation.setCompanyDetailsId(list.get(i).getCompanyDetailsId());
			companyLocation.setContactPerson(list.get(i).getContactPerson());
			companyLocation.setContactMobile(list.get(i).getContactMobile());

			listCompanyLocations.add(companyLocation);
		}
		return listCompanyLocations;
	}

	public long getCountByCustomerIdAndOnBoardedBy(long customerId, long onBoardedById, String userRole) {
		Query query;
		if (userRole.equals("SUPERADMIN")) {
			query = currentSession().createQuery(
					"select count(*) from CompanyLocation cl where cl.companyDetailsId= :COMPANY_DETAIL_ID");
			query.setParameter("COMPANY_DETAIL_ID", customerId);
			return (Long) query.uniqueResult();
		}

		// SALESTEAM OR WEBADMIN
		query = currentSession().createQuery(
				"select count(*) from CompanyLocation cl where cl.companyDetailsId= :CUSTOMER_ID and cl.onBoardedBy= :ON_BOARDED_BY");
		query.setParameter("CUSTOMER_ID", customerId);
		query.setParameter("ON_BOARDED_BY", onBoardedById);

		return (Long) query.uniqueResult();
	}
}
