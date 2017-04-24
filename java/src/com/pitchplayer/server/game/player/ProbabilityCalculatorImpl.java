package com.pitchplayer.server.game.player;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import com.pitchplayer.Card;

public class ProbabilityCalculatorImpl implements ProbabilityCalculator {

	static float[][] probCache = new float[51][2];

	/**
	 * Factorial function implementation.
	 * @param n
	 * @return
	 */
	public static BigInteger factorial(BigInteger n) {
		if (n.equals(BigInteger.ONE)) {
			return n;
		}
		return n.multiply(factorial(n.subtract(BigInteger.ONE)));
	}
	
	/**
	 * Find the probability that a card will win the HIGH point.
	 * @return the probability that there were no cards higher than card c dealt.
	 */
	public float getProbHigh(Card c, int handSize, int numPlayers) {
		if (c.getValue() == Card.ACE) {
			return 1f;
		}
		int beaters = Card.ACE - c.getValue(); // # of cards that can beat my card
		return getWinnerProbability(beaters, handSize, numPlayers);
	}

	/**
	 * Find the probability that a card will win the LOW point.
	 * @return the probability that there were no cards lower than card c dealt.
	 */
	public float getProbLow(Card c, int handSize, int numPlayers) {
		if (c.getValue() == 0) {
			return 1f;
		}
		int beaters = c.getValue(); // # of cards that can beat my card
		return getWinnerProbability(beaters, handSize, numPlayers);
	}
	
	/**
	 * Find the probability that a card is a winner.
	 * @param setSize the size of the set of cards which may beat the card in question 
	 * @param handSize the number of cards in a hand
	 * @param numPlayers the number of players in the game
	 * @return
	 */
	public float getWinnerProbability(int setSize, int handSize, int numPlayers) {
		if (probCache[setSize][numPlayers-3] > 0.0) {
			return probCache[setSize][numPlayers-3];
		}
		int allCards = 52 - handSize; // all unknown cards
		int otherHands = (numPlayers-1)*handSize; // all cards in other players' hands
		int nonBeaters = 52 - setSize - handSize; // cards which can't beat the cards in setSize
		BigInteger winningOutcomes = choose(nonBeaters, otherHands);
		BigInteger allOutcomes = choose(allCards, otherHands);
		BigDecimal prob = new BigDecimal(winningOutcomes).divide(new BigDecimal(allOutcomes), new MathContext(100)); 
		probCache[setSize][numPlayers-3] = prob.floatValue();
		return probCache[setSize][numPlayers-3];
	}
	

	/**
	 * Find the probability that a set of cards was dealt.
	 * @param setSize the size of the set of cards
	 * @param handSize the size of a hand of cards
	 * @param numPlayers the number of players in the game
	 * 
	 */
	public float getProbabilityCardsDealt(int setSize, int handSize,
			int numPlayers) {
		return 1f - getWinnerProbability(setSize, handSize, numPlayers);
	}
	
	/**
	 * Implements the "choose" formula, C(n,r) =  n!/(r!(n-r)!) 
	 * Used to determine the number of possible combinations of r objects from a set of n objects.
	 * @param n the entire set of objects
	 * @param r the subset whose possible combinations are to be chosen
	 * @return the result of the formula
	 */
	public static BigInteger choose(int n, int r) {
		BigInteger nFact = factorial(new BigInteger(String.valueOf(n)));
		BigInteger nMinusRFact = factorial(new BigInteger(String.valueOf(n-r)));
		BigInteger rFact = factorial(new BigInteger(String.valueOf(r)));
		return nFact.divide(nMinusRFact).multiply(rFact);
	}


}
