package com.pitchplayer.server;

import com.pitchplayer.userprofiling.om.User;

public interface ChallengeInitiator {
	
	public User getUser();
	
	public void challengeAccepted(String playerName);
	
	public void challengeDeclined(String playerName);
	
}
