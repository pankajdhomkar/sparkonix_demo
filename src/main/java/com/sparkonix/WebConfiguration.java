package com.sparkonix;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilderSpec;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class WebConfiguration extends Configuration {
	@NotEmpty
	private String template;

	@NotEmpty
	private String defaultName = "Stranger";

	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	@Valid
	@NotNull
	private CacheBuilderSpec authenticationCachePolicy;

	@Valid
	@NotNull
	private String domainurl;

	@Valid
	@NotNull
	@JsonProperty("mailUsername")
	private String mailUsername;

	@Valid
	@NotNull
	@JsonProperty("mailPassword")
	private String mailPassword;

	@Valid
	@NotNull
	@JsonProperty("mailHost")
	private String mailHost;

	@Valid
	@NotNull
	@JsonProperty("mailSetFrom")
	private String mailSetFrom;

	@Valid
	@NotNull
	@JsonProperty("smsSenderId")
	private String smsSenderId;

	@Valid
	@NotNull
	@JsonProperty("smsApiKey")
	private String smsApiKey;

	@Valid
	@NotNull
	@JsonProperty("smsApiUrl")
	private String smsApiUrl;

	@Valid
	@NotNull
	@JsonProperty("superadminUsername")
	private String superadminUsername;

	@Valid
	@NotNull
	@JsonProperty("superadminEmail")
	private String superadminEmail;

	@Valid
	@NotNull
	@JsonProperty("superadminPassword")
	private String superadminPassword;

	@Valid
	@NotNull
	@JsonProperty("qrCodeImagesDirectory")
	private String qrCodeImagesDirectory;

	@Valid
	@NotNull
	@JsonProperty("fcmServerKey")
	private String fcmServerKey;

	@Valid
	@NotNull
	@JsonProperty("machineDocsDirectory")
	private String machineDocsDirectory;

	public String getDomainurl() {
		return domainurl;
	}

	public void setDomainurl(String domainurl) {
		this.domainurl = domainurl;
	}

	public String getMailUsername() {
		return mailUsername;
	}

	public void setMailUsername(String mailUsername) {
		this.mailUsername = mailUsername;
	}

	public String getMailPassword() {
		return mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	public CacheBuilderSpec getAuthenticationCachePolicy() {
		return authenticationCachePolicy;
	}

	public void setAuthenticationCachePolicy(CacheBuilderSpec authenticationCachePolicy) {
		this.authenticationCachePolicy = authenticationCachePolicy;
	}

	@JsonProperty
	public String getTemplate() {
		return template;
	}

	@JsonProperty
	public void setTemplate(String template) {
		this.template = template;
	}

	@JsonProperty
	public String getDefaultName() {
		return defaultName;
	}

	@JsonProperty
	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory() {
		return database;
	}

	@JsonProperty("database")
	public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
		this.database = dataSourceFactory;
	}

	public String getSmsSenderId() {
		return smsSenderId;
	}

	public void setSmsSenderId(String smsSenderId) {
		this.smsSenderId = smsSenderId;
	}

	public String getSuperadminUsername() {
		return superadminUsername;
	}

	public void setSuperadminUsername(String superadminUsername) {
		this.superadminUsername = superadminUsername;
	}

	public String getSuperadminPassword() {
		return superadminPassword;
	}

	public void setSuperadminPassword(String superadminPassword) {
		this.superadminPassword = superadminPassword;
	}

	public String getMailHost() {
		return mailHost;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public String getMailSetFrom() {
		return mailSetFrom;
	}

	public void setMailSetFrom(String mailSetFrom) {
		this.mailSetFrom = mailSetFrom;
	}

	public String getQrCodeImagesDirectory() {
		return qrCodeImagesDirectory;
	}

	public void setQrCodeImagesDirectory(String qrCodeImagesDirectory) {
		this.qrCodeImagesDirectory = qrCodeImagesDirectory;
	}

	public String getSmsApiUrl() {
		return smsApiUrl;
	}

	public void setSmsApiUrl(String smsApiUrl) {
		this.smsApiUrl = smsApiUrl;
	}

	public String getSuperadminEmail() {
		return superadminEmail;
	}

	public String getSmsApiKey() {
		return smsApiKey;
	}

	public void setSmsApiKey(String smsApiKey) {
		this.smsApiKey = smsApiKey;
	}

	public void setSuperadminEmail(String superadminEmail) {
		this.superadminEmail = superadminEmail;
	}

	public String getFcmServerKey() {
		return fcmServerKey;
	}

	public void setFcmServerKey(String fcmServerKey) {
		this.fcmServerKey = fcmServerKey;
	}

	public String getMachineDocsDirectory() {
		return machineDocsDirectory;
	}

	public void setMachineDocsDirectory(String machineDocsDirectory) {
		this.machineDocsDirectory = machineDocsDirectory;
	}

}
