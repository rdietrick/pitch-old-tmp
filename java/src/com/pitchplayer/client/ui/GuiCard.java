package com.pitchplayer.client.ui;

import com.pitchplayer.client.ClientCard;
import com.pitchplayer.client.ui.GuiPitchClient;

import java.awt.Rectangle;

/**
 * Encapsulates all information necessary when displaying a card in the player's
 * hand.
 */
public class GuiCard {

	private ClientCard card;

	private Rectangle boundingRect;

	private int handIndex = -1;

	/**
	 * Instantiate a card with an initial suit and value.
	 * 
	 * @param card
	 * @param useFullImage
	 *            whether the full image should be used
	 */
	public GuiCard(ClientCard card, Rectangle bounds, int handIndex) {
		this.card = card;
		this.boundingRect = bounds;
		this.handIndex = handIndex;
	}

	/**
	 * Get the card which should be represented on the screen
	 */
	public ClientCard getCard() {
		return this.card;
	}

	/**
	 * Get the indice in the player's hand array
	 */
	public int getHandIndex() {
		return this.handIndex;
	}

	/**
	 * Get the bounding rectangle for this card
	 */
	public Rectangle getBoundingRect() {
		return this.boundingRect;
	}

}