package com.pitchplayer.servlet;

/**
 * NullParameterException
 * 
 * Thrown when a requested parameter from an HttpServletRequest was not passed
 * to the server or is equal to "".
 * 
 * @author Robert Dietrick
 */

public class NullParameterException extends Exception {

	// name of the missing parameter
	private String paramName;

	/**
	 * Constructor takes the name of the missing parameter.
	 */
	public NullParameterException(String paramName) {
		this.paramName = paramName;
	}

	/**
	 * Get the name of the requested parameter.
	 */
	public String getParameterName() {
		return this.paramName;
	}

}