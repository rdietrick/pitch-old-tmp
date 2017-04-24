package com.pitchplayer.userprofiling.om;

public enum PrivacyOption {

	PRIVATE(0),
	VIS_TO_FRIENDS(1),
	PUBLIC(2);
	
	private final int dbVal;
	
	PrivacyOption(int dbVal) {
		this.dbVal = dbVal;
	}
	
	public int dbVal() {
		return this.dbVal;
	}
	
}
