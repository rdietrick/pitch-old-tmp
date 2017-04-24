package com.pitchplayer.userprofiling.om;

// Generated Oct 10, 2008 4:02:44 PM by Hibernate Tools 3.2.1.GA

import java.util.Date;

/**
 * UserPref generated by hbm2java
 */
public class UserPref implements java.io.Serializable {

	private int userId;
	private User User;
	private Integer showName;
	private Integer showCity;
	private Integer showState;
	private Boolean newsletterSubscriptionStatus;
	private Date dateUpdated;
	
	public static final int SHOW_EVERYONE = 2;
	public static final int SHOW_FRIENDS = 1;
	public static final int SHOW_NEVER = 0;

	public UserPref() {
	}

	public UserPref(int userId, User User, Integer showName,
			Integer showCity, Integer showState, Boolean newsletterSubscriptionStatus) {
		this.userId = userId;
		this.User = User;
		this.showName = showName;
		this.showCity = showCity;
		this.showState = showState;
		this.newsletterSubscriptionStatus = newsletterSubscriptionStatus;
	}

	public UserPref(int userId, User User, Integer showName,
			Integer showCity, Integer showState, Boolean newsletterSubscriptionStatus,
			Date dateUpdated) {
		this.userId = userId;
		this.User = User;
		this.showName = showName;
		this.showCity = showCity;
		this.showState = showState;
		this.newsletterSubscriptionStatus = newsletterSubscriptionStatus;
		this.dateUpdated = dateUpdated;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public User getUser() {
		return this.User;
	}

	public void setUser(User User) {
		this.User = User;
	}

	public Integer getShowName() {
		return this.showName;
	}

	public void setShowName(Integer showName) {
		this.showName = showName;
	}

	public Integer getShowCity() {
		return this.showCity;
	}

	public void setShowCity(Integer showCity) {
		this.showCity = showCity;
	}

	public Integer getShowState() {
		return this.showState;
	}

	public void setShowState(Integer showState) {
		this.showState = showState;
	}

	public Date getDateUpdated() {
		return this.dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public static UserPref getDefaultUserPref() {
		UserPref p = new UserPref();
		p.setDateUpdated(new Date());
		p.setShowCity(SHOW_EVERYONE);
		p.setShowState(SHOW_EVERYONE);
		p.setShowName(SHOW_FRIENDS);
		p.setNewsletterSubscriptionStatus(true);
		return p;
	}

	public Boolean getNewsletterSubscriptionStatus() {
		return newsletterSubscriptionStatus;
	}

	public void setNewsletterSubscriptionStatus(Boolean newsletterSubscriptionStatus) {
		this.newsletterSubscriptionStatus = newsletterSubscriptionStatus;
	}

}