package com.pitchplayer.server;

import com.pitchplayer.userprofiling.om.User;

/**
 * Interface for filtering non-applicable challenges for users.
 * @author robd
 *
 */
public interface ChallengeFilter {

	/**
	 * Filter out irrelevant challenges
	 * @param challenge
	 * @return true if the challenge should be shown to the user.
	 */
	public boolean filterChallenge(Challenge challenge);
	
}
