package com.pitchplayer.server.game;

import com.pitchplayer.server.game.player.GamePlayer;

public class CheaterException extends GameException {

	private GamePlayer player;
	
	public CheaterException(GamePlayer player, String message) {
		super(message);
		this.player = player;
	}

	public GamePlayer getPlayer() {
		return player;
	}
	

}
