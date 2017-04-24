package com.pitchplayer.server.game;

import java.util.List;

import com.pitchplayer.server.game.GameStatus;

public class GameInfo {

	private int gameId;
	private GameOptions gameOptions;
	private GameStatus status;
	private List<String> playerNames;
	
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public GameStatus getStatus() {
		return status;
	}
	public void setStatus(GameStatus status) {
		this.status = status;
	}
	public List<String> getPlayerNames() {
		return playerNames;
	}
	public void setPlayerNames(List<String> playerNames) {
		this.playerNames = playerNames;
	}
	public GameOptions getGameOptions() {
		return gameOptions;
	}
	public void setGameOptions(GameOptions gameOptions) {
		this.gameOptions = gameOptions;
	}
	
	
	
}
