package com.pitchplayer.userprofiling.util;

import com.pitchplayer.userprofiling.om.User;

public interface FriendUpdateListener {

	/**
	 * Get the username of the user that this listener is attached to.
	 * @return
	 */
	public String getUsername();
	
	/**
	 * Notify this listener that a user logged in/out.
	 * @param user
	 * @param loginStatus
	 */
	public void notifyUserStatusUpdate(User user, boolean loginStatus);
	
	/**
	 * Notify this listener that a friend request was accepted.
	 * @param newFriend
	 */
	public void notifyFriendAdded(User newFriend);
	
}
