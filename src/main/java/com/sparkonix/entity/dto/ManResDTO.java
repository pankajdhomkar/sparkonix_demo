package com.sparkonix.entity.dto;

import java.io.Serializable;

import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.User;

public class ManResDTO implements Serializable {

	private static final long serialVersionUID = 3090830713597004836L;

	CompanyDetail manResDetail;
	User webAdminUser;

	public CompanyDetail getManResDetail() {
		return manResDetail;
	}

	public void setManResDetail(CompanyDetail manResDetail) {
		this.manResDetail = manResDetail;
	}

	public User getWebAdminUser() {
		return webAdminUser;
	}

	public void setWebAdminUser(User webAdminUser) {
		this.webAdminUser = webAdminUser;
	}

}
