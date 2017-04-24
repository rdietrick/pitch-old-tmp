package com.pitchplayer.userprofiling;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;

import com.pitchplayer.db.DbException;
import com.pitchplayer.db.DuplicateRecordException;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserAssociation;
import com.pitchplayer.util.DWRProxy;

public class DWRRemoteFriendsInterfaceImpl extends DWRProxy implements RemoteFriendsInterface {

	private UserAssociationService userAssociationService;
	private UserService userService;
	
	public boolean addFriend(String friendName) {
		User friend = userService.getUserByUsername(friendName);
		if (friend != null) {
			try {
				userAssociationService.createAssociationRequest(getSessionUser(), friend, UserAssociationType.FRIEND);
				return true;
			} catch (DbException dbe) {
				if (DuplicateRecordException.class.isAssignableFrom(dbe.getClass())) {
					log.debug("friend request already exists");
					return true;
				}
				log.error("Unexpected error adding friend", dbe);
				return false;
			}
		}
		return false;
	}


	public boolean acceptFriendRequest(String username) {
		User friend = userService.getUserByUsername(username);
		if (friend != null) {
			try {
				userAssociationService.associateUsers(getSessionUser(), friend, UserAssociationType.FRIEND);
				return true;
			} catch (DataAccessException dae) {
				log.error("Error associating users", dae);
				return false;
			} catch (DbException e) {
				log.error("Error associating users", e);
				return false;
			}
		}
		else {
			log.error("no user with username " + username);
		}
		return false;
	}

	public String[] getFriends() {
		List<UserAssociation> assocs = 
			userAssociationService.getUserAssociations(getSessionUser(), UserAssociationType.FRIEND, UserAssociation.STATUS_CONFIRMED);
		String[] friendNames = new String[assocs.size()];
		int i=0;
		for (UserAssociation a : assocs) {
			friendNames[i++] = a.getUserByAssociateId().getUsername();
		}
		return friendNames;
	}

	public List<UserAssociation> getPendingFriendRequests() {
		return userAssociationService.getPendingAssociationRequests(getSessionUser(), UserAssociationType.FRIEND);
	}

	public boolean ignoreFriendRequest(String username) {
		// TODO not implemented yet
		return false;
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
