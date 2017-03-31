package com.sparkonix.entity.dto;

public class ResetPasswordCheckDTO {
	
	private String t1;
	private String t2;	
	private String newPassword;	
	
	public ResetPasswordCheckDTO() {
		super();
	}
	
	public ResetPasswordCheckDTO(String t1, String t2) {
		super();
		this.t1 = t1;
		this.t2 = t2;
	}
	
	
	public String getNewPassword() {
		return newPassword;
	}


	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}


	public String getT1() {
		return t1;
	}
	public void setT1(String t1) {
		this.t1 = t1;
	}
	public String getT2() {
		return t2;
	}
	public void setT2(String t2) {
		this.t2 = t2;
	}
}
