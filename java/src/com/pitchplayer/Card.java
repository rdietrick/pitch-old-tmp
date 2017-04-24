package com.pitchplayer;

/**
 * A Card in a CardGame. Has both a suit and a "value" (2-A)
 */

public class Card {
	private int suit;

	private int intValue;

	private String strValue;

	/**
	 * Integer constant which represents the suit Spades
	 */
	public final static int SPADE = 0;

	/**
	 * Integer constant which represents the suit Hearts
	 */
	public final static int HEART = 1;

	/**
	 * Integer constant which represents the suit Clubs
	 */
	public final static int CLUB = 2;

	/**
	 * Integer constant which represents the suit Diamonds
	 */
	public final static int DIAMOND = 3;

	/**
	 * Array of Strings representing the allowable values of cards ("2" - "9",
	 * "T", "J", "Q", "K", "A")
	 */
	public final static String[] VALUES = { "2", "3", "4", "5", "6", "7", "8",
			"9", "T", "J", "Q", "K", "A" };

	public static final int ACE = 12;

	public static final int KING = 11;

	public static final int QUEEN = 10;

	public static final int JACK = 9;

	/**
	 * Instantiate a card with an initial suit and value.
	 * 
	 * @param intSuit
	 *            an integer corresponding to one of the four suit constants
	 * @param intValue
	 *            the integer value of the card
	 */
	public Card(int intSuit, int intValue) {
		suit = intSuit;
		this.intValue = intValue;
		strValue = VALUES[intValue];
	}

	/**
	 * Instantiate a card with an initial suit and value.
	 * 
	 * @param intSuit
	 *            an integer corresponding to one of the four suit constants
	 * @param strValue
	 *            one of the values within the VALUES array.
	 */
	public Card(int intSuit, String strValue) {
		this.suit = intSuit;
		this.strValue = strValue;
		for (int i = 0, n = VALUES.length; i < n; i++) {
			if (strValue.equals(VALUES[i])) {
				this.intValue = i;
				return;
			}
		}
		throw new IllegalArgumentException("Illegal value argument: " + strValue);
	}

	/**
	 * Instantiate a card with an initial suit and value.
	 * 
	 * @param strCard
	 *            string to be parsed into a suit and values respectively (e.g.
	 *            JD, 2S, TC)
	 */
	public Card(String strCard) {
		if (strCard == null || strCard.length() < 2 || strCard.length() > 2) {
			throw new IllegalArgumentException("Illegal card string: " + strCard);
		}
		String strSuit = strCard.substring(1);
		if (strSuit.equals("S")) {
			this.suit = SPADE;
		}
		else if (strSuit.equals("H")) {
			this.suit = HEART;
		}
		else if (strSuit.equals("C")) {
			this.suit = CLUB;
		}
		else if (strSuit.equals("D")) {
			this.suit = DIAMOND;
		}
		else { 
			throw new IllegalArgumentException("Illegal suit argument: " + strSuit);
		}
		this.strValue = strCard.substring(0, 1);
		for (int i = 0, n = VALUES.length; i < n; i++) {
			if (this.strValue.equals(VALUES[i])) {
				this.intValue = i;
				return;
			}
		}
		throw new IllegalArgumentException("Illegal value argument: " + strValue);
	}

	/**
	 * Comapare for equality with another card.
	 * 
	 * @return true if the two cards suit and values are the same
	 */
	public final boolean equals(Card otherCard) {
		if (this.getSortValue() == otherCard.getSortValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Get the value of the card. <BR>
	 * Values are represented as the integer index within the VALUES array.
	 * 
	 * @return the integer value of the card (0-12, where a '2' == 0 and an
	 *         'Ace' == 12)
	 */
	public final int getValue() {
		return this.intValue;
	}

	/**
	 * Get the suit of the card.
	 * 
	 * @return an integer corresponding to one of the suit constants above.
	 */
	public final int getSuit() {
		return this.suit;
	}

	/**
	 * Get the suit of the card as a String.
	 * 
	 * @return a single-character (capitalized) string representing the first
	 *         letter of the suit of this card.
	 */
	public final String getStrSuit() {
		return getSuit(getSuit());
	}

	/**
	 * Get the string value for a suit integer.
	 */
	public final static String getSuit(int suit) {
		String strSuit;

		switch (suit) {
		case 0:
			strSuit = "S";
			break;

		case 1:
			strSuit = "H";
			break;

		case 2:
			strSuit = "C";
			break;
		case 3:
			strSuit = "D";
			break;

		default:
			strSuit = "";
		}
		return strSuit;
	}

	/**
	 * Get the value of the card as a String.
	 * 
	 * @return a string representing the value of this card (e.g. "2", "T",
	 *         "J", "Q", "K", "A")
	 */
	public final String getStrValue() {
		return this.strValue;
	}

	/**
	 * Get a string representation of this card.
	 * 
	 * @return a string with combined value and suit (e.g. "TD", "AC")
	 */
	public String toString() {
		return getStrValue() + getStrSuit();
	}

	/**
	 * Get a unique integer for each card, which aids in sorting high-to-low by suit.
	 * 
	 */
	public final int getSortValue() {
		return 13 * suit + (12 - intValue);
	}

	public int hashcode() {
		return getSortValue();
	}
	
	/**
	 * Get the game points this card is worth.  This is Pitch-specific and should probably
	 * be in a subclass PitchCard.
	 */
	public final int getGamePoints() {
		// check for Game points
		if (intValue == 8) {
			return 10;
		} else if (intValue > 8) {
			return intValue - 8;
		} else {
			return 0;
		}
	}

}