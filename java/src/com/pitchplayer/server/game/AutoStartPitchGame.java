package com.pitchplayer.server.game;

import com.pitchplayer.server.game.player.GamePlayer;
import com.pitchplayer.stats.om.GameRecord;

public class AutoStartPitchGame extends PitchGame {

	public AutoStartPitchGame(GameRecord gameRecord, GameFactory factory,
			GamePlayer creator, GameOptions gameOptions) {
		super(gameRecord, factory, creator, gameOptions);
	}

}