package com.pitchplayer.server.game.test;

import com.pitchplayer.server.game.GameSimulator;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.stats.GameSimulatorService;
import com.pitchplayer.test.AbstractSpringTest;

public class GameLifeCycleTest extends AbstractSpringTest {

	private GameSimulatorService gameSimulatorService;

	public void setGameSimulatorService(GameSimulatorService gameSimulatorService) {
		this.gameSimulatorService = gameSimulatorService;
	}

	public void testGameExecution() {
		GameSimulator sim = gameSimulatorService.simulateGames(GameType.SIM_SINGLES, 1);
		sim.run();
	}
	
}
