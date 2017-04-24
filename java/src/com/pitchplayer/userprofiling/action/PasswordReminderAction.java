package com.pitchplayer.userprofiling.action;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.User;

public class PasswordReminderAction extends BaseAction {

	private UserService userService;
	private String emailAddress;

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String execute() {
		User user = null;
		try {
			user = userService.getUserByEmailAddress(emailAddress);
		} catch (Throwable t) {
			log.error("Error querying for user by email address", t);
			addActionError("System error.  Please try again later");
			return ERROR;
		}
		if (user == null) {
			addActionError("No user with that email address could be found");
			return ERROR;
		}
		try {
			userService.sendUserPasswordReminderEmail(user);
		} catch (Throwable t) {
			log.error("Error sending user password reminder email", t);
			addActionError("Error sending email.  Please try again later");
			return ERROR;
		}
		return SUCCESS;
	}
	
}
