package com.pitchplayer.server;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.pitchplayer.server.game.GameOptions;

/**
 * A Challenge targeted to specific users.
 * @author robd
 *
 */
public class TargetedChallenge extends Challenge {

	public static enum PlayerStatus {
		ACCEPTED, DECLINED, WAITING;
	}

	private ChallengeInitiator challenger;
	private final HashMap<String, PlayerStatus> targets;
	Logger log = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Construct a new TargetedChallenge with a collection of target users.
	 * If the targetUsernames list contains the name of the challenger, the
	 * challenger will NOT be added to the list of targets 
	 * @param type
	 * @param gameId
	 * @param challenger
	 * @param gameOptions
	 * @param humanPlayers
	 */
	public TargetedChallenge(ChallengeType type, int gameId, ChallengeInitiator challenger,
			GameOptions gameOptions, List<String> humanPlayers) {
		super(type, gameId, challenger.getUser(), gameOptions);
		this.challenger = challenger;
		if (humanPlayers != null && humanPlayers.size() > 0) {
			this.targets = new HashMap<String, PlayerStatus>(humanPlayers.size()-1);
			for (String player : humanPlayers) {
				if (!player.equals(challenger.getUser().getUsername())) {
					this.targets.put(player, PlayerStatus.WAITING);
					log.debug("user " + player + " targeted");
				}
			}
		}
		else {
			this.targets = new HashMap<String, PlayerStatus>(0);
		}
	}

	/**
	 * Find out this challenge was targeted to a particular user.
	 * @param username the username of the user to check for targeting
	 * @return
	 */
	public boolean isUserTargeted(String username) {
		for (String p : targets.keySet()) {
			if (p.equals(username)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the list of users targeted by this challenge
	 * @return
	 */
	public String[] getTargetedUsernames() {
		return targets.keySet().toArray(new String[] {});
	}
	

	/**
	 * Update the challenge to indicate that a player has declined.
	 * @param player
	 */
	public void decline(String playerName) {
		log.debug("decline() called");
		targets.put(playerName, PlayerStatus.DECLINED);
		challenger.challengeDeclined(playerName);
	}
	
	public void accept(String playerName) {
		targets.put(playerName, PlayerStatus.ACCEPTED);
		challenger.challengeAccepted(playerName);
	}
	
}
