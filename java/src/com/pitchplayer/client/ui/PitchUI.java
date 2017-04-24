package com.pitchplayer.client.ui;

import java.awt.Image;
import java.net.URL;

import com.pitchplayer.Card;
import com.pitchplayer.client.ClientCard;

/**
 * @author robd
 *
 */
public interface PitchUI {

	/**
	 * Update the UI to display the list of games.
	 * Called when a server message is received with the list of current games.
	 * 
	 * @param gameInfo
	 *            the list of games received from the server
	 */
	public void notifyDisplayGameList(String[][] gameInfo);

	/**
	 * Update the UI when this player successfully joined a game.
	 * Called when a server message is received indicating that a game join
	 * attempt was successful.
	 *  
	 */
	public void notifyJoinOk(int gameType, String[] playerNames);

	/**
	 * Update the UI to display a message indicating that an attempt to join 
	 * a game failed.
	 * Called when a server message is received indicating that an attempt to
	 * join a game failed.
	 */
	public void notifyJoinFailed();

	/**
	 * Update the UI to display the newly added player in the current game.
	 * Called when a server message is received that indicates that a player was
	 * added to the game this client is in.
	 */
	public void notifyPlayerAdded(String playerName);

	/**
	 * Update the UI to display a played card.
	 * Called when a 'card played' message was received from the server.
	 */
	public void notifyCardPlayed(int playerIndex, ClientCard card);

	/**
	 * Update the UI to display a server message.
	 * Called when an informational server message is received.
	 */
	public void notifyServerMessage(String msg);

	/**
	 * Update the UI to display a chat message from a player.
	 * Called when a chat message is received
	 */
	public void notifyChatMessage(String msg);

	/**
	 * Update the UI to display winning bid information.
	 * @param playerIndex the index of the player who won the bid
	 * @param bidAmt the number of points the player bid
	 */
	public void notifyWinningBid(int playerIndex, int bidAmt);
	
	/**
	 * Update the UI to update the score of the current game.
	 * Display the scores in the user interface. 
	 * FIX: add format of score string to javadoc here
	 */
	public void notifyDisplayScores(String scoreString);

	/**
	 * Update the UI to indicate that the current game was aborted.
	 * 
	 * @param quitterName
	 *            the name of the player who quit the game
	 */
	public void notifyGameAborted(String quitterName);

	/**
	 * Update the UI to indicate that the current game is over (won).
	 * Update the UI to show which player won the game.
	 * 
	 * @param winnerName
	 *            the name of the winning player.
	 */
	public void notifyGameOver(String winnerName);
	
	/**
	 * Notify the UI to indicate that it is the user's turn to bid.
	 */
	public void notifyMyBid(String bidStr);

	/**
	 * Notify the UI to display a new hand of cards.
	 * 
	 * @param strHand
	 *            the new hand of cards, as a comma delimeted list of cards as
	 *            String representations (equivalent to:
	 *            Card.toString(),Card.toString(),...
	 */
	public void notifyTakeHand(String strHand);

	/**
	 * Notify the UI to indicate that it's this player's turn.
	 */
	public void notifyMyTurn(boolean isMyTurn);

	/**
	 * Notify the UI that a trick was won by a player.
	 * 
	 * @param playerIndex the index (seat) of the player who won the trick
	 * @param card the card which won the trick
	 */
	public void notifyTrickWon(int playerIndex, Card card);

	/**
	 * Play the sound at the specified location, relative to the location of the applet.
	 * @param soundFile
	 */
	public void playSound(String soundFile);
	
	/**
	 * Get an image relative to the location of the applet.
	 * @param s the path to the image file.
	 * @return an Image loaded from the pathname
	 */
	public Image getImage(String s);

	/**
	 * Notify this UI of an update to the list of logged-in users.
	 * @param args
	 */
	public void notifyDisplayUserList(String[] args);

	/**
	 * Notify this UI that authentication failed.
	 *
	 */
	public void notifyAuthFailed();

	/**
	 * Notify this UI that authentication succeeded
	 *
	 */
	public void notifyAuthSucceeded();

	public void notifyLobbyChat(String username, String message);

	
}
