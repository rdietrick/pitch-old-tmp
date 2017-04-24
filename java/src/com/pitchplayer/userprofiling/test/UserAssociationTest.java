package com.pitchplayer.userprofiling.test;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.pitchplayer.db.DbException;
import com.pitchplayer.test.AbstractSpringTest;
import com.pitchplayer.userprofiling.UserAssociationService;
import com.pitchplayer.userprofiling.UserAssociationType;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserAssociation;

public class UserAssociationTest extends AbstractSpringTest {

	public UserAssociationService userAssociationService;
	public UserService userService;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public void testAssociateUsers() throws DbException {
		User robd = userService.getUserByUsername("robd");
		log.debug("found user robd");
		User friend1 = userService.getUserByUsername("grizzle");
		log.debug("found user " + friend1.getUsername());
		userAssociationService.associateUsers(robd, friend1, UserAssociationType.FRIEND);
		log.debug("associated users");
		assertTrue(confirmAssoc(robd, friend1, UserAssociationType.FRIEND.getDbValue()) && 
				confirmAssoc(friend1, robd, UserAssociationType.FRIEND.getDbValue()));
	}

	private boolean confirmAssoc(User user, User userAssoc, int assocTypeFriend) {
		Set<UserAssociation> associates = user.getUserAssociations();
		for (UserAssociation assoc : associates) {
			if (assoc.getUserByAssociateId().getUsername().equals(userAssoc.getUsername())) {
				return true;
			}
		}
		return false;
	}

	public void testCreateAssociationRequest() {
	}

	public void testDisassociateUsers() {
		fail("Not yet implemented");
	}

	public void testGetFriends() {
		User user = userService.getUserByUsername("robd");
		List<UserAssociation> friends = userAssociationService.getUserAssociations(user, UserAssociationType.FRIEND, 
				UserAssociation.STATUS_CONFIRMED);
		log.debug("*****************************************");
		for (UserAssociation assoc : friends) {
			log.debug("found friend: " + assoc.getUserByAssociateId().getUsername());
		}
		log.debug("*****************************************");
	}
	
	
	public void testGetFriendsFromUser() {
		User user = userService.getUserByUsername("robd");
		for (User friend : user.getFriends()) {
			log.debug("friend: " + friend.getUsername());
		}
	}

	public void testConfirmAssociation() throws DbException {
		User robd = userService.getUserByUsername("robd");
		User friend1 = userService.getUserByUsername("rizbert");
		userAssociationService.createAssociationRequest(robd, friend1, UserAssociationType.FRIEND);
		userAssociationService.associateUsers(robd, friend1, UserAssociationType.FRIEND);
		
		List<UserAssociation> assocs = 
			userAssociationService.getUserAssociations(robd, UserAssociationType.FRIEND, UserAssociation.STATUS_CONFIRMED);
		assertTrue(assocs.size() == 1);
		assocs = 
			userAssociationService.getUserAssociations(friend1, UserAssociationType.FRIEND, UserAssociation.STATUS_CONFIRMED);
		assertTrue(assocs.size() == 1);
	}
	
	public UserAssociationService getUserAssociationService() {
		return userAssociationService;
	}

	public void setUserAssociationService(
			UserAssociationService userAssociationService) {
		this.userAssociationService = userAssociationService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
