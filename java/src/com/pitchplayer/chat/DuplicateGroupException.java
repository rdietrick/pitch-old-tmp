package com.pitchplayer.chat;

public class DuplicateGroupException extends Exception {

	String groupName;

	/**
	 * Get the value of groupName.
	 * 
	 * @return Value of groupName.
	 */
	public String getGroupName() {
		return groupName;
	}

	public DuplicateGroupException(String groupName) {
		super(groupName);
		this.groupName = groupName;
	}

}