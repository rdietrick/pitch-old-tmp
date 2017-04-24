package com.pitchplayer.chat;

public class AlreadyJoinedException extends Exception {

	String groupName;

	/**
	 * Get the value of groupName.
	 * 
	 * @return Value of groupName.
	 */
	public String getGroupName() {
		return groupName;
	}

	public AlreadyJoinedException(String groupName) {
		super(groupName);
		this.groupName = groupName;
	}

}