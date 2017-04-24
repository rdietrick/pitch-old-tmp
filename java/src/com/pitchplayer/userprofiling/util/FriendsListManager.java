package com.pitchplayer.userprofiling.util;

import java.util.HashMap;
import java.util.Map;

import com.pitchplayer.userprofiling.om.User;

public class FriendsListManager implements FriendUpdateNotifier {

	private HashMap<String, FriendUpdateListener> listeners = new HashMap<String, FriendUpdateListener>();
	
	/**
	 * Add a listener
	 */
	public void addListener(FriendUpdateListener listener) {
		listeners.put(listener.getUsername(), listener);
	}

	/**
	 * Notify an online user that they have a new friend
	 */
	public void friendAdded(User user, User friend) {
		for (Map.Entry<String, FriendUpdateListener> entry : listeners.entrySet()) {
			if (friend.getUsername().equals(entry.getKey())) {
				entry.getValue().notifyFriendAdded(user);
				return;
			}
		}
	}

	/**
	 * Remove a listener
	 */
	public void removeListener(FriendUpdateListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify users that another user's login status changed.
	 */
	public void updateUserStatus(User user, boolean loginStatus) {
		for (FriendUpdateListener listener : listeners.values()) {
			listener.notifyUserStatusUpdate(user, loginStatus);
		}
	}

}
