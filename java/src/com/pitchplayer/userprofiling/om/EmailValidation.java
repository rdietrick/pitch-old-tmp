package com.pitchplayer.userprofiling.om;

import java.util.Date;

public class EmailValidation implements java.io.Serializable{

	private Integer userId;
	private User user;
	private String validationCode;
	private Date sentDate;
	private Date validatedDate;

	public EmailValidation() {
		
	}

	public EmailValidation(int userId, User user) {
        this.userId = userId;
        this.user = user;
    }
    public EmailValidation(int userId, User user, String validationCode, Date sentDate, Date validatedDate) {
       this.userId = userId;
       this.user = user;
       this.validationCode = validationCode;
       this.sentDate = sentDate;
       this.validatedDate = validatedDate;
    }
   	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
    public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getValidationCode() {
		return validationCode;
	}
	public void setValidationCode(String validationCode) {
		this.validationCode = validationCode;
	}
	public Date getSentDate() {
		return sentDate;
	}
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}
	public Date getValidatedDate() {
		return validatedDate;
	}
	public void setValidatedDate(Date validatedDate) {
		this.validatedDate = validatedDate;
	}
	
	
}
