package com.pitchplayer.stats;

import java.util.Collection;
import java.util.Map;

import com.pitchplayer.common.ResultsPage;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.stats.om.PlayerExtendedStats;
import com.pitchplayer.stats.om.PlayerStat;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
public interface PlayerStatService {

	public Map<GameType, PlayerStat> getPlayerStatsForUsername(String username);
	
	public Map<GameType, PlayerStat> getPlayerStatsForUserId(int userId);
	
	public PlayerExtendedStats getExtendedStatsForUsername(String username);

	public PlayerExtendedStats getExtendedStatsForUserId(int userId);
	
	public Map<Integer, PlayerStat> getPlayerStatsForUserIds(Collection<Integer> userIds);
	
	public Map<String, PlayerStat> getPlayerStatsForUsernames(Collection<String> usernames);

	public Map<Integer, PlayerExtendedStats> getExtendedStatsForUserIds(Collection<Integer> userIds);
	
	public Map<String, PlayerExtendedStats> getExtendedStatsForUsernames(Collection<String> usernames);

	public Integer getPlayerRankForUsername(String username);
	public Integer getPlayerRankForUserId(Integer userId);
	public Map<String, Integer> getPlayerRanksForUsernames(Collection<String> usernames);
	public Map<Integer, Integer>getPlayerRanksForUserIds(Collection<Integer> userIds);
	

	/**
	 * Get a page of player rankings ordered by overall winning percentage.
	 * @param gameType
	 * @param offset
	 * @param limit
	 * @return
	 */
	public ResultsPage getPlayerStats(GameType gameType, int offset, int limit);
	
	/**
	 * Get a page of overall player rankings ordered by overall winning percentage.
	 * @param gameType
	 * @param offset
	 * @param limit
	 * @return
	 */
	public ResultsPage getPlayerStats(int offset, int limit);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void deleteSimStats(GameType gameType);

	/**
	 * Get a page of results with the players' position in the middle of the page.
	 * @param limit
	 * @return
	 */
	public ResultsPage getPlayersRankingsPage(Integer userId, int limit);
	
}
