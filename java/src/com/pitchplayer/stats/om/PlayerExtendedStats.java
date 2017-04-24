package com.pitchplayer.stats.om;

import com.pitchplayer.userprofiling.om.User;

public class PlayerExtendedStats {
	private Integer userId;
	private String username;
	private float gameAvg;
	private float jackAvg;
	private float jackStealAvg;
	private float jackLossAvg;
	private float UpAvg;
	
	public PlayerExtendedStats() {
		
	}
	
	public PlayerExtendedStats(int userId) {
        this.userId = userId;
    }
    public PlayerExtendedStats(int userId, String username, float gameAvg, float jackAvg, float jackStealAvg, float jackLossAvg, float upAvg) {
       this.userId = userId;
       this.username = username;
       this.gameAvg = gameAvg;
       this.jackAvg = jackAvg;
       this.jackStealAvg = jackStealAvg;
       this.jackLossAvg = jackLossAvg;
       this.UpAvg = upAvg;
    }

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public float getGameAvg() {
		return gameAvg;
	}

	public void setGameAvg(float gameAvg) {
		this.gameAvg = gameAvg;
	}

	public float getJackAvg() {
		return jackAvg;
	}

	public void setJackAvg(float jackAvg) {
		this.jackAvg = jackAvg;
	}

	public float getJackStealAvg() {
		return jackStealAvg;
	}

	public void setJackStealAvg(float jackStealAvg) {
		this.jackStealAvg = jackStealAvg;
	}

	public float getJackLossAvg() {
		return jackLossAvg;
	}

	public void setJackLossAvg(float jackLossAvg) {
		this.jackLossAvg = jackLossAvg;
	}

	public float getUpAvg() {
		return UpAvg;
	}

	public void setUpAvg(float upAvg) {
		UpAvg = upAvg;
	}

}
