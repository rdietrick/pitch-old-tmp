package com.pitchplayer.userprofiling.action;

import java.util.List;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.stats.PlayerRankingsCache;
import com.pitchplayer.stats.PlayerStatService;
import com.pitchplayer.userprofiling.UserAssociationService;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.User;

public class UserSearchAction extends BaseAction {

	private UserService userService;
	// private UserAssociationService userAssociationService;
	private String username;
	private User user;
	private boolean friend;
	private List<User> searchResults;
	private PlayerRankingsCache playerRankingsCache;
	
	public String searchByUsername() {
		try {
			searchResults = userService.searchUsers(username);
		} catch (Exception e) {
			log.error("Error querying for user", e);
			addActionError("There was an error executing your query.  Please try again later.");
			return ERROR;
		}
		if (searchResults == null || searchResults.size() == 0) {
			addActionError("Your query did not match any users.");
			return ERROR;
		}
		return SUCCESS;
	}

	public String viewUserProfile() {
		try {
			user = userService.getUserByUsername(username);
		} catch (Exception e) {
			log.error("Error retreiving user", e);
			addActionError("System error.  Please try again later");
			return ERROR;
		}
		user.setCurrentRank(playerRankingsCache.getPlayerRank(username));
		return SUCCESS;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setUsername(String username) {
		log.debug("username  set to " + username);
		this.username = username;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUsername() {
		return username;
	}

	public List<User> getSearchResults() {
		return searchResults;
	}

	public boolean isFriend() {
		return friend;
	}

	public void setPlayerRankingsCache(PlayerRankingsCache playerRankingsCache) {
		this.playerRankingsCache = playerRankingsCache;
	}

//	public void setUserAssociationService(
//			UserAssociationService userAssociationService) {
//		this.userAssociationService = userAssociationService;
//	}
	
}
