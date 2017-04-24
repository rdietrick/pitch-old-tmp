package com.pitchplayer.server.game;

/**
 * Represents a bid by a player
 */
public class Bid {

	private final int playerIndex;

	private final int bid;

	public static final int PASS = 0;


	/**
	 * Constructor. Creates a bid with an initial name and bid amount.
	 * 
	 * @param index
	 *            the bidder's index in the rotation of players
	 * @param amount
	 *            the amount the bidder is bidding
	 */
	public Bid(int index, int amount) {
		this.playerIndex = index;
		this.bid = amount;
	}


	/**
	 * Get the index of the bidder
	 */
	public int getPlayerIndex() {
		return this.playerIndex;
	}

	/**
	 * Get the number of points the bid is for
	 */
	public int getBid() {
		return this.bid;
	}

	/**
	 * Get a string representation of this bid.
	 */
	public String toString() {
		return "player at index " + playerIndex + " bid " + bid;
	}

}