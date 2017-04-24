package com.pitchplayer.stats.action;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.common.ResultsPage;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.stats.PlayerStatService;

public class StatViewerAction extends BaseAction {
	
	private static final String SUCCESS_SINGLES = "success-singles";
	private static final String SUCCESS_DOUBLES = "success-doubles";
	private static final String SUCCESS_SIM = "success-sim";
	private int offset = 0;
	private int limit = 10;
	private ResultsPage results;
	private PlayerStatService playerStatService;
	private GameType gameType = GameType.SINGLES;
	
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		if (offset > 0) {
			this.offset = offset;
		}
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public ResultsPage getResults() {
		return results;
	}
	public void setResults(ResultsPage results) {
		this.results = results;
	}
	
	public String execute() {
		try {
			results = playerStatService.getPlayerStats(offset, limit);
			return SUCCESS;
		} catch (Throwable t) {
			log.error("Error querying for stats", t);
			addActionError("System error.  Please try again later.");
			return ERROR;
		}
	}
	
	
	public String executeGetMyRankingsPage() {
		try {
			results = playerStatService.getPlayersRankingsPage(getSessionUser().getUserId(), limit);
			return SUCCESS;
		} catch (Throwable t) {
			log.error("Error querying for stats", t);
			addActionError("System error.  Please try again later.");
			return ERROR;
		}
	}
	
	public PlayerStatService getPlayerStatService() {
		return playerStatService;
	}
	public void setPlayerStatService(PlayerStatService playerStatsService) {
		this.playerStatService = playerStatsService;
	}
	public GameType getGameType() {
		return gameType;
	}
	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}
	
}
