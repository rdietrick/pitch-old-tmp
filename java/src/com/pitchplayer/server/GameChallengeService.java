package com.pitchplayer.server;


public interface GameChallengeService extends Runnable {

	public void sendChallenge(Challenge challenge);
		
	public void addChallengeListener(ChallengeListener listener);
	
	public void removeChallengeListener(ChallengeListener listener);
	
	public void stop();
	
}
