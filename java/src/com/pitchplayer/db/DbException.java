package com.pitchplayer.db;

/**
 * A Database-related exception.
 * @author robd
 *
 */
public class DbException extends Exception {

	public DbException() {
		super();
	}
	
	public DbException(String msg, Exception e) {
		super(msg, e);
	}

	public DbException(String string) {
		super(string);
	}
}
