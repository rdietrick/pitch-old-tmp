package com.pitchplayer.chat;

public class NoSuchGroupException extends Exception {

	public NoSuchGroupException(String groupName) {
		super(groupName);
		this.groupName = groupName;
	}

	String groupName;

	/**
	 * Get the value of groupName.
	 * 
	 * @return Value of groupName.
	 */
	public String getGroupName() {
		return groupName;
	}

}