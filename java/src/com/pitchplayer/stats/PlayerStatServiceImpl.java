package com.pitchplayer.stats;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pitchplayer.common.ResultsPage;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.stats.dao.PlayerStatDao;
import com.pitchplayer.stats.om.PlayerExtendedStats;
import com.pitchplayer.stats.om.PlayerStat;

public class PlayerStatServiceImpl implements PlayerStatService {

	private PlayerStatDao statDao;
	Logger log = Logger.getLogger(this.getClass().getName());
	
	public Map<GameType, PlayerStat> getPlayerStatsForUsername(String username) {
		return statDao.getPlayerStats(username);
	}

	public Map<GameType, PlayerStat> getPlayerStatsForUserId(int userId) {
		return statDao.getPlayerStats(userId);
	}

	public PlayerExtendedStats getExtendedStatsForUsername(String username) {
		return statDao.getExtendedStats(username);
	}

	public PlayerExtendedStats getExtendedStatsForUserId(int userId) {
		return statDao.getExtendedStats(userId);
	}

	public Map<Integer, PlayerExtendedStats> getExtendedStatsForUserIds(
			Collection<Integer> userIds) {
		return statDao.getExtendedStatsForUserIds(userIds);
	}

	public Map<String, PlayerExtendedStats> getExtendedStatsForUsernames(
			Collection<String> usernames) {
		return statDao.getExtendedStatsForUsernames(usernames);
	}

	public PlayerStatDao getPlayerStatsDao() {
		return statDao;
	}

	public void setPlayerStatsDao(PlayerStatDao playerStatsDao) {
		this.statDao = playerStatsDao;
	}


	public ResultsPage getPlayerStats(GameType gameType, int offset, int limit) {
		return statDao.getPlayerStats(gameType, offset, limit);
	}

	public Map<Integer, PlayerStat> getPlayerStatsForUserIds(
			Collection<Integer> userIds) {
		return statDao.getPlayerStatsForUserIds(userIds);
	}

	public Map<String, PlayerStat> getPlayerStatsForUsernames(
			Collection<String> usernames) {
		return statDao.getPlayerStatsForUsernames(usernames);		
	}

	public ResultsPage getPlayerStats(int offset, int limit) {
		return statDao.getPlayerStats(offset, limit);
	}

	public Integer getPlayerRankForUserId(Integer userId) {
		return statDao.getRankForUser(userId);
	}

	public Map<Integer, Integer> getPlayerRanksForUserIds(Collection<Integer> userIds) {
		return statDao.getRanksForUserIds(userIds);
	}

	public Integer getPlayerRankForUsername(String username) {
		return statDao.getRankForUser(username);
	}

	public Map<String, Integer> getPlayerRanksForUsernames(Collection<String> usernames) {
		return statDao.getRanksForUsernames(usernames);
	}

	public void deleteSimStats(GameType gameType) {
		statDao.deleteSimStats(gameType);
		
	}

	public ResultsPage getPlayersRankingsPage(Integer userId, int limit) {
		return statDao.getPlayersRankingsPage(userId, limit);
	}

}
