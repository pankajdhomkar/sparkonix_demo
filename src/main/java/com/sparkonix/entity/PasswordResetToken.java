package com.sparkonix.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "reset_password_token")
@NamedQueries({
	@NamedQuery(name = "com.sparkonix.entity.PasswordResetToken.findToken", query = "SELECT t FROM PasswordResetToken t WHERE t.token = :TOKEN"),
	@NamedQuery(name = "com.sparkonix.entity.PasswordResetToken.findByTokens", query = "SELECT u FROM PasswordResetToken u WHERE u.token = :TOKEN AND u.secondToken = :SECOND_TOKEN")})
public class PasswordResetToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4611768919334775743L;
	private static final long SECOND = 1000l;
	private static final long MINUTE = SECOND * 60;
	private static final long HOUR = MINUTE * 60;
	public static final long EXPIRY_PERIOD = HOUR * 24;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "token", nullable = false)
	private String token;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "expiry_date", nullable = false)
	private Date expiryDate;

	@Column(name = "second_token")
	private String secondToken;	

	public PasswordResetToken() {
		super();
	}

	public PasswordResetToken(String token) {
		super();
		this.token = token;
	}

	public String getSecondToken() {
		return secondToken;
	}

	public void setSecondToken(String secondToken) {
		this.secondToken = secondToken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	public boolean isExpired() {
		Calendar cal = Calendar.getInstance();
	    if ((expiryDate
	        .getTime() - cal.getTime()
	        .getTime()) <= 0) {
	        return true;
	    } 
	    return false;
	}
}
