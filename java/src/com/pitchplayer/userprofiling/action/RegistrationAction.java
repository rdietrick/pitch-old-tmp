package com.pitchplayer.userprofiling.action;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.Preparable;
import com.pitchplayer.action.BaseAction;
import com.pitchplayer.db.DbException;
import com.pitchplayer.userprofiling.DuplicateUserException;
import com.pitchplayer.userprofiling.UserAssociationService;
import com.pitchplayer.userprofiling.UserAssociationType;
import com.pitchplayer.userprofiling.UserInvitationService;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.UserStore;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserConstants;
import com.pitchplayer.userprofiling.om.UserHomeAddress;
import com.pitchplayer.userprofiling.om.UserInvitation;

public class RegistrationAction extends BaseAction implements Preparable, ServletRequestAware {
	private Logger log = Logger.getLogger(this.getClass().getName());
	private User user = new User();
	private UserStore userStore;
	private UserService userService;
	UserInvitationService userInvitationService;
	UserAssociationService userAssociationService;
	private String passwdConfirmation;
	private int dobMonth, dobDay, dobYear;
	private Date dob;
	private String invitationCode;
	HttpServletRequest request;
	
	public RegistrationAction() {
		UserHomeAddress addr = new UserHomeAddress();
		user.setUserHomeAddress(addr);
		addr.setUser(user);		
	}
	
	public void prepare() {
		user.setLoginCount(0);
		user.setUserType(UserConstants.USER_TYPE_HUMAN);
		user.setRegistrationDate(new Date());
		user.setLoggedIn(false);
	}	
	
	public void validate() {
		if (dobMonth == 0 || dobYear == 0 || dobDay == 0) {
			addActionError("Please enter a valid birthdate.");
			return;
		}
		if (dobYear < 1900) {
			addActionError("Please enter a valid year.");
		}
		Calendar cal = Calendar.getInstance();
		cal.roll(Calendar.YEAR, -16);
		Date minAge = cal.getTime();
		cal.set(Calendar.YEAR, dobYear);
		cal.set(Calendar.MONTH, dobMonth-1); // months are zero-based
		cal.set(Calendar.DATE, dobDay);
		dob = cal.getTime();
		log.debug("minAge = " + minAge.toString() + ", dob = " + dob.toString());
		if (minAge.before(dob)) {
			addActionError("Sorry!  You must be 16 years old in order to register.");
		}
	}
	
	public String execute() {
		user.setBirthDate(dob);
		UserInvitation invitation = null;
		if (invitationCode != null && !invitationCode.trim().equals("")) {
			invitation = userInvitationService.getByInvitationCode(invitationCode);
		}
		try {
			userService.createUser(user, (invitation != null && invitation.getInviteeEmail().equalsIgnoreCase(user.getEmailAddress())));
		} catch (DuplicateUserException due) {
			this.addActionError(due.getMessage());
			return ERROR;
		} catch (Throwable t) {
			log.error("Error creating user", t);
			this.addActionError("System error.  Please try again later.");
			return ERROR;
		}
		if (invitation != null) {
			// user was invited, link user to the inviter
			UserInvitation invite = userInvitationService.getByInvitationCode(invitationCode);
			if (invite != null) {
				invite.setStatus((byte) 1);
				userInvitationService.update(invite);
				try {
					userAssociationService.associateUsers(user, invite.getUser(), UserAssociationType.FRIEND);
				} catch (DbException e) {
					log.error("Error associating users", e);
				}
			}
			else {
				log.warn("Invitation code not found: " + invitationCode);
			}
			if (invitation.getInviteeEmail().equalsIgnoreCase(user.getEmailAddress())) {
				setSessionUser(user);
				return "login-success";
			}
		}
		return SUCCESS;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public String getPasswdConfirmation() {
		return passwdConfirmation;
	}

	public void setPasswdConfirmation(String passwdConfirmation) {
		this.passwdConfirmation = passwdConfirmation;
	}

	public int getDobMonth() {
		return dobMonth;
	}

	public void setDobMonth(int dobMonth) {
		this.dobMonth = dobMonth;
	}

	public int getDobDay() {
		return dobDay;
	}

	public void setDobDay(int dobDay) {
		this.dobDay = dobDay;
	}

	public int getDobYear() {
		return dobYear;
	}

	public void setDobYear(int dobYear) {
		this.dobYear = dobYear;
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	public UserInvitationService getUserInvitationService() {
		return userInvitationService;
	}

	public void setUserInvitationService(UserInvitationService userInvitationService) {
		this.userInvitationService = userInvitationService;
	}

	public UserAssociationService getUserAssociationService() {
		return userAssociationService;
	}

	public void setUserAssociationService(
			UserAssociationService userAssociationService) {
		this.userAssociationService = userAssociationService;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public UserStore getUserStore() {
		return userStore;
	}

	public void setUserStore(UserStore userStore) {
		this.userStore = userStore;
	}
	
	
}
