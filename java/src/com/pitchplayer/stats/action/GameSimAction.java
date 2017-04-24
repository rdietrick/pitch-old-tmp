package com.pitchplayer.stats.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.server.game.GameSimulator;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.stats.GameSimulatorService;
import com.pitchplayer.stats.PlayerStatService;

public class GameSimAction extends BaseAction {

	public static final int DFLT_MAX_SIMS = 2000;
	private int numGames;
	private String gameType;
	private int maxSims = DFLT_MAX_SIMS;
	private GameSimulatorService gameSimulatorService;
	private PlayerStatService playerStatService;
	
	public String simulateGames() {
		try {
			runSims();
			return SUCCESS;
		} catch (Exception e) {
			addActionError(e.getMessage());
			return ERROR;
		}
	}
	
	public String deleteSimStats() {
		log.debug("deleteSimStats invoked");
		try {
			if (gameType.equals(GameType.SIM_SINGLES.toString())) {
				playerStatService.deleteSimStats(GameType.SIM_SINGLES);
			}
			else if (gameType.equals(GameType.SIM_DOUBLES.toString())) {
				playerStatService.deleteSimStats(GameType.SIM_DOUBLES);
			}
			else {
				addActionError("Unrecognized gameType: " + gameType);
				return ERROR;
			}
			log.debug("success");
			return SUCCESS;
		} catch (Exception e) {
			addActionError("Error deleting stats: " + e.getMessage());
			return ERROR;
		}
	}
	
	/**
	 * Simulate pitch games.
	 * @return whether or not the simulation was created OK
	 */
	protected void runSims() throws Exception {
		if (numGames > maxSims) {
			numGames = maxSims;
		}
		log.debug("simulating " + numGames + " " + gameType + " games");
		GameType gt = null;
		if (gameType.equals(GameType.SIM_SINGLES.toString())) {
			gt = GameType.SIM_SINGLES;
		}
		else if (gameType.equals(GameType.SIM_DOUBLES.toString())) {
			gt = GameType.SIM_DOUBLES;
		}
		else {
			throw new Exception("Unrecognized gameType field '" + gameType + "'");
		}
		GameSimulator gameSim = gameSimulatorService.simulateGames(gt, numGames);
		storeSim(gameSim);
		Thread t = new Thread(gameSim);
		t.start();
	}

	/**
	 * Stores a reference (hash) to the sim in the user's session and in the
	 * member hashtable.
	 */
	private synchronized void storeSim(GameSimulator sim) {
		String hash = String.valueOf(System.currentTimeMillis());
		HashMap<String, GameSimulator> simulations = new HashMap<String, GameSimulator>();
		simulations.put(hash, sim);
		List<GameSimulator> userSims = null;
		if (getSession().get("sims") != null) {
			userSims = (ArrayList<GameSimulator>) getSession().get("sims");
		} else {
			userSims = new ArrayList<GameSimulator>();
		}
		userSims.add(sim);
		getSession().put("sims", userSims);
	}
	
	public int getNumGames() {
		return numGames;
	}


	public void setNumGames(int numGames) {
		this.numGames = numGames;
	}


	public int getMaxSims() {
		return maxSims;
	}


	public void setMaxSims(int maxSims) {
		this.maxSims = maxSims;
	}


	public GameSimulatorService getGameSimulatorService() {
		return gameSimulatorService;
	}


	public void setGameSimulatorService(GameSimulatorService gameSimulatorService) {
		this.gameSimulatorService = gameSimulatorService;
	}


	public String getGameType() {
		return gameType;
	}


	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

	public void setPlayerStatService(PlayerStatService playerStatService) {
		this.playerStatService = playerStatService;
	}

}
