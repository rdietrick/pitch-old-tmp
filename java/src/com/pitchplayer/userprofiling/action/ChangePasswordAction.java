package com.pitchplayer.userprofiling.action;

import com.opensymphony.xwork2.Action;
import com.pitchplayer.action.BaseAction;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.User;

public class ChangePasswordAction extends BaseAction {

	private String crntPassword;
	private String newPassword;
	private String newPasswordConfirmation;
	private UserService userService;

	public String execute() {
		User user = this.getSessionUser();
		if (user == null) {
			return Action.LOGIN;
		}
		try {
			if (!userService.updatePassword(user, crntPassword, newPassword)) {
				addActionError("Incorrect password.  Please try again.");
				return ERROR;
			}
		} catch (Throwable t) {
			log.error("Error updating user", t);
			addActionError("System error.  Please try again later");
			return ERROR;
		}
		return SUCCESS;
	}

	public String getCrntPassword() {
		return crntPassword;
	}

	public void setCrntPassword(String crntPassword) {
		this.crntPassword = crntPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public String getNewPasswordConfirmation() {
		return newPasswordConfirmation;
	}

	public void setNewPasswordConfirmation(String newPasswordConfirmation) {
		this.newPasswordConfirmation = newPasswordConfirmation;
	}
}
