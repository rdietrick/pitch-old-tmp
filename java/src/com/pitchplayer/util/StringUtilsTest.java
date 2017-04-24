package com.pitchplayer.util;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

	public void testIsBlank() {
		String[] blanks = new String[3];
		blanks[0] = "";
		blanks[1] = " ";
		blanks[2] = "\t";
		for (int i=0;i<blanks.length;i++) {
			assertTrue("string '" + blanks[i] + "' failed", StringUtils.isBlank(blanks[i]));
		}
		assertTrue(true);
	}
	
	public void testNotBlank() {
		assertFalse(StringUtils.isBlank("a"));
	}
}
