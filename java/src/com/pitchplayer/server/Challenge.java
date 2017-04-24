package com.pitchplayer.server;

import java.util.Date;

import com.pitchplayer.common.AbstractUserNotification;
import com.pitchplayer.common.UserNotificationType;
import com.pitchplayer.server.game.GameOptions;
import com.pitchplayer.userprofiling.om.User;

public class Challenge extends AbstractUserNotification {

	private int gameId;
	private User challenger;
	private GameOptions gameOptions;
	private ChallengeType challengeType;
	public static enum ChallengeType { ALL, FRIENDS, REMATCH };
	private boolean expired = false;
	
	
	public Challenge(ChallengeType type, int gameId, User challenger, GameOptions gameOptions) {
		super(UserNotificationType.CHALLENGE, new Date());
		this.challengeType = type;
		this.gameId = gameId;
		this.challenger = challenger;
		this.gameOptions = gameOptions;
	}
	
	public int getGameId() {
		return gameId;
	}
	public User getChallenger() {
		return challenger;
	}
	public GameOptions getGameOptions() {
		return gameOptions;
	}

	public ChallengeType getType() {
		return challengeType;
	}
	
	public String getChallengerName() {
		return challenger.getUsername();
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Challenge)) {
			return false;
		}
		Challenge c = (Challenge)o;
		if (c.getGameId() == this.gameId) {
			return true;
		}
		return false;
	}

	public void expire() {
		this.expired = true;
	}
	
	public boolean getExpired() {
		return this.expired || this.challenger.getSessionId() == null;
	}
}
