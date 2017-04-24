package com.pitchplayer.stats;

import com.pitchplayer.server.game.GameSimulator;
import com.pitchplayer.server.game.GameType;

public interface GameSimulatorService {

	/**
	 * Simulate some number of Pitch games between computer players.
	 * @param gt type of game to simulate
	 * @param numGames number of games to simulate
	 * @return
	 */
	public GameSimulator simulateGames(GameType gameType, int numGames);

	
}
