package com.pitchplayer.userprofiling.om.test;

import com.pitchplayer.test.AbstractSpringTest;
import com.pitchplayer.userprofiling.dao.UserDao;
import com.pitchplayer.userprofiling.om.User;

public class Mysql5UserDaoTest extends AbstractSpringTest {

	private UserDao userDao;
	
	
	public void testGetUserByUsernameAndPassword() {
		String username = "grizzle";
		String password = "grizzle";
		User user = userDao.getUserByUsernameAndPassword(username, password);
		assertNotNull(user);
	}


	public UserDao getUserDao() {
		return userDao;
	}


	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
