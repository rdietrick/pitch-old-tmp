package com.pitchplayer.userprofiling.util;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.pitchplayer.userprofiling.om.User;

public class UserFriendsList implements FriendUpdateListener {

	private String username;
	private Collection<UserFriend> friendUpdates;

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public UserFriendsList(String username) {
		this.username = username;
		this.friendUpdates = new ArrayList<UserFriend>();
	}
	
	public UserFriendsList(String username, Collection<String> onlineFriends, 
			Collection<String> offlineFriends) {
		this.username = username;
		for (String name : onlineFriends) {
			friendUpdates.add(new UserFriend(name, true));
		}
		for (String name : offlineFriends) {
			friendUpdates.add(new UserFriend(name, false));
		}
	}
	
	public String getUsername() {
		return username;
	}

	public void notifyFriendAdded(User newFriend) {
		UserFriend friend = new UserFriend(newFriend.getUsername(), newFriend.getLoggedIn());
		friend.setNew(true);
		friendUpdates.add(friend);
	}

	public void notifyUserStatusUpdate(User user, boolean loginStatus) {
		for (UserFriend friend : friendUpdates) {
			if (friend.getUsername().equals(user.getUsername())) {
				friend.setOnline(loginStatus);
			}
		}
	}


	public Collection<UserFriend> getFriendUpdates() {
		Collection<UserFriend> copy = new ArrayList<UserFriend>(friendUpdates.size());
		for (UserFriend f : friendUpdates) {
			copy.add(f);
		}
		friendUpdates.clear();
		return copy;
	}
	
}
