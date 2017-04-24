package com.pitchplayer.common.test;

import java.util.Date;
import java.util.TreeSet;

import com.pitchplayer.common.AbstractUserNotification;
import com.pitchplayer.common.UserNotification;
import com.pitchplayer.common.UserNotificationType;

import junit.framework.TestCase;

public class NotificationTest extends TestCase {

	public void testNotificationSort() {
		TreeSet<UserNotification> set = new TreeSet<UserNotification>();
		UserNotification n1 = new TestNotification();
		UserNotification n2 = new TestNotification();
		UserNotification n3 = new TestNotification();
		UserNotification n4 = new TestNotification();
		UserNotification n5 = new TestNotification();
		set.add(n3);
		set.add(n5);
		set.add(n1);
		set.add(n2);
		set.add(n4);
		Date last = null;
		for (UserNotification n : set) {
			if (last == null) {
				last = n.getTimestamp();
			}
			else {
				assertTrue(n.getTimestamp().getTime() > last.getTime());
				last = n.getTimestamp();
			}
		}
	}
	
	private class TestNotification extends AbstractUserNotification {

		public TestNotification() {
			super(UserNotificationType.TEST, new Date());
		}
		
	}
	
}
