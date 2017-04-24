package com.pitchplayer.common;

import java.util.Date;

public interface UserNotification extends Comparable {

	public UserNotificationType getNotificationType();
	
	public Date getTimestamp();
	
	
	
}
