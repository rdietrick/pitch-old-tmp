package com.pitchplayer.userprofiling;

public enum UserStatus {

	ACTIVE((byte)1),
	DELETED((byte)2),
	BANNED((byte)3);
	
	private Byte numericStatus;

	public Byte getNumericStatus() {
		return numericStatus;
	}
	
	UserStatus(byte numericStatus) {
		this.numericStatus = numericStatus;
	}
	
}
