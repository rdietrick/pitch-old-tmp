package com.pitchplayer.server;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.pitchplayer.common.UserNotification;

public class UserNotificationServiceImpl implements UserNotificationService {

	private ArrayList<UserNotificationListener> listeners = new ArrayList<UserNotificationListener>();
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public void addNotificationListener(UserNotificationListener listener) {
		listeners.add(listener);
		log.debug("added listener of class " + listener.getClass().getName());
	}

	public void removeNotificationListener(UserNotificationListener listener) {
		listeners.remove(listener);
	}

	public void sendNotification(UserNotification notification) {
		log.debug("received notification of type " + notification.getNotificationType() + "; sending to " + listeners.size() + " listeners");
		for (UserNotificationListener listener : listeners) {
			listener.notifyUser(notification);
		}

	}

}
