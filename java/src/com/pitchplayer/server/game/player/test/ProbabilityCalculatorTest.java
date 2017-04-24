package com.pitchplayer.server.game.player.test;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.Deck;
import com.pitchplayer.server.game.player.ProbabilityCalculatorImpl;
import com.pitchplayer.server.game.player.StaticProbabilityCalculatorImpl;

import junit.framework.TestCase;

public class ProbabilityCalculatorTest extends TestCase {

	Logger log = Logger.getLogger(this.getClass().getName());
	
	public void testFactorial() {
		ProbabilityCalculatorImpl pc = new ProbabilityCalculatorImpl();
		this.assertEquals(pc.factorial(new BigInteger(String.valueOf(10))), new BigInteger("3628800"));
		this.assertEquals(pc.factorial(BigInteger.ONE), BigInteger.ONE);
		this.assertEquals(pc.factorial(new BigInteger(String.valueOf(24))), new BigInteger("620448401733239439360000"));
	}

	public void testFourPlayerProbabiity() {
		ProbabilityCalculatorImpl pc = new ProbabilityCalculatorImpl();
		float f = pc.getWinnerProbability(4, 6, 4);
		System.out.println("f = " + f);
		this.assertEquals(f, 0.125, 0.001f);
	}

	public void testGetProbHigh() {
		StaticProbabilityCalculatorImpl staticImpl = new StaticProbabilityCalculatorImpl();
		ProbabilityCalculatorImpl dynImpl = new ProbabilityCalculatorImpl();
		Deck deck = new Deck();
		Card[] cards = deck.getCards(52);
		for (Card c : cards) {
			float s = staticImpl.getProbHigh(c, 6, 3);
			float d = dynImpl.getProbHigh(c, 6, 3);
			this.assertEquals("values not equal for card " + c.getStrValue(), d, s, .000001);
		}
	}

	public void testGetProbLow() {
		StaticProbabilityCalculatorImpl staticImpl = new StaticProbabilityCalculatorImpl();
		ProbabilityCalculatorImpl dynImpl = new ProbabilityCalculatorImpl();
		Deck deck = new Deck();
		Card[] cards = deck.getCards(52);
		for (Card c : cards) {
			float s = staticImpl.getProbLow(c, 6, 3);
			float d = dynImpl.getProbLow(c, 6, 3);
			this.assertEquals("values not equal for card " + c.getStrValue(), d, s, .000001);
		}
	}
	
	public void testAllHighProbs() {
		log.debug("probabilities of high:");
		log.debug("4 players\t\t3 players");
		Deck deck = new Deck();
		ProbabilityCalculatorImpl pc = new ProbabilityCalculatorImpl();
		for (Card c : deck.getCards(13)) {
			float f4 = pc.getProbHigh(c, 6, 4);
			float f3 = pc.getProbHigh(c, 6, 3);
			log.debug(c.toString() + ": " + f4 + "\t" + f3);
		}
	}
	
	public void testGetProbDealt() {
		ProbabilityCalculatorImpl pc = new ProbabilityCalculatorImpl();
		log.debug("prob 1 card dealt with 3 players = " + pc.getProbabilityCardsDealt(1,6, 3));
		log.debug("prob 1 card dealt with 3 players = " + pc.getProbabilityCardsDealt(1,6, 4));
	}

}
