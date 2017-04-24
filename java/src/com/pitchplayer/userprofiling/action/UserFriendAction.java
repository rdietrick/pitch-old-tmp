package com.pitchplayer.userprofiling.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.stats.PlayerStatService;
import com.pitchplayer.stats.om.PlayerStat;
import com.pitchplayer.userprofiling.UserAssociationService;
import com.pitchplayer.userprofiling.UserAssociationType;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserAssociation;

public class UserFriendAction extends BaseAction {

	private UserAssociationService userAssociationService;
	private Collection<PlayerStat> friends;
	private List<UserAssociation> friendRequests;
	private PlayerStatService playerStatService;

	public String doGetUserFriends() {
		try {
			User user = getSessionUser();
			if (user == null) {
				addActionError("User not logged in.");
				return ERROR;
			}
			List<UserAssociation> assocs = userAssociationService.getUserAssociations(user, 
					UserAssociationType.FRIEND, UserAssociation.STATUS_CONFIRMED);
			log.debug(user.getUsername() + " has " + assocs.size() + " friends");
			List<Integer> friendIds = new ArrayList<Integer>(assocs.size() + 1);
			for (UserAssociation a : assocs) {
				friendIds.add(a.getUserByAssociateId().getUserId());
			}
			friendIds.add(user.getUserId());
			friends = playerStatService.getPlayerStatsForUserIds(friendIds).values();
			return SUCCESS;
		} catch (Throwable t) {
			log.error("Error getting friends/stats", t);
			return ERROR;
		}
	}

	public String doGetFriendRequests() {
		User user = getSessionUser();
		if (user == null) {
			addActionError("User not logged in.");
			return ERROR;
		}
		friendRequests = userAssociationService.getPendingAssociationRequests(user, UserAssociationType.FRIEND);
		return SUCCESS;
	}

	public void setUserAssociationService(
			UserAssociationService userAssociationService) {
		this.userAssociationService = userAssociationService;
	}

	
	public List<UserAssociation> getFriendRequests() {
		return friendRequests;
	}

	public void setPlayerStatService(PlayerStatService playerStatService) {
		this.playerStatService = playerStatService;
	}

	public Collection<PlayerStat> getFriends() {
		return friends;
	}
	
}
