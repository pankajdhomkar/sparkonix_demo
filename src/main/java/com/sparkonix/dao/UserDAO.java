package com.sparkonix.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.sparkonix.entity.User;

import io.dropwizard.hibernate.AbstractDAO;

public class UserDAO extends AbstractDAO<User> {

	public UserDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public User save(User user) throws Exception {
		return persist(user);
	}

	public User getById(long userId) throws Exception {
		// return get(userId);
		return uniqueResult(namedQuery("com.sparkonix.entity.User.findByID").setParameter("ID", userId));
	}

	public List<User> findAll() {
		return list(namedQuery("com.sparkonix.entity.User.findAll"));
	}

	public User findUserByUsernameAndPassword(String username, String password) {
		return uniqueResult(namedQuery("com.sparkonix.entity.User.findUserByUsernameAndPassword")
				.setParameter("USERNAME", username).setParameter("PASSWORD", password));
	}

	public User checkSuperAdminByUsername(String superadminUsername) {
		return uniqueResult(namedQuery("com.sparkonix.entity.User.checkSuperAdminByUsername")
				.setParameter("USERNAME", superadminUsername).setParameter("ROLE", "SUPERADMIN"));
	}

	public Object findByRole(String role) {
		return list(namedQuery("com.sparkonix.entity.User.findByRole").setParameter("ROLE", role));
	}

	public void delete(long userid) throws Exception {
		currentSession().delete(getById(userid));
	}

	public User findByEmail(String email) {
		return uniqueResult(namedQuery("com.sparkonix.entity.User.findByEmail").setParameter("EMAIL", email));
	}

	public User getByCompanyDetailsIdAndRole(long companyDetailId, String role) {
		return uniqueResult(namedQuery("com.sparkonix.entity.User.findByCompanyDetailsIdAndRole")
				.setParameter("COMPANY_DETAIL_ID", companyDetailId).setParameter("ROLE", role));

	}

	public Object findAllByRoleCompanyDetailsId(String role, long companyDetailId) {
		return list(namedQuery("com.sparkonix.entity.User.findByCompanyDetailsIdAndRole")
				.setParameter("COMPANY_DETAIL_ID", companyDetailId).setParameter("ROLE", role));

	}

}
