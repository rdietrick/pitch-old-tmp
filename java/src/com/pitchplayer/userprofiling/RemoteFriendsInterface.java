package com.pitchplayer.userprofiling;

import java.util.List;

import com.pitchplayer.userprofiling.om.UserAssociation;

public interface RemoteFriendsInterface {

	public boolean addFriend(String username);
	
	public String[] getFriends();
	
	public List<UserAssociation> getPendingFriendRequests();
	
	public boolean acceptFriendRequest(String username);
	
	public boolean ignoreFriendRequest(String username);
	
}
