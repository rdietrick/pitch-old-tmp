package com.pitchplayer.server.game.player;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.Trick;
import com.pitchplayer.userprofiling.om.User;

public class StrategicPlayer extends SmartBiddingCardCountingCPUPlayer {

	private int pointsScored = 0;
	private int pointsBid = 0;
	
	public StrategicPlayer(User user) {
		super(user);
	}

	@Override
	protected Card play() {
		Trick trick = getTrick();
		Card[] hand = getHand();
		int trump = trick.getTrump();

		int playIndex = 0;

		// if it's the last trick, just throw the card
		if (hand.length == 1) {
			return hand[0];
		}

		if (trick.getPlayCount() == 0) {
			// MY LEAD
			
		}
		else {
			// SOMEONE ELSE LED
			
		}
		

		return null;
	}

}
