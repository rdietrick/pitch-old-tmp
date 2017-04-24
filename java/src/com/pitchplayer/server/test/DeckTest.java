package com.pitchplayer.server.test;

import junit.framework.*;

import com.pitchplayer.server.game.Deck;
import com.pitchplayer.Card;

import java.util.Hashtable;

import org.apache.log4j.Logger;

/**
 * Test case for a deck of cards
 */
public class DeckTest extends TestCase {

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public DeckTest(String name) {
		super(name);
	}

	/**
	 * Test that the correct number of cards are dealt
	 */
	public void testDeal() {
		Deck cardDeck = new Deck();
		assertEquals(6, cardDeck.getCards(6).length);
	}

	/**
	 * Test that a shuffled deck still contains 52 unique cards
	 */
	public void testShuffle() {
		Deck cardDeck = new Deck();
		cardDeck.shuffle();
		Hashtable cardHash = new Hashtable();
		Card[] cards = cardDeck.getCards(52);
		assertEquals("Wrong number of cards dealt", 52, cards.length);
		for (int i = 0; i < cards.length; i++) {
			String cardName = cards[i].getStrValue() + cards[i].getStrSuit();
			if (cardHash.containsKey(cardName)) {
				fail("Duplicate card: " + cardName);
			} else {
				cardHash.put(cardName, cards[i]);
			}
			log.debug(cardName);
		}
		assertTrue(true);
	}
	
	public void testSortValues() {
		Deck d = new Deck();
		for (Card c : d.getCards(52)) {
			log.debug(c.toString() + ": " + c.getSortValue());
		}
	}
	
	public void testStringCardConstruction() {
		try {
			Card c = new Card("S");
			this.assertTrue("Single-char card construction should fail", false);
		} catch (IllegalArgumentException iae) {
		}
		try {
			Card c = new Card((String)null);
			this.assertTrue("Null string card construction should fail", false);
		} catch (IllegalArgumentException iae) {
		}
		try {
			Card c = new Card("10S");
			this.assertTrue("Illegal string card construction should fail", false);
		} catch (IllegalArgumentException iae) {
		}

		assertTrue(true);
	}
	

}