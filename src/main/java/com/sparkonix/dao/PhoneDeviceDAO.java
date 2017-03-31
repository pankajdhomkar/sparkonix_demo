package com.sparkonix.dao;

import java.util.List;

import org.apache.xmlbeans.impl.xb.xsdschema.RestrictionDocument.Restriction;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.sparkonix.entity.PhoneDevice;

import io.dropwizard.hibernate.AbstractDAO;

public class PhoneDeviceDAO extends AbstractDAO<PhoneDevice> {

	public PhoneDeviceDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public PhoneDevice save(PhoneDevice phoneDevice) throws Exception {
		return persist(phoneDevice);
	}

	public PhoneDevice getById(long phoneDeviceId) throws Exception {
		return get(phoneDeviceId);
	}

	public List<PhoneDevice> findAll() {
		return list(namedQuery("com.sparkonix.entity.PhoneDevice.findAll"));
	}

	/*public PhoneDevice getOperatorByPhoneNumber(String phoneNumber) {
		return uniqueResult(namedQuery("com.sparkonix.entity.PhoneDevice.getOperatorByPhoneNumber")
				.setParameter("PHONE_NUMBER", phoneNumber));
	}*/
	public PhoneDevice getOperatorByPhoneNumber(String phoneNumber) {
		
		List<PhoneDevice> operatorList= list(namedQuery("com.sparkonix.entity.PhoneDevice.getOperatorByPhoneNumber")
				.setParameter("PHONE_NUMBER", phoneNumber));
		if(!operatorList.isEmpty()){
			return operatorList.get(0);
		}else{
			return null;
		}
		/*return list(namedQuery("com.sparkonix.entity.PhoneDevice.getOperatorByPhoneNumber")
				.setParameter("PHONE_NUMBER", phoneNumber)).get(0);*/
	}

	public PhoneDevice getOperatorByMobileNumCustomerIdLocationId(String phoneNumber, long customerId,
			long locationId) {
		return uniqueResult(namedQuery("com.sparkonix.entity.PhoneDevice.getOperatorByMobileNumCustomerIdLocationId")
				.setParameter("PHONE_NUMBER", phoneNumber)
				.setParameter("CUSTOMER_ID", customerId)
				.setParameter("LOCATION_ID", locationId));
	}

	public List<PhoneDevice> findAllByCustomerIdAndOnBoardedId(long customerId, long onBoardedById) {
		return list(namedQuery("com.sparkonix.entity.PhoneDevice.findAllByCustomerIdAndOnBoardedId")
				.setParameter("CUSTOMER_ID", customerId).setParameter("ON_BOARDED_BY", onBoardedById));
	}

	public List<PhoneDevice> findAllByCustomerId(long customerId) {
		return list(namedQuery("com.sparkonix.entity.PhoneDevice.findAllByCustomerId").setParameter("CUSTOMER_ID",
				customerId));
	}

	public long getCountByCustomerIdAndOnBoardedBy(long customerId, long onBoardedById, String userRole) {
		Query query;
		if (userRole.equals("SUPERADMIN")) {
			query = currentSession()
					.createQuery("select count(*) from PhoneDevice pd where pd.customerId= :CUSTOMER_ID");
			query.setParameter("CUSTOMER_ID", customerId);
			return (Long) query.uniqueResult();
		}

		// SALESTEAM OR WEBADMIN
		query = currentSession().createQuery(
				"select count(*) from PhoneDevice pd where pd.customerId= :CUSTOMER_ID and pd.onBoardedBy= :ON_BOARDED_BY");
		query.setParameter("CUSTOMER_ID", customerId);
		query.setParameter("ON_BOARDED_BY", onBoardedById);

		return (Long) query.uniqueResult();
	}

	public List<String> getAllFcmTokens() {
		Criteria criteria = currentSession().createCriteria(PhoneDevice.class, "PhoneDevice");
		criteria.add(Restrictions.isNotNull("fcmToken"));
		criteria.setProjection(Projections.projectionList().add(Projections.property("fcmToken"), "fcmToken"));
		// criteria.setResultTransformer(Transformers.aliasToBean(PhoneDevice.class));
		return criteria.list();

	}
}
