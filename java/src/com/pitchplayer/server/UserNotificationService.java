package com.pitchplayer.server;

import com.pitchplayer.common.UserNotification;

public interface UserNotificationService {

	public void sendNotification(UserNotification notification);
	
	public void addNotificationListener(UserNotificationListener listener);
	
	public void removeNotificationListener(UserNotificationListener listener);
	
}
