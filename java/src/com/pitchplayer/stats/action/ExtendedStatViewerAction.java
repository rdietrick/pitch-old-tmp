package com.pitchplayer.stats.action;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.stats.PlayerStatService;
import com.pitchplayer.stats.om.PlayerExtendedStats;

public class ExtendedStatViewerAction extends BaseAction {

	private PlayerStatService playerStatService;
	private PlayerExtendedStats stats;
	private String username;
	
	public PlayerStatService getPlayerStatService() {
		return playerStatService;
	}
	public void setPlayerStatService(PlayerStatService playerStatsService) {
		this.playerStatService = playerStatsService;
	}

	/**
	 * Default functionality same as execute().
	 */
	public String doDefault() {
		return execute();
	}
	
	/**
	 * Handle normal execution.
	 */
	public String execute() {
		stats = playerStatService.getExtendedStatsForUsername(username);
		return SUCCESS;
	}
	

	public String viewExtendedStats() {
		execute();
		return SUCCESS;
	}
	public PlayerExtendedStats getStats() {
		return stats;
	}
	public void setStats(PlayerExtendedStats stats) {
		this.stats = stats;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
