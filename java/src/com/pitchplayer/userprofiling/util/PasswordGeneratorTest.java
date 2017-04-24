package com.pitchplayer.userprofiling.util;

import junit.framework.TestCase;

public class PasswordGeneratorTest extends TestCase {

	public void testGenerateRandomPassword() {
		String passwd = PasswordGenerator.generateRandomPassword();
		System.out.println(passwd);
		assertNotNull(passwd);
	}

}
