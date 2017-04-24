package com.pitchplayer.userprofiling.util;

import com.pitchplayer.userprofiling.om.User;

public interface FriendUpdateNotifier {

	/**
	 * Add a listener to this notifier
	 * @param listener
	 */
	public void addListener(FriendUpdateListener listener);
	
	/**
	 * Remove an attached listener
	 * @param listener
	 */
	public void removeListener(FriendUpdateListener listener);
	
	/**
	 * Notify listeners that a user's login status changed.
	 * @param user
	 * @param loginStatus
	 */
	public void updateUserStatus(User user, boolean loginStatus);
	
	
	/**
	 * Notify listeners that a friend request was accepted.
	 * @param user
	 * @param friend
	 */
	public void friendAdded(User user, User friend);
	
	
}
