package com.pitchplayer.userprofiling.action;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.server.game.player.DWRPlayerProxy;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.EmailValidation;
import com.pitchplayer.userprofiling.om.User;

public class LoginAction extends BaseAction implements ServletRequestAware {

	private static final String RESULT_MSG_BOARDS = "msgBoards";
	private static final String RESULT_EMAIL_VAL_REQUIRED = "emailValRequired";
	// private static final String RESULT_NO_EMAIL_VAL = "noEmailVal";
	private String username;
	private String password;
	private String vCode;
	private boolean boardsLogin = false;
	private UserService userService;
	private HttpServletRequest request;

	Logger log = Logger.getLogger(this.getClass().getName());

	public String execute() {
		User user = null;
		try {
			user = userService.authenticateUser(username, password);
		} catch (Throwable t) {
			log.error("error authenticating user", t);
			addActionError("System error.  Please try again later.");
			return ERROR;
		}
		if (user == null) {
			log.debug("login failed for user " + username);
			addActionError("Authentication failed.  Please retype your username and password.");
			return ERROR;
		}
		else if (!user.isEmailValidated()) { // if email not validated
			if (vCode != null) {
				// do email validation
				EmailValidation eVal = user.getEmailValidation();
				if (!userService.validateEmail(user, vCode)) {
					// validation failed
					this.addActionError("Invalid email validation code.");
					return ERROR;
				}
				else {
					log.debug("email validation successful.");
					// everything is cool, user now logged in
				}
			}
			else {
				this.getSession().put(SESSION_ATTR_TMP_USER, user);
				if (user.getEmailValidation() == null) {
					// user was never sent an email validation message
				}
				else {
					addActionError("Your email address has not been confirmed yet.  " + 
							"Please refer to the message sent to you at " + user.getEmailAddress() + 
							" with important instructions for activating your account.  " + 
							"If you have deleted the original message, use the form below to have an account activation message resent to your email address.");
				}
				return RESULT_EMAIL_VAL_REQUIRED;
			}
		}
		// everything OK
		setSessionUser(user);
		// add DWRLobby
		if (boardsLogin) {
			return RESULT_MSG_BOARDS;
		}
		else {
			log.debug("returning success");
			return SUCCESS;
		}
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isBoardsLogin() {
		return boardsLogin;
	}

	public void setBoardsLogin(boolean boardsLogin) {
		this.boardsLogin = boardsLogin;
	}

	public String getVCode() {
		return vCode;
	}

	public void setVCode(String code) {
		vCode = code;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}


	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
