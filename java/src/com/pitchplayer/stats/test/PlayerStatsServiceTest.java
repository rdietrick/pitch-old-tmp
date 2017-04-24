package com.pitchplayer.stats.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pitchplayer.common.ResultsPage;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.stats.PlayerStatService;
import com.pitchplayer.stats.om.PlayerExtendedStats;
import com.pitchplayer.stats.om.PlayerStat;
import com.pitchplayer.test.AbstractSpringTest;

public class PlayerStatsServiceTest extends AbstractSpringTest {

	private PlayerStatService statsService;

	static List<Integer> ids = new ArrayList<Integer>();
	static {
		ids.add(9206);
		ids.add(15);
		ids.add(1);
	}

	Logger log = Logger.getLogger(this.getClass().getName());
	
	private void log() {
		log.debug("*********************************************************");
	}
	
	public void testGetPlayerStatsForUserIds() {
		Map<Integer, PlayerStat> statMap = statsService.getPlayerStatsForUserIds(ids);
		log();
		for (PlayerStat stat : statMap.values()) {
			log.debug(stat.getUsername() + ": " + stat.getWins() + " - " + stat.getLosses());
		}
		log();
	}
	
	public void testGetPlayerStatString() {
		Map<GameType, PlayerStat> stats = statsService.getPlayerStatsForUsername("wiseguy");
		assertNotNull(stats);
		log();
		for (Map.Entry<GameType, PlayerStat> entry: stats.entrySet()) {
			log.debug("wiseguy has played " + entry.getValue().getGames() + " games of type " + entry.getKey());
		}
		log();
	}

	public void testGetPlayerStatInt() {
		Map<GameType, PlayerStat> stats = statsService.getPlayerStatsForUserId(15);
		assertNotNull(stats);
		log();
		for (Map.Entry<GameType, PlayerStat> entry: stats.entrySet()) {
			log.debug("wiseguy has played " + entry.getValue().getGames() + " games of type " + entry.getKey());
		}
		log();
	}

	public void testGetSinglesPlayerStat() {
		ResultsPage stats = statsService.getPlayerStats(GameType.SINGLES, 0, 20);
		assertNotNull(stats);
		assertNotNull(stats.getResults());
		assertFalse(stats.getResults().isEmpty());
		log();
		log.debug("there are " + stats.getResults().size() + " ranked singles users");
	}

	public void testGetSimPlayerStat() {
		ResultsPage stats = statsService.getPlayerStats(GameType.SIM_SINGLES, 0,20);
		assertNotNull(stats);
		assertNotNull(stats.getResults());
		assertFalse(stats.getResults().isEmpty());
		log();
		log.debug("there are " + stats.getResults().size() + " ranked sim users");
	}

	public void testGetPlayerStatPaging() {
		ResultsPage stats = statsService.getPlayerStats(GameType.SIM_SINGLES, 0,2);
		assertNotNull(stats);
		assertNotNull(stats.getResults());
		assertFalse(stats.getResults().isEmpty());
		assertEquals(stats.getResults().size(), 2);
	}

	
	public void testGetPlayerExtendedStatsString() {
		PlayerExtendedStats s = statsService.getExtendedStatsForUsername("dummy");
		assertNotNull(s);
		log();
		log.debug("dummy jack_avg = " + s.getJackAvg());
	}
	
	public void testGetPlayerExtendedStatsInt() {
		PlayerExtendedStats s = statsService.getExtendedStatsForUserId(1);
		assertNotNull(s);
		log.debug("dummy jack_avg = " + s.getJackAvg());		
	}

	public PlayerStatService getPlayerStatService() {
		return statsService;
	}

	public void setPlayerStatService(PlayerStatService PlayerStatService) {
		this.statsService = PlayerStatService;
	}

}
