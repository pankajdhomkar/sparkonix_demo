package com.sparkonix.api;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.apache.log4j.Logger;
import com.sparkonix.ApplicationContext;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.User;
import com.sparkonix.utils.MD5;

public class CreateAdminUser {

	private static final Logger log = Logger.getLogger(CreateAdminUser.class);
	private UserDAO userDAO;
	private SessionFactory sessionFactory;
	private MD5 md5 = new MD5();

	public CreateAdminUser(UserDAO userDAO, SessionFactory sessionFactory) {
		this.userDAO = userDAO;
		this.sessionFactory = sessionFactory;
	}

	public void createSuperAdminUser() {
		Session session = null;
		Transaction transaction = null;
		String saUsername = ApplicationContext.getInstance().getConfig().getSuperadminUsername();
		String saPassword = ApplicationContext.getInstance().getConfig().getSuperadminPassword();

		String encryptedPassword = md5.generateMD5(saPassword);

		try {
			session = sessionFactory.openSession();
			ManagedSessionContext.bind(session);
			transaction = session.beginTransaction();

			User user = userDAO.checkSuperAdminByUsername(saUsername);
			if (user == null) {
				User newUser = new User();
				newUser.setName("Anand Pathak");
				newUser.setMobile("0");
				newUser.setEmail(saUsername);
				newUser.setPassword(encryptedPassword);
				newUser.setRole("SUPERADMIN");
				newUser.setNotificationType(User.NOTIFICATION_TYPE.BOTH.toString());

				userDAO.save(newUser);
				log.info("Super admin created");
			} else {
				user.setPassword(encryptedPassword);
				userDAO.save(user);
				log.info("Super admin updated");
			}
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null)
				transaction.rollback();
			log.error("Failed to create super admin. Error: " + e.getMessage(), e);
		} finally {
			if (session != null)
				session.close();

			ManagedSessionContext.unbind(sessionFactory);
		}
	}
}
