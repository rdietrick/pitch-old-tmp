package com.pitchplayer.server.game.player.test;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.Deck;
import com.pitchplayer.server.game.player.PitchPlayer;

import junit.framework.TestCase;

public class CardSortTest extends TestCase {

	public void testSort() {
		Deck d = new Deck();
		d.shuffle();
		Card[] hand = d.getCards(6);
		PitchPlayer.sortCards(hand);
		for (Card c : hand) {
			System.out.println(c.toString());
		}
	}
}
