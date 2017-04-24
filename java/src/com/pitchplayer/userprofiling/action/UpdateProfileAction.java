package com.pitchplayer.userprofiling.action;

import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.pitchplayer.action.BaseAction;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserGamePref;
import com.pitchplayer.userprofiling.om.UserPref;

public class UpdateProfileAction extends BaseAction implements Preparable {

	private UserService userService;

	private User user;
	
	private Map<String, String> stateMap;
	
	public String viewProfile() {
		// user will already have been retrieved via prepare()
		return SUCCESS;
	}
	
	public String updateProfile() {
		userService.update(user);
		return SUCCESS;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void prepare() throws Exception {
		user = (User)getSession().get(SESSION_ATTR_USER);
		UserPref p = user.getUserPref();
		if (p == null) {
			p = UserPref.getDefaultUserPref();
			p.setUser(user);
			user.setUserPref(p);
		}
		UserGamePref gp = user.getUserGamePref();
		if (gp == null) {
			gp = UserGamePref.getDefaultUserGamePref();
			gp.setUser(user);
			user.setUserGamePref(gp);
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Map<String, String> getStateMap() {
		return stateMap;
	}

	public void setStateMap(Map<String, String> stateMap) {
		this.stateMap = stateMap;
	}
	
}
