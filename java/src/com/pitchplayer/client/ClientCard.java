package com.pitchplayer.client;

import com.pitchplayer.Card;

/**
 * Card subclass with convenient methods for use in clients.
 */
public class ClientCard extends Card {

	private boolean played = false;

	private boolean isPlayable = false;

	private boolean blinking = false;

	private boolean blinkState = false;

	/**
	 * Instantiate a card with an initial suit and value.
	 * 
	 * @param intSuit
	 *            an integer corresponding to one of the four suit constants
	 * @param intValue
	 *            the integer value of the card
	 */
	public ClientCard(int intSuit, int intValue) {
		super(intSuit, intValue);
	}

	/**
	 * Instantiate a card with an initial suit and value.
	 * 
	 * @param intSuit
	 *            an integer corresponding to one of the four suit constants
	 * @param strValue
	 *            one of the values within the VALUES array.
	 */
	public ClientCard(int intSuit, String strValue) {
		super(intSuit, strValue);
	}

	/**
	 * Instantiate a card with an initial suit and value.
	 * 
	 * @param strCard
	 *            string to be parsed into a suit and values respectively (e.g.
	 *            JD, 2S, TC)
	 */
	public ClientCard(String strCard) {
		super(strCard);
	}

	public void setPlayed() {
		this.played = true;
	}

	public boolean wasPlayed() {
		return this.played;
	}

	/**
	 * Find out whether this card is playable.
	 */
	public boolean getIsPlayable() {
		return !this.played && this.isPlayable;
	}

	/**
	 * Set the card as playable or not
	 */
	public void setIsPlayable(boolean playable) {
		this.isPlayable = playable;
	}

	public void setBlinking(boolean on) {
		this.blinking = on;
	}

	public boolean getBlinkState() {
		return this.blinkState;
	}

	public void toggleBlinkState() {
		this.blinkState = !blinkState;
	}

	/**
	 * Get the name of the file which represents this card
	 */
	public String getFullImageFilename() {
		StringBuffer sb = new StringBuffer("../images/").append(
				getStrValue().toLowerCase()).append(getStrSuit().toLowerCase())
				.append(".gif");
		return sb.toString();
	}

	/**
	 * Get the name of the file which represents this card
	 */
	public String getSmallImageFilename() {
		StringBuffer sb = new StringBuffer("../images/").append(
				getStrValue().toLowerCase()).append(getStrSuit().toLowerCase())
				.append("p.gif");
		return sb.toString();
	}

}