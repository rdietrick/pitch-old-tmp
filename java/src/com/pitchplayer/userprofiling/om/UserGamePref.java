package com.pitchplayer.userprofiling.om;

// Generated Oct 10, 2008 4:02:44 PM by Hibernate Tools 3.2.1.GA

import java.util.Date;

import com.pitchplayer.server.game.GameType;

/**
 * UserGamePref generated by hbm2java
 */
public class UserGamePref implements java.io.Serializable {

	private int userId;
	private User User;
	private byte dfltGameType;
	private Integer dfltLowScoring;
	private Integer dfltChallengeSend;
	private Integer dfltChallengeShow;
	private Date dateUpdated;

	public static final int ALL = 2;
	public static final int FRIENDS = 1;
	public static final int NEVER = 0;
	
	public UserGamePref() {
	}

	public UserGamePref(int userId, User User, byte dfltGameType,
			Integer dfltLowScoring, Integer dfltChallengeSend, Integer dfltChallengeShow) {
		this.userId = userId;
		this.User = User;
		this.dfltGameType = dfltGameType;
		this.dfltLowScoring = dfltLowScoring;
		this.dfltChallengeSend = dfltChallengeSend;
		this.dfltChallengeShow = dfltChallengeShow;
	}

	public UserGamePref(int userId, User User, byte dfltGameType,
			Integer dfltLowScoring, Integer dfltChallengeSend, Integer dfltChallengeShow,
			Date dateUpdated) {
		this.userId = userId;
		this.User = User;
		this.dfltGameType = dfltGameType;
		this.dfltLowScoring = dfltLowScoring;
		this.dfltChallengeSend = dfltChallengeSend;
		this.dateUpdated = dateUpdated;
		this.dfltChallengeShow = dfltChallengeShow;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public User getUser() {
		return this.User;
	}

	public void setUser(User User) {
		this.User = User;
	}

	public byte getDfltGameType() {
		return this.dfltGameType;
	}

	public void setDfltGameType(byte dfltGameType) {
		this.dfltGameType = dfltGameType;
	}

	public Integer getDfltLowScoring() {
		return this.dfltLowScoring;
	}

	public void setDfltLowScoring(Integer dfltLowScoring) {
		this.dfltLowScoring = dfltLowScoring;
	}

	public Integer getDfltChallengeSend() {
		return this.dfltChallengeSend;
	}

	public void setDfltChallengeSend(Integer dfltChallengeSend) {
		this.dfltChallengeSend = dfltChallengeSend;
	}

	public Date getDateUpdated() {
		return this.dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public Integer getDfltChallengeShow() {
		return dfltChallengeShow;
	}

	public void setDfltChallengeShow(Integer dfltChallengeShow) {
		this.dfltChallengeShow = dfltChallengeShow;
	}

	public static UserGamePref getDefaultUserGamePref() {
		UserGamePref gp = new UserGamePref();
		gp.setDateUpdated(new Date());
		gp.setDfltChallengeSend(ALL);
		gp.setDfltChallengeShow(ALL);
		gp.setDfltLowScoring(1);
		gp.setDfltGameType(GameType.SINGLES.getDbFlag());
		return gp;
	}

}
