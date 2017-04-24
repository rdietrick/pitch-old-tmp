package com.pitchplayer.stats;

import com.pitchplayer.server.game.GameFactory;
import com.pitchplayer.server.game.GameSimulator;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.server.game.player.CPUPlayerFactory;

public class GameSimulatorServiceImpl implements GameSimulatorService {

	private CPUPlayerFactory playerFactory;
	private GameFactory gameFactory;

	public void setPlayerFactory(CPUPlayerFactory f) {
		this.playerFactory = f;
	}

	public void setGameFactory(GameFactory gf) {
		this.gameFactory = gf;
	}

	public GameSimulator simulateGames(GameType gameType, int numGames) {
		GameSimulator sim = new GameSimulator(gameType, numGames);
		sim.setCPUPlayerFactory(playerFactory);
		sim.setGameFactory(gameFactory);
		return sim;
	}

}
