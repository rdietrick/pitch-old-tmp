package com.pitchplayer.server.game;

import com.pitchplayer.Card;

/**
 * Represents a trick of cards from a player's perspective. Provides information
 * about the cards played so far.
 */
public class Trick {

	private PlayedCard[] cards;

	int cardCount;

	int trickCount;

	int winningCard;

	int winner;

	int leadSuit;

	int trump;

	int gamePoints = 0;

	boolean jackOut;

	/**
	 * Create a new Trick for a given # of players.
	 * 
	 * @param numPlayers
	 */
	public Trick(int numPlayers) {
		this.cards = new PlayedCard[numPlayers];
		this.cardCount = 0;
		this.winningCard = -1;
		this.winner = -1;
		this.leadSuit = -1;
		this.trump = -1;
		this.gamePoints = 0;
		this.jackOut = false;
		this.trickCount = 0;
	}

	/**
	 * Reinitialize the trick, so we don't have to create a new one. Should be
	 * called at the end of each trick.
	 */
	public void reset() {
		this.cards = new PlayedCard[cards.length];
		this.cardCount = 0;
		this.winningCard = -1;
		this.winner = -1;
		this.leadSuit = -1;
		this.gamePoints = 0;
		this.jackOut = false;
		this.trickCount++;
	}

	/**
	 * Keep track of a played card.
	 */
	public void cardPlayed(Card card, int playerIndex) {
		this.cards[cardCount] = new PlayedCard(card, playerIndex);
		gamePoints += card.getGamePoints();
		if (cardCount == 0) {
			if (trickCount == 0) {
				trump = card.getSuit();
			}
			this.leadSuit = card.getSuit();
			this.winner = playerIndex;
			this.winningCard = cardCount;
		} else {
			// determine if this card is winning the trick:
			if (
			// beat a card of the same suit?
			((card.getSuit() == cards[winningCard].getSuit()) && (card
					.getValue() > cards[winningCard].getValue()))
					||
					// trumped a non-trump card?
					((card.getSuit() != cards[winningCard].getSuit()) && (card
							.getSuit() == trump)))
			// mark this card/player as the winner of the trick
			{
				this.winner = playerIndex;
				this.winningCard = cardCount;
			}
		}
		if ((card.getSuit() == trump) && (card.getValue() == Card.JACK)) {
			jackOut = true;
		}
		this.cardCount++;
	}

	/**
	 * Find out which player is winning the trick
	 * 
	 * @return the index of the player currently winning the trick
	 */
	public int getWinner() {
		return this.winner;
	}

	/**
	 * Find out what the card winning the trick is
	 */
	public Card getWinningCard() {
		if (winningCard >= 0) {
			return cards[winningCard];
		} else {
			return null;
		}
	}

	/**
	 * Find out what the total number of game points in the trick is so far.
	 */
	public int getGamePoints() {
		return gamePoints;
	}

	/**
	 * Get all the cards thrown so far in this trick
	 */
	public Card[] getCards() {
		return cards;
	}

	/**
	 * Get the number of cards played so far
	 */
	public int getPlayCount() {
		return cardCount;
	}

	/**
	 * Get the trump suit
	 */
	public int getTrump() {
		return this.trump;
	}

	/**
	 * Find out what suit was lead
	 * 
	 * @return an int equating to the suit which was lead
	 */
	public int getLeadSuit() {
		return this.leadSuit;
	}

	/**
	 * Find out whether the jack was played by a player in this trick
	 */
	public boolean getJackOut() {
		return jackOut;
	}

	/**
	 * Get the trick count 0 means first trick
	 */
	public int getTrickCount() {
		return trickCount;
	}

}