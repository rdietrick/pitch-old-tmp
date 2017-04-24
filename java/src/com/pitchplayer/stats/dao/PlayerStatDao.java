package com.pitchplayer.stats.dao;

import java.util.Collection;
import java.util.Map;

import com.pitchplayer.common.ResultsPage;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.stats.om.PlayerExtendedStats;
import com.pitchplayer.stats.om.PlayerStat;

public interface PlayerStatDao {

	public Map<GameType, PlayerStat> getPlayerStats(Integer userId);
	
	public Map<GameType, PlayerStat> getPlayerStats(String username);
	
	/**
	 * Get a page of player rankings ordered by winning percentage for a particular type of game
	 * (singles, doubled, etc.)
	 * @param gameType
	 * @param offset
	 * @param limit
	 * @return
	 */
	public ResultsPage getPlayerStats(GameType gameType, int offset, int limit);

	/**
	 * Get a page of player rankings ordered by overall winning percentage.
	 * @param offset
	 * @param limit
	 * @return
	 */
	public ResultsPage getPlayerStats(int offset, int limit);


	public PlayerExtendedStats getExtendedStats(String username);
	
	public PlayerExtendedStats getExtendedStats(Integer userId);
	
	public Map<String, PlayerExtendedStats> getExtendedStatsForUsernames(
			Collection<String> usernames);

	public Map<Integer, PlayerExtendedStats> getExtendedStatsForUserIds(
			Collection<Integer> userIds);

	public Map<Integer, PlayerStat> getPlayerStatsForUserIds(
			Collection<Integer> userIds);

	public Map<String, PlayerStat> getPlayerStatsForUsernames(
			Collection<String> usernames);

	public Integer getRankForUser(Integer userId);

	public Map<String, Integer> getRanksForUsernames(Collection<String> usernames);

	public Integer getRankForUser(String username);

	public Map<Integer, Integer> getRanksForUserIds(Collection<Integer> userIds);

	public void deleteSimStats(GameType gameType);

	/**
	 * Get the page of results with the specified plaer in the middle.
	 * @param userId
	 * @param limit
	 * @return
	 */
	public ResultsPage getPlayersRankingsPage(Integer userId, int limit);

}
