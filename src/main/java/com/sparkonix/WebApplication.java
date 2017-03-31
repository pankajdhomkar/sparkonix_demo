package com.sparkonix;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.sparkonix.api.CreateAdminUser;
import com.sparkonix.auth.WebAuthenticator;
import com.sparkonix.dao.ClosedIssueDAO;
import com.sparkonix.dao.CompanyDetailDAO;
import com.sparkonix.dao.CompanyLocationDAO;
import com.sparkonix.dao.IssueDAO;
import com.sparkonix.dao.MachineAmcHistoryDAO;
import com.sparkonix.dao.MachineAmcServiceHistoryDAO;
import com.sparkonix.dao.MachineDAO;
import com.sparkonix.dao.MachineDocumentDAO;
import com.sparkonix.dao.MachineSubscriptionHistoryDAO;
import com.sparkonix.dao.PhoneDeviceDAO;
import com.sparkonix.dao.QRCodeDAO;
import com.sparkonix.dao.ResetPasswordTokenDAO;
import com.sparkonix.dao.SubscriptionHistoryDAO;
import com.sparkonix.dao.UserDAO;
import com.sparkonix.entity.ClosedIssue;
import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.CompanyLocation;
import com.sparkonix.entity.Issue;
import com.sparkonix.entity.Machine;
import com.sparkonix.entity.MachineAmcHistory;
import com.sparkonix.entity.MachineAmcServiceHistory;
import com.sparkonix.entity.MachineDocument;
import com.sparkonix.entity.PasswordResetToken;
import com.sparkonix.entity.PhoneDevice;
import com.sparkonix.entity.QRCode;
import com.sparkonix.entity.SubscriptionHistory;
import com.sparkonix.entity.User;
import com.sparkonix.resources.BroadcastMessagesResource;
import com.sparkonix.resources.ClosedIssueResource;
import com.sparkonix.resources.ClosedIssuesResource;
import com.sparkonix.resources.CompanyDetailResource;
import com.sparkonix.resources.CompanyDetailsResource;
import com.sparkonix.resources.CompanyLocationResource;
import com.sparkonix.resources.CompanyLocationsResource;
import com.sparkonix.resources.IssueResource;
import com.sparkonix.resources.IssuesResource;
import com.sparkonix.resources.LoginResource;
import com.sparkonix.resources.MachineAmcHistoriesResource;
import com.sparkonix.resources.MachineAmcHistoryResource;
import com.sparkonix.resources.MachineAmcServiceHistoriesResource;
import com.sparkonix.resources.MachineAmcServiceHistoryResource;
import com.sparkonix.resources.MachineDocumentResource;
import com.sparkonix.resources.MachineDocumentsResource;
import com.sparkonix.resources.MachineResource;
import com.sparkonix.resources.MachineSubscriptionHistoriesResource;
import com.sparkonix.resources.MachineSubscriptionHistoryResource;
import com.sparkonix.resources.MachinesResource;
import com.sparkonix.resources.PhoneDeviceResource;
import com.sparkonix.resources.PhoneDevicesResource;
import com.sparkonix.resources.PrivacyPolicyResource;
import com.sparkonix.resources.QRCodeResource;
import com.sparkonix.resources.QRCodesResource;
import com.sparkonix.resources.SubscriptionHistoriesResource;
import com.sparkonix.resources.SubscriptionHistoryResource;
import com.sparkonix.resources.SubscriptionReportResource;
import com.sparkonix.resources.TermsConditionResource;
import com.sparkonix.resources.UserResource;
import com.sparkonix.resources.UsersResource;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class WebApplication extends Application<WebConfiguration> {
	public static void main(String[] args) throws Exception {
		new WebApplication().run(args);
	}

	private final HibernateBundle<WebConfiguration> hibernateBundle = new HibernateBundle<WebConfiguration>(
			ClosedIssue.class, CompanyDetail.class, CompanyLocation.class, Issue.class, Machine.class,
			MachineAmcHistory.class, MachineAmcServiceHistory.class, MachineDocument.class, PhoneDevice.class,
			SubscriptionHistory.class, com.sparkonix.entity.User.class, QRCode.class, PasswordResetToken.class) {
		@Override
		public DataSourceFactory getDataSourceFactory(WebConfiguration configuration) {
			return configuration.getDataSourceFactory();
		}
	};

	@Override
	public void initialize(Bootstrap<WebConfiguration> bootstrap) {
		// Enable variable substitution with environment variables
		bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
				bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));

		bootstrap.addBundle(new AssetsBundle("/webapp", "/", "index.html", "WebApp"));
		bootstrap.addBundle(new AssetsBundle());
		bootstrap.addBundle(new MigrationsBundle<WebConfiguration>() {
			@Override
			public DataSourceFactory getDataSourceFactory(WebConfiguration configuration) {
				return configuration.getDataSourceFactory();
			}
		});
		bootstrap.addBundle(hibernateBundle);
	}

	@Override
	public void run(WebConfiguration configuration, Environment environment) {
		ApplicationContext.init(configuration);
		((DefaultServerFactory) configuration.getServerFactory()).setJerseyRootPath("/api/*");

		final ClosedIssueDAO closedIssueDAO = new ClosedIssueDAO(hibernateBundle.getSessionFactory());
		final CompanyDetailDAO companyDetailDAO = new CompanyDetailDAO(hibernateBundle.getSessionFactory());
		final CompanyLocationDAO companyLocationDAO = new CompanyLocationDAO(hibernateBundle.getSessionFactory());
		final IssueDAO issueDAO = new IssueDAO(hibernateBundle.getSessionFactory());
		final MachineDAO machineDAO = new MachineDAO(hibernateBundle.getSessionFactory());
		final MachineAmcHistoryDAO machineAmcHistoryDAO = new MachineAmcHistoryDAO(hibernateBundle.getSessionFactory());
		final MachineAmcServiceHistoryDAO machineAmcServiceHistoryDAO = new MachineAmcServiceHistoryDAO(
				hibernateBundle.getSessionFactory());
		final MachineDocumentDAO machineDocumentDAO = new MachineDocumentDAO(hibernateBundle.getSessionFactory());
		final MachineSubscriptionHistoryDAO machineSubscriptionHistoryDAO = new MachineSubscriptionHistoryDAO(
				hibernateBundle.getSessionFactory());
		final PhoneDeviceDAO phoneDeviceDAO = new PhoneDeviceDAO(hibernateBundle.getSessionFactory());
		final SubscriptionHistoryDAO subscriptionHistoryDAO = new SubscriptionHistoryDAO(
				hibernateBundle.getSessionFactory());
		final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
		final QRCodeDAO qrcodeDAO = new QRCodeDAO(hibernateBundle.getSessionFactory());
		final ResetPasswordTokenDAO resetPasswordTokenDAO = new ResetPasswordTokenDAO(hibernateBundle.getSessionFactory());

		// for uploading multipart/form-data
		environment.jersey().register(MultiPartFeature.class);

		CachingAuthenticator<BasicCredentials, User> cachingAuthenticator = new CachingAuthenticator<>(
				environment.metrics(), new WebAuthenticator(hibernateBundle.getSessionFactory()),
				configuration.getAuthenticationCachePolicy());

		// register resources
		environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
				.setAuthenticator(cachingAuthenticator).setRealm("Sparkonix").buildAuthFilter()));
		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
		environment.jersey().register(RolesAllowedDynamicFeature.class);

		environment.jersey().register(new ClosedIssueResource(closedIssueDAO));
		environment.jersey().register(new ClosedIssuesResource(closedIssueDAO));

		environment.jersey().register(new CompanyDetailResource(companyDetailDAO, userDAO));
		environment.jersey().register(
				new CompanyDetailsResource(companyDetailDAO, companyLocationDAO, machineDAO, phoneDeviceDAO, userDAO));

		environment.jersey().register(new CompanyLocationResource(companyLocationDAO));
		environment.jersey().register(new CompanyLocationsResource(companyLocationDAO));

		environment.jersey()
				.register(new IssueResource(issueDAO, phoneDeviceDAO, machineDAO, companyDetailDAO, userDAO));
		environment.jersey().register(new IssuesResource(issueDAO, phoneDeviceDAO, machineDAO, companyDetailDAO,
				userDAO, companyLocationDAO));

		environment.jersey().register(new MachineAmcHistoryResource(machineAmcHistoryDAO));
		environment.jersey().register(new MachineAmcHistoriesResource(machineAmcHistoryDAO));

		environment.jersey()
				.register(new MachineAmcServiceHistoryResource(machineAmcServiceHistoryDAO, machineDAO, userDAO));
		environment.jersey()
				.register(new MachineAmcServiceHistoriesResource(machineAmcServiceHistoryDAO, machineDAO, userDAO));

		environment.jersey()
				.register(new MachineDocumentResource(machineDocumentDAO, configuration.getMachineDocsDirectory()));
		environment.jersey().register(new MachineDocumentsResource(machineDocumentDAO,
				configuration.getMachineDocsDirectory(), companyDetailDAO));

		environment.jersey().register(new MachineResource(machineDAO, companyDetailDAO, machineAmcServiceHistoryDAO,
				qrcodeDAO, phoneDeviceDAO));
		environment.jersey()
				.register(new MachinesResource(machineDAO, companyDetailDAO, companyLocationDAO, qrcodeDAO));

		environment.jersey().register(new MachineSubscriptionHistoryResource(machineSubscriptionHistoryDAO));
		environment.jersey().register(new MachineSubscriptionHistoriesResource(machineSubscriptionHistoryDAO));

		environment.jersey().register(new PhoneDeviceResource(phoneDeviceDAO));
		environment.jersey().register(new PhoneDevicesResource(phoneDeviceDAO, companyLocationDAO));

		environment.jersey().register(new SubscriptionHistoryResource(subscriptionHistoryDAO));
		environment.jersey().register(new SubscriptionHistoriesResource(subscriptionHistoryDAO));

		environment.jersey().register(new UserResource(userDAO, resetPasswordTokenDAO));
		environment.jersey().register(new UsersResource(userDAO));

		environment.jersey().register(new QRCodeResource(qrcodeDAO, machineDAO));
		environment.jersey().register(new QRCodesResource(qrcodeDAO, userDAO));

		environment.jersey().register(new LoginResource(userDAO));

		// add Super Admin if not exist(yml)
		CreateAdminUser createAdminUser = new CreateAdminUser(userDAO, hibernateBundle.getSessionFactory());
		createAdminUser.createSuperAdminUser();

		environment.jersey().register(new TermsConditionResource());
		environment.jersey().register(new PrivacyPolicyResource());

		environment.jersey().register(new SubscriptionReportResource(machineDAO, companyDetailDAO, companyLocationDAO));

		environment.jersey().register(new BroadcastMessagesResource(phoneDeviceDAO));
	}
}
