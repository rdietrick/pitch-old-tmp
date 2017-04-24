package com.pitchplayer.userprofiling;

import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.pitchplayer.userprofiling.om.User;

public class DbBackedUserStore implements UserStore {

	private static DbBackedUserStore instance;

	private UserService userService;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private DbBackedUserStore() {
	}
	
	/**
	 * Add a user to the user store, indicating that they have established an 
	 * authenticated HTTP session.
	 */
	public void addUser(String sessionId, User user) {
		user.setLoggedIn(true);
		user.setSessionId(sessionId);
		user.setLastLogin(new Date());
		if (user.getLoginCount() == null) {
			user.setLoginCount(new Integer(1));
		}
		else {
			user.setLoginCount(new Integer(user.getLoginCount().intValue() + 1));
		}
		userService.update(user);
	}

	/**
	 * Mark a user as being connected via a Pitch client.
	 */
	public User connectUser(String sessionId) {
		User user = userService.getUserBySessionId(sessionId);
		if (user != null) {
			user.setLoggedIn(true);
			userService.update(user);
		}
		return user;
	}

	public String[] getUsernames() {
		return userService.getActiveUsernames();
	}

	public Collection getUsers() {
		return userService.getActiveUsers();
	}

	/**
	 * Completely remove a user from the store, indicating that their HTTP and Pitch sessions
	 * are closed.
	 */
	public void removeUser(User user) {
		user.setSessionId(null);
		user.setLoggedIn(false);
		userService.update(user);
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void disconnectUser(User user) {
		user.setLoggedIn(false);
		userService.update(user);
	}

}
