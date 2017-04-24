package com.pitchplayer.userprofiling.om.test;

import com.pitchplayer.db.DbException;
import com.pitchplayer.userprofiling.om.UserManager;

import junit.framework.TestCase;

public class UserManagerTest extends TestCase {

	public void testCreateUser() {
		fail("Not yet implemented");
	}

	public void testAuthenticateUser() {
		String username = "grizzle";
		String password = "grizzle";
		try {
			this.assertNotNull(new UserManager().authenticateUser(username, password));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.fail();
		}
	}



}
