package com.pitchplayer.server.game;

import java.util.Date;

import org.apache.log4j.Logger;

import com.pitchplayer.server.ServerException;
import com.pitchplayer.server.game.player.CPUPlayer;
import com.pitchplayer.server.game.player.CPUPlayerFactory;

public class GameSimulator implements Runnable {

	private int numGames;
	private GameType gameType;
	
	private int crntGame = 0;

	private Date startTime = null;

	private Date endTime = null;

	private boolean finished = false;
	
	private GameFactory gameFactory;
	private CPUPlayerFactory cpuPlayerFactory;
	private Logger log = Logger.getLogger(this.getClass().getName());

	public GameSimulator(GameType gameType, int numGames) {
		this.gameType = gameType;
		this.numGames = numGames;
	}

	public GameFactory getGameFactory() {
		return gameFactory;
	}

	public void setGameFactory(GameFactory gameFactory) {
		this.gameFactory = gameFactory;
	}

	public CPUPlayerFactory getCPUPlayerFactory() {
		return cpuPlayerFactory;
	}

	public void setCPUPlayerFactory(CPUPlayerFactory cpuPlayerFactory) {
		this.cpuPlayerFactory = cpuPlayerFactory;
	}

	public int getNumGames() {
		return this.numGames;
	}

	public int getCurrentGame() {
		return crntGame + 1;
	}

	public boolean getFinished() {
		return this.finished;
	}

	public long getTimeElapsed() {
		if (startTime == null) {
			return 0l;
		}
		long time;
		if (endTime == null)
			time = new Date().getTime();
		else
			time = endTime.getTime();
		return time - startTime.getTime();
	}

	public float getAvgSecondsPerGame() {
		if (startTime == null) {
			return 0f;
		}
		return (getTimeElapsed() / 1000f) / getCurrentGame();
	}

	public float getAvgGamesPerSecond() {
		if (startTime == null) {
			return 0f;
		}
		return getCurrentGame() / (getTimeElapsed() / 1000f);
	}

	public void run() {
		log.debug("Beginning Simulation (" + numGames + " games)...");
		startTime = new Date();
		try {
			for (int i = 0; i < numGames; i++) {
				crntGame = i;
				runSimGame();
			}
		} catch (ServerException sqle) {
			log.debug("Error creating new game" + sqle);
		}
		endTime = new Date();
		finished = true;
	}

	
	private PitchGame runSimGame() throws ServerException {
		GameOptions opts = new GameOptions(gameType, false);
		switch (opts.getGameType()) {
		case SIM_SINGLES:
			opts.setMinPlayers(3);
			opts.setMaxPlayers(3);
			break;
		case SIM_DOUBLES:
			opts.setMinPlayers(4);
			opts.setMaxPlayers(4);
			break;
		default:
			throw new IllegalArgumentException("Game type " + opts.getGameType() + " is not a valid simulation type");
		}
		CPUPlayer creator = cpuPlayerFactory.getRandomCPUPlayer(gameType);
		PitchGame game = gameFactory.createGame(creator, opts);
		for (int i=1;i<opts.getMaxPlayers();i++) {
			CPUPlayer player = null;
			player = cpuPlayerFactory.getRandomCPUPlayer(gameType, game.getComputerPlayerIds());
			game.addPlayer(player);
		}
		return game;
	}
	
}