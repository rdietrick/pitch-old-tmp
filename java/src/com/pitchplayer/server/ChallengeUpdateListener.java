package com.pitchplayer.server;

/**
 * Interface for receiving updates about Challenges.
 * @author robd
 *
 */
public interface ChallengeUpdateListener {

	public void userAccepted(String username);
	
	public void userDeclined(String username);
	
}
