package com.pitchplayer.db;

public class DuplicateRecordException extends DbException {

	public DuplicateRecordException(String message, Exception cause) {
		super(message, cause);
	}
	
	public DuplicateRecordException(String message) {
		super(message);
	}
	
}
