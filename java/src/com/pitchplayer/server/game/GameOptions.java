package com.pitchplayer.server.game;

import com.pitchplayer.server.game.GameType;

public class GameOptions {

	private boolean lowCapturable = false;
	private int maxPlayers = 4;
	private int minPlayers = 3;
	private GameType gameType;
	private boolean moneyGame = false;
	
	public GameOptions() {
		this(GameType.SINGLES, false);
	}
	
	public GameOptions(GameType gameType) {
		this(gameType, false);
	}
	
	public GameOptions(GameType gameType, boolean lowCapturable) {
		this.lowCapturable = lowCapturable;
		this.gameType = gameType;
		this.moneyGame = false;
		if (gameType == GameType.DOUBLES) {
			this.minPlayers = 4;
		}
		this.maxPlayers = 4;
	}
	
	public boolean isLowCapturable() {
		return lowCapturable;
	}
	public void setLowCapturable(boolean lowCapturable) {
		this.lowCapturable = lowCapturable;
	}
	public int getMaxPlayers() {
		return maxPlayers;
	}
	public void setMaxPlayers(int maxPlayers) throws IllegalArgumentException {
		if (gameType == GameType.DOUBLES && maxPlayers < 4) {
			throw new IllegalArgumentException("Doubles games must contain 4 players");
		}
		this.maxPlayers = maxPlayers;
	}
	public GameType getGameType() {
		return gameType;
	}
	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}
	public boolean isMoneyGame() {
		return moneyGame;
	}
	public void setMoneyGame(boolean moneyGame) {
		this.moneyGame = moneyGame;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public void setMinPlayers(int minPlayers) {
		if (gameType == GameType.DOUBLES && minPlayers < 4) {
			throw new IllegalArgumentException("Doubles games must contain 4 players");
		}
		this.minPlayers = minPlayers;
	}
	
	
}
