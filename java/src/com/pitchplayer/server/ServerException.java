package com.pitchplayer.server;

public class ServerException extends Exception {

	public static enum StatusCode {
		NO_GAME,
		SESSION_TIMEOUT,
		SERVER_ERROR,
		ILLEGAL_OPERATION,
		UNKNOWN;
	}
	
	private StatusCode statusCode;
	
	public ServerException(StatusCode statusCode, String message, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;		
	}

	public ServerException(StatusCode statusCode, String message) {
		this(statusCode, message, null);
	}

	public StatusCode getStatusCode() {
		return statusCode;
	}

}
