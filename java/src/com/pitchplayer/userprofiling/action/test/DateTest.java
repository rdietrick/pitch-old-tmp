package com.pitchplayer.userprofiling.action.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

public class DateTest extends TestCase {

	public void testBefore() {
		Calendar cal = Calendar.getInstance();
		cal.roll(Calendar.YEAR, -16);
		Date minAge = cal.getTime();
		cal.set(Calendar.YEAR, 2000);
		cal.set(Calendar.MONTH, 7);
		cal.set(Calendar.DATE, 27);
		Date dob = cal.getTime();
		assertTrue(minAge.before(dob));
	}
	public void testAfter() {
		Calendar cal = Calendar.getInstance();
		cal.roll(Calendar.YEAR, -16);
		Date minAge = cal.getTime();
		cal.set(Calendar.YEAR, 1972);
		cal.set(Calendar.MONTH, 7);
		cal.set(Calendar.DATE, 27);
		Date dob = cal.getTime();
		assertTrue(minAge.after(dob));
	}
}
