package com.sparkonix.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.sparkonix.entity.Machine;

import io.dropwizard.hibernate.AbstractDAO;

public class MachineDAO extends AbstractDAO<Machine> {

	public MachineDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public Machine save(Machine machine) throws Exception {
		return persist(machine);
	}

	public Machine getById(long machineId) throws Exception {
		return get(machineId);
	}

	public Machine getMachineByQrCode(String qrCode) throws Exception {

		return uniqueResult(
				namedQuery("com.sparkonix.entity.Machine.getMachineByQrCode").setParameter("QR_CODE", qrCode));
	}

	public List<Machine> findAll() {
		return list(namedQuery("com.sparkonix.entity.Machine.findAll"));
	}

	@SuppressWarnings("unchecked")
	public List<Machine> getAllMachinesForCompany(long companyId) {
		Criteria c = currentSession().createCriteria(Machine.class, "m");
		c.add(Restrictions.or(Restrictions.eq("m.manufacturerId", companyId),
				Restrictions.eq("m.resellerId", companyId)));
		return c.list();
	}

	public List<Machine> findAllByCustomerIdAndOnBoardedId(long companyDetailsId, long onBoardedById) {

		/*
		 * return currentSession().createQuery(
		 * "SELECT m, cd.companyName as companyName FROM Machine m, CompanyDetail cd"
		 * + " WHERE m.manufacturerId=cd.id AND m.onBoardedBy=" + onBoardedById
		 * + " AND m.customerId=" + companyDetailsId).list();
		 */

		return list(namedQuery("com.sparkonix.entity.Machine.findAllByCustomerIdAndOnBoardedId")
				.setParameter("CUSTOMER_ID", companyDetailsId).setParameter("ON_BOARDED_BY", onBoardedById));

	}

	public List<Machine> findAllByCustomerId(long companyDetailsId) {
		return list(namedQuery("com.sparkonix.entity.Machine.findAllByCustomerId").setParameter("CUSTOMER_ID",
				companyDetailsId));
	}

	public long getCountByCustomerIdAndOnBoardedBy(long customerId, long onBoardedById, String userRole) {
		Query query;
		if (userRole.equals("SUPERADMIN")) {
			query = currentSession().createQuery("select count(*) from Machine m where m.customerId= :CUSTOMER_ID");
			query.setParameter("CUSTOMER_ID", customerId);
			return (Long) query.uniqueResult();
		}

		// SALESTEAM OR WEBADMIN
		query = currentSession().createQuery(
				"select count(*) from Machine m where m.customerId= :CUSTOMER_ID and m.onBoardedBy= :ON_BOARDED_BY");
		query.setParameter("CUSTOMER_ID", customerId);
		query.setParameter("ON_BOARDED_BY", onBoardedById);

		return (Long) query.uniqueResult();
	}

	public Machine findMachineByQrCode(String qrcode) {
		return uniqueResult(
				namedQuery("com.sparkonix.entity.Machine.findMachineByQrCode").setParameter("QRCODE", qrcode));
	}

	public List<Machine> getSubscriptionData(int attendmeSubEndMonth, int attendmeSubEndYear, int warrantyExpEndMonth,
			int warrantyExpEndYear, int amcSubEndMonth, int amcSubEndYear, long onBoardedBy) {

		Criteria criteria = currentSession().createCriteria(Machine.class, "m");
		
		//check selected values
		if(attendmeSubEndMonth!=0 ){
			criteria.add(Restrictions.sqlRestriction("extract(month from cur_subscription_enddate) =" + attendmeSubEndMonth));
		}
		if(attendmeSubEndYear !=0 ){
			criteria.add(Restrictions.sqlRestriction("extract(year from cur_subscription_enddate) =" + attendmeSubEndYear));
		}
		
		if(warrantyExpEndMonth!=0 ){
			criteria.add(Restrictions.sqlRestriction("extract(month from warranty_expiry_date) =" + warrantyExpEndMonth));	
		}
		if(warrantyExpEndYear !=0 ){
			criteria.add(Restrictions.sqlRestriction("extract(year from warranty_expiry_date) =" + warrantyExpEndYear));		
		}
		
		if(amcSubEndMonth!=0 ){
			criteria.add(Restrictions.sqlRestriction("extract(month from cur_amc_enddate) =" + amcSubEndMonth));
		}
		if(amcSubEndYear !=0 ){
			criteria.add(Restrictions.sqlRestriction("extract(year from cur_amc_enddate) =" + amcSubEndYear));
		}
		//if man/res webadmin
		if(onBoardedBy!=0 ){
			criteria.add(Restrictions.eq("onBoardedBy", onBoardedBy));			 
		}		 

		criteria.setProjection(Projections.projectionList().add(Projections.property("id"), "id")
				.add(Projections.property("modelNumber"), "modelNumber")
				.add(Projections.property("serialNumber"), "serialNumber")
				.add(Projections.property("customerId"), "customerId")
				.add(Projections.property("manufacturerId"), "manufacturerId")
				.add(Projections.property("resellerId"), "resellerId")
				.add(Projections.property("installationDate"), "installationDate")
				.add(Projections.property("warrantyExpiryDate"), "warrantyExpiryDate")
				.add(Projections.property("locationId"), "locationId")
				.add(Projections.property("curSubscriptionStartDate"), "curSubscriptionStartDate")
				.add(Projections.property("curSubscriptionEndDate"), "curSubscriptionEndDate")
				.add(Projections.property("curAmcStartDate"), "curAmcStartDate")
				.add(Projections.property("curAmcEndDate"), "curAmcEndDate"));
		criteria.setResultTransformer(Transformers.aliasToBean(Machine.class));

		return criteria.list();

		/*
		 * String str =
		 * "select m.id, m.curSubscriptionEndDate from Machine m where extract(month from m.curSubscriptionEndDate) = :month and "
		 * + " extract(year from m.curSubscriptionEndDate) = :year"; Query query
		 * = currentSession().createQuery(str); query.setInteger("month",
		 * attendmeSubEndMonth); query.setInteger("year", attendmeSubEndYear);
		 * 
		 * List<Machine> list = query.list(); System.out.println("got list");
		 * System.out.println(list); return list;
		 */

	}
	public List<String> getAllMachineModelNumbers() {
		
		Criteria criteria = currentSession().createCriteria(Machine.class);		
		criteria.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("modelNumber"), "modelNumber")));
		criteria.setResultTransformer(Transformers.aliasToBean(Machine.class));
		
		return criteria.list();
	}
	
public List<String> getAllMachineModelNumbersByManufacturerId(long manufacturerId) {
		
		Criteria criteria = currentSession().createCriteria(Machine.class);
		criteria.add(Restrictions.eq("manufacturerId", manufacturerId));	
		criteria.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("modelNumber"), "modelNumber")));
		criteria.setResultTransformer(Transformers.aliasToBean(Machine.class));
		
		return criteria.list();
	}

	 
}
