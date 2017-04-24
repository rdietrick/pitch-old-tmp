package com.pitchplayer.server.game;

import java.util.LinkedList;
import java.util.List;

import com.pitchplayer.Card;

/**
 * Represents a deck of 52 cards
 */
public class Deck {

	Card[] cards;

	int nextCard = 0;

	/**
	 * Creates a normal deck of 52 cards, initially unshuffled (ordered by suit
	 * and value)
	 */
	public Deck() {
		cards = new Card[52];
		int cardNum = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				cards[cardNum] = new Card(i, j);
				cardNum++;
			}
		}
	}

	/**
	 * Shuffles the deck
	 */
	public void shuffle() {
		List<Card> unshuffled = new LinkedList<Card>();

		for (Card c : cards) {
			unshuffled.add(c);
		}

		for (int i = 0; i < 52;i++) {
			int rand = (int) (Math.random() * unshuffled.size());
			cards[i] = unshuffled.remove(rand);
		}
		nextCard = 0;
	}

	/**
	 * Grab the next <i>quantity </i> of cards from the top of the deck. <br>
	 * Useful for dealing.
	 * 
	 * @param quantity
	 *            the number of cards to grab from the top of the deck
	 * @return a specified number of cards from the top of the deck
	 */
	public Card[] getCards(int quantity) {
		Card[] nextCards = new Card[quantity];
		for (int i = 0; i < quantity; i++) {
			nextCards[i] = cards[nextCard];
			nextCard++;
		}
		return nextCards;
	}

}