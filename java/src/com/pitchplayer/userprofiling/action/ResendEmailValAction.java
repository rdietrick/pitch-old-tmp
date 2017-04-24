package com.pitchplayer.userprofiling.action;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.User;

public class ResendEmailValAction extends BaseAction {

	private UserService userService;
	private String emailAddress;
	
	public String execute() {
		User user = (User) getSession().get(SESSION_ATTR_TMP_USER);
		if (user == null) {
			return LOGIN;
		}
		try {
			if (!emailAddress.equals(user.getEmailAddress())) {
				user.setEmailAddress(emailAddress);
			}
			userService.sendValidationEmail(user);
			getSession().remove(SESSION_ATTR_TMP_USER);
		} catch (Throwable t) {
			log.error("Error sending email validation", t);
			addActionError("System error.  Please try again later");
			return ERROR;
		}
		return SUCCESS;
	}

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
	
}
