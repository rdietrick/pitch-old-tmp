package com.pitchplayer.server;


public interface ChallengeListener {

	/**
	 * Receive a challenge from another user.
	 * @param challenge
	 */
	public void receiveChallenge(Challenge challenge);
	
	/**
	 * Revoke a challenge
	 * @param c
	 */
	public void revokeChallenge(Challenge c);
	
}
