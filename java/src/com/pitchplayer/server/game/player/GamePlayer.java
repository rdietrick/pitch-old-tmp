package com.pitchplayer.server.game.player;

import org.apache.log4j.Logger;

import com.pitchplayer.*;
import com.pitchplayer.server.game.CardGame;
import com.pitchplayer.userprofiling.om.User;

/**
 * Abstract base class for a player in a card game.
 */
public abstract class GamePlayer {

	private Integer userId;

	protected int index; // player's index in a game

	private CardGame game = null; // the game this player is in

	private String sessionId = null; // not sure if this is really needed for
	 // anything

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public boolean getInGame() {
		return (getGame() != null);
	}

	public abstract String getUsername();
	
	/**
	 * Get the user's session ID
	 */
	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * Set the user's session ID
	 */
	public void setSessionId(String s) {
		this.sessionId = s;
	}

	/**
	 * Get the card game this player is in.
	 */
	protected CardGame getGame() {
		return this.game;
	}

	/**
	 * Set the card game this player is in.
	 */
	public void setGame(CardGame game) {
		this.game = game;
	}

	/**
	 * Set the player's index in a game.
	 */
	public void setIndex(int i) {
		this.index = i;
	}

	/**
	 * Get the player's index in a game.
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * Notify this player that a card was played by another player. <br>
	 * Called from a CardGame.
	 * 
	 * @param playerIndex
	 *            the index of the player playing the card
	 * @param playedCard
	 *            info about the card being played
	 */
	public abstract void notifyPlay(int playerIndex, Card playedCard);

	/**
	 * Notify this player that it is their turn. <br>
	 * Called from a CardGame.
	 */
	public abstract Card notifyTurn();

	/**
	 * Notify the client that a new player has been added to the game. <br>
	 * Called from a CardGame. Default implementation does nothing.
	 * 
	 * @param playerName
	 *            the name of the player added to a game
	 */
	public void notifyPlayerAdded(String playerName) {
	}

	/**
	 * Notify this player that the game has been won. <br>
	 * The game session may continue (to allow chating) after
	 * this method is called and until gameEnded() is called.
	 * 
	 * @param winner
	 *            the name of the winning player.
	 */
	public void gameWon(String winner) {
	}
	

	/**
	 * Notify the player that another player aborted the game.
	 * This implementation does nothing.
	 * 
	 * @param quiter
	 *            the name of the quitter
	 */
	public void gameAborted(String quitter) {
	}

	
	/**
	 * Notify this player that the game session has ended.
	 */
	public void gameEnded() {
		// If I'm the only player in the game, the server ended it, so I can safely leave the game.
		if (getGame().getNumPlayers() == 1) {
			leaveGame();
		}
	}

	/**
	 * Accept a message from the server. <br>
	 * Called from a CardGame. This implementation does nothing.
	 */
	public void serverMessage(String message) {
	}

	/**
	 * Accept a message from someone another player. <br>
	 * Called from a CardGame. This implementation does nothing.
	 * 
	 * @param player
	 *            the name of the player sending the message
	 * @param quote
	 *            the message
	 */
	public void sendQuote(String player, String quote) {
	}

	/**
	 * Notify this player of which player won the last trick. Called from a
	 * CardGame.
	 * 
	 * @param playerIndex
	 *            the index of the player who one
	 * @param card
	 *            the winning card
	 */
	public abstract void notifyTrickWon(int playerIndex, Card card);

	/**
	 * Take a hand of cards. <br>
	 * Called from a CardGame.
	 * 
	 * @param hand
	 *            an array of cards.
	 */
	public void takeHand(Card[] hand) {
		sortCards(hand);
		StringBuffer cards = new StringBuffer(hand[0].toString());
		for (int i = 1, n = hand.length; i < n; i++) {
			cards.append("," + hand[i].toString());
		}
	}

	/**
	 * Sort the cards by suit and value. <br>
	 * Sorts suits in order Spaded, Hearts, Clubs, Diamonds and values from Ace
	 * down to 2.
	 * 
	 * @param cards
	 *            a hand of cards to be sorted
	 */
	public static void sortCards(Card[] cards) {
		for (int i = 0, n = cards.length; i < n; i++) {
			/*
			 * From the first element to the end of the unsorted section
			 */
			for (int j = 1; j < (cards.length - i); j++) {
				/* If adjacent items are out of order, swap them */
				if (cards[j - 1].getSortValue() > cards[j].getSortValue()) {
					swapElements(cards, j - 1, j);
				}
			}
		}
	}

	/**
	 * Method to swap to elements in an array
	 */
	private static void swapElements(Object[] arr, int pos1, int pos2) {
		Object temp = arr[pos1];
		arr[pos1] = arr[pos2];
		arr[pos2] = temp;
	}
	
	/**
	 * Leave a game.
	 * The status of the game will determine whether this results in a quit or not.
	 */
	void leaveGame() {
		if (game != null) {
			game.leaveGame(this);
			game = null;
		}
	}

	/**
	 * Send a lobby chat message to a player.
	 * Default implementation does nothing.
	 * @param username
	 * @param text
	 */
	public void notifyLobbyChat(String username, String text) {
		// TODO Auto-generated method stub
		
	}

	public abstract User getUser();

	/**
	 * Called when another player leaves the current game
	 * @param p
	 */
	public void notifyPlayerLeftGame(GamePlayer p) {
		// TODO Auto-generated method stub
		
	}

	public void serverMultiMessage(String[] messages) {
		// TODO Auto-generated method stub
		
	}
	
}