package com.pitchplayer.server;

import com.pitchplayer.common.UserNotification;

public interface UserNotificationListener {

	public void notifyUser(UserNotification notification);
	
}
