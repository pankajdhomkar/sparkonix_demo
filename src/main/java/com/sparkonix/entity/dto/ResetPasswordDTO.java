package com.sparkonix.entity.dto;

import java.io.Serializable;

import com.sparkonix.entity.CompanyDetail;
import com.sparkonix.entity.User;

public class ResetPasswordDTO {

	private String oldPassword;
	private String newPassword;
	private long userId;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
