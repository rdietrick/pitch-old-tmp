package com.pitchplayer.userprofiling.action;

import com.pitchplayer.server.GameChallengeService;
import com.pitchplayer.userprofiling.UserStore;
import com.pitchplayer.userprofiling.util.FriendsListManager;

public class UserSessionPreparer {

	private GameChallengeService gameChallengeService;
	private FriendsListManager friendsListManager;
	private UserStore userStore;
	
	public GameChallengeService getGameChallengeService() {
		return gameChallengeService;
	}
	public void setGameChallengeService(GameChallengeService gameChallengeService) {
		this.gameChallengeService = gameChallengeService;
	}
	public FriendsListManager getFriendsListManager() {
		return friendsListManager;
	}
	public void setFriendsListManager(FriendsListManager friendsListManager) {
		this.friendsListManager = friendsListManager;
	}
	public UserStore getUserStore() {
		return userStore;
	}
	public void setUserStore(UserStore userStore) {
		this.userStore = userStore;
	}
	
}
