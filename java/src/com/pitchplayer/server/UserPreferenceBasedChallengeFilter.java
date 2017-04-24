package com.pitchplayer.server;

import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserGamePref;

public class UserPreferenceBasedChallengeFilter implements ChallengeFilter {

	private User user;

	public UserPreferenceBasedChallengeFilter(User user) {
		this.user = user;
	}
	
	public boolean filterChallenge(Challenge challenge) {
		if (user == null || challenge == null || challenge.getExpired()
				|| !challenge.getChallenger().getLoggedIn()
				|| challenge.getChallengerName().equals(user.getUsername())) {
			return false;
		}
		UserGamePref prefs = user.getUserGamePref();
		if (prefs.getDfltChallengeShow() == UserGamePref.NEVER) {
			return false;
		}
		else if (prefs.getDfltChallengeShow() == UserGamePref.FRIENDS) {
			for (User friend : user.getFriends()) {
				if (friend.getUsername().equals(challenge.getChallengerName())) {
					return true;
				}
			}
		}
		return true;
	}

}
