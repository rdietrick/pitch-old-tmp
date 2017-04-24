package com.pitchplayer.userprofiling;

public enum UserAssociationType {

	FRIEND((byte)1);
	
	private byte dbValue;
	
	UserAssociationType(byte n) {
		this.dbValue = n;
	}
	
	public byte getDbValue() {
		return this.dbValue;
	}
	
}
