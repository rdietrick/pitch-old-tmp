package com.pitchplayer.userprofiling;

import java.util.Collection;

import com.pitchplayer.userprofiling.om.User;

public interface UserStore {

	/**
	 * Add an authenticated user
	 */
	public void addUser(String sessionId, User user);

	/**
	 * Remove a user by sessionId, indicating that their HTTP & Pitch session is closed.
	 */
	public void removeUser(User user);

	/**
	 * Update a user in the store to indicate that they are no longer connected via a Pitch client.
	 * @param user
	 */
	public void disconnectUser(User user);
	
	/**
	 * Get a user from the store if it's there
	 * 
	 * @return a user matching the session id or null if the user could not be
	 *         located.
	 */
	public User connectUser(String sessionId);

	/**
	 * Get the list of logged-in users.
	 */
	public Collection<User> getUsers();

	/**
	 * I don't think this should be used.
	 * @return
	 */
	public String[] getUsernames();

}