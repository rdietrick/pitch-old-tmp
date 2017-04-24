package com.pitchplayer.server.game;

import com.pitchplayer.Card;

public class PlayedCard extends Card {

	int playedBy;

	/**
	 * Create a new Played Card
	 */
	public PlayedCard(Card card, int playerIndex) {
		super(card.getSuit(), card.getValue());
		this.playedBy = playerIndex;
	}

	/**
	 * Get the index of the player that played the card
	 */
	public int getPlayer() {
		return this.playedBy;
	}

}