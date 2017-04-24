package com.pitchplayer.userprofiling.test;

import java.util.Date;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.pitchplayer.test.AbstractSpringTest;
import com.pitchplayer.userprofiling.DuplicateUserException;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.CPUPlayerRecord;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserConstants;
import com.pitchplayer.userprofiling.om.UserPref;

public class UserServiceTest extends AbstractSpringTest {

	UserService userService;
	private Logger log = Logger.getLogger(this.getClass().getName());

	public void testGetUserByUsername() {
		User u = userService.getUserByUsername("robd");
		assertTrue(u != null);
	}
	
	
	public void testCreateUser() throws DuplicateUserException {
		User u = new User();
		u.setUsername("test_user");
		u.setPasswd("asdf1234");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1973);
		cal.set(Calendar.MONTH, Calendar.JULY);
		cal.set(Calendar.DATE, 27);
		u.setBirthDate(cal.getTime());
		u.setEmailAddress("robd@pitchplayer.com");
		u.setFirstName("Test");
		u.setLastName("User");
		u.setRegistrationDate(new Date());
		userService.createUser(u, false);
		// setComplete();
	}

	public void testSetUserPrefs() {
		User u = userService.getUserByUsername("robd");
		UserPref p = u.getUserPref();
		if (p == null) {
			p = new UserPref();
			p.setUser(u);
			u.setUserPref(p);
		}
		p.setDateUpdated(new Date());
		p.setShowCity(1);
		p.setShowName(1);
		p.setShowState(1);
		userService.update(u);
		setComplete();
	}
	
	public void testSearchUsers() {
		List<User> users = userService.searchUsers("r");
		log.debug("FOUND " + users.size() + " MATCHES");
		for (User u : users) {
			log.debug("Found user: " + u.getUsername());
		}
	}
	
	public void testGetCPUPlayers() {
		List<CPUPlayerRecord> cpus = userService.getAllCPUPlayers();
		assertTrue(cpus.size() > 0);
	}

	public void testGetPlayableCPUPlayers() {
		List<CPUPlayerRecord> cpus = userService.getAllPlayableCPUPlayers();
		for (CPUPlayerRecord p : cpus) {
			if (p.getStatus().equals(CPUPlayerRecord.Status.DISABLED)) {
				assertFalse(true);
			}
		}
		assertTrue(true);
	}
	
	public void testCreateCPUPlayer() throws Exception {
		CPUPlayerRecord p = new CPUPlayerRecord();
		p.setClassName("x");
		p.setPlayerType(UserConstants.USER_TYPE_CPU_SINGLES);
		userService.createCPUPlayer(p, "newdude");
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
}
