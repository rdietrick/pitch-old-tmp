package com.pitchplayer.stats.test;

import java.util.List;

import com.pitchplayer.server.game.GameType;
import com.pitchplayer.stats.GameSimulatorService;
import com.pitchplayer.stats.PlayerStatService;
import com.pitchplayer.stats.om.PlayerStat;
import com.pitchplayer.test.AbstractSpringTest;

import junit.framework.TestCase;

public class GameSimulatorServiceTest extends AbstractSpringTest {

	private GameSimulatorService gameSimulatorService;
	private PlayerStatService playerStatService;

	
	public void testSimulateGames() {
		this.setComplete();

		gameSimulatorService.simulateGames(GameType.SIM_SINGLES, 5);
		List<PlayerStat> stats = playerStatService.getPlayerStats(GameType.SIM_SINGLES, 0, -1).getResults();
		this.assertTrue(stats.size() > 0);
		
	}

	public GameSimulatorService getGameSimulatorService() {
		return gameSimulatorService;
	}

	public void setGameSimulatorService(GameSimulatorService gameSimulatorService) {
		this.gameSimulatorService = gameSimulatorService;
	}

	public PlayerStatService getPlayerStatService() {
		return playerStatService;
	}

	public void setPlayerStatService(PlayerStatService playerStatService) {
		this.playerStatService = playerStatService;
	}

}
