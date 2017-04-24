package com.pitchplayer.common;

import java.util.Date;

public class AbstractUserNotification implements UserNotification {

	protected UserNotificationType nType;
	protected Date timestamp;
	
	public AbstractUserNotification(UserNotificationType nType, Date timestamp) {
		this.nType = nType;
		this.timestamp = timestamp;
	}
	
	public UserNotificationType getNotificationType() {
		return nType;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public int compareTo(Object obj) {
		UserNotification other = (UserNotification)obj;
		return (int)(other.getTimestamp().getTime() - this.getTimestamp().getTime());
	}
}
