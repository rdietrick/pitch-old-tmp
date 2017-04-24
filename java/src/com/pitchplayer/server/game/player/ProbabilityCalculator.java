package com.pitchplayer.server.game.player;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.PitchGame;

public interface ProbabilityCalculator {

	/**
	 * Get the probability that a card is high
	 * @param c
	 * @param handSize
	 * @param numPlayers
	 * @return
	 */
	public float getProbHigh(Card c, int handSize, int numPlayers);
	
	/**
	 * Get the probability that a card is low
	 * @param c
	 * @param handSize
	 * @param numPlayers
	 * @return
	 */
	public float getProbLow(Card c, int handSize, int numPlayers);
	
	
	/**
	 * Get the probability that a set of cards were dealt
	 * @param setSize the size of the set of cards in question
	 * @param handSize
	 * @param numPlayers
	 * @return
	 */
	public float getProbabilityCardsDealt(int setSize, int handSize, int numPlayers);

	
	/**
	 * Find the probability that a card is a winner.
	 * TODO: rename this method, as it's confusing.  This really returns the probability
	 * that a set of cards was NOT dealt.
	 * @param setSize the size of the set of cards which may beat the card in question 
	 * @param handSize the number of cards in a hand
	 * @param numPlayers the number of players in the game
	 * @return
	 */
	public float getWinnerProbability(int setSize, int handSize, int numPlayers);
	
}
