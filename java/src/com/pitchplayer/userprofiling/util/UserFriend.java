package com.pitchplayer.userprofiling.util;

public class UserFriend {

	private String username;
	private boolean isNew;
	private boolean online;

	public UserFriend() {
		
	}
	
	public UserFriend(String username, boolean online) {
		this.username = username;
		this.online = online;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean getNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	public boolean getOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	
	public boolean getIsNew() {
		return getNew();
	}
	
}
