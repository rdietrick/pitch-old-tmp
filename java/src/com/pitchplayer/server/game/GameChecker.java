package com.pitchplayer.server.game;

import java.util.TimerTask;


/**
 * Periodically cleans up game list by killing inactive games.
 */
public class GameChecker extends TimerTask {

	private GameFactory gameFactory;

	public GameChecker(GameFactory factory) {
		super();
		this.gameFactory = factory;
	}

	public void run() {
		gameFactory.pruneGameList();
	}

}