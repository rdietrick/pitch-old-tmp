package com.pitchplayer.server.game.player;

import com.pitchplayer.server.game.Bid;

/**
 * Base class for players in a Pitch game
 */
public abstract class PitchPlayer extends GamePlayer {

	/**
	 * Notify this player that it is their bid (pitch specific)
	 */
	public abstract int notifyBidTurn(Bid[] bids);

	/**
	 * Notify this player of the winning bid information
	 */
	public abstract void notifyBidder(Bid bid);

	/**
	 * Notify this player of the current score
	 */
	public abstract void notifyScores(String scoreMsg);

	/**
	 * Notify this player of a new bid.
	 * Default implementation does nothing. 
	 * @param index index of the player making the bid
	 * @param bid amount bid
	 */
	public void notifyBidMade(int index, int bid) {
		
	}
	

}