package com.pitchplayer.server.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.player.CPUPlayer;
import com.pitchplayer.server.game.player.GamePlayer;
import com.pitchplayer.server.game.player.PitchPlayer;
import com.pitchplayer.stats.om.GameRecord;
import com.pitchplayer.stats.om.GamePlayerRecord;

/**
 * A doubles Pitch game. Subclass of CardGame. <BR>
 */
public class DoublesPitchGame extends PitchGame {

	/***************************************************************************
	 * TODO: 1. create some object to lock on during all addPlayer methods,
	 * instead of making them all synchronized.
	 **************************************************************************/


	/**
	 * Creates a game with the supplied id number and one initial player.
	 * 
	 * @param gameRecord
	 *            the game's id number
	 * @param player
	 *            an initial GamePlayer
	 */
	DoublesPitchGame(GameRecord gameRecord, GameFactory gf, GamePlayer player, GameOptions gameOptions) {
		super(gameRecord, gf, player, gameOptions);
	}

	/**
	 * Create a Game with an initial team of players. Not implemented properly
	 * yet
	 */
	/*
	 * DoublesPitchGame(long id, GameFactory gf , GamePlayer p1, GamePlayer p2) {
	 * super(id, gf, p1); this.minPlayers = 4; this.type = "DoublesPitchGame"; }
	 */

	/**
	 * Add a player to the game in a particular position
	 * TODO: This is not properly implemented
	 * @param newPlayer
	 *            the new player to be added to the game
	 * @param pIndex
	 *            the position at which the player is to be added
	 */
	public synchronized DoublesPitchGame addPlayer(GamePlayer newPlayer, int pIndex) {
		if (!this.isJoinable() || pIndex > maxPlayers - 1) {
			log.warn("invalid attempt to add player");
			return null;
		}
		if (pIndex < players.size()) { // seat is already created
			// make sure they're not attempting to sit in someone else's seat
			if (players.elementAt(pIndex) != null) {
				return null;
			}
			newPlayer.setIndex(pIndex);
			players.setElementAt(newPlayer, pIndex);
		} else { // new seat needs to be created for player
			// create empty seats if necessary
			for (int i = players.size(); i < pIndex; i++) {
				players.addElement(null);
			}

			// add the new player
			newPlayer.setIndex(pIndex);
			players.addElement(newPlayer);
		}
		newPlayer.setGame(this);
		playerJoined(newPlayer);
		return this;
	}

	/**
	 * Add a new player to the game as the partner of an existing player.
	 * TODO: This is not properly implemented
	 * @param newPlayer
	 *            the new player
	 * @param partnerIndex
	 *            the existing player's index
	 */
	public synchronized DoublesPitchGame addPartner(GamePlayer newPlayer, int partnerIndex) {
		if (!this.isJoinable()) {
			return null;
		}
		newPlayer.setGame(this);
		// create new seats in between the partners if we have to
		if (players.size() <= partnerIndex + 1) {
			players.addElement(null);
		}

		newPlayer.setIndex(partnerIndex + 2);
		if (players.size() <= partnerIndex + 2) {
			// add a seat for the partner
			players.addElement(newPlayer);
		} else {
			// seat the partner in the vacant seat
			players.setElementAt(newPlayer, partnerIndex + 2);
		}
		playerJoined(newPlayer);
		return this;
	}

	protected int seatPlayer(GamePlayer newPlayer) {
		int seatIndex = -1;
		if (players.contains(null)) {
			// find an empty seat at the table
			seatIndex = players.indexOf(null);
			players.setElementAt(newPlayer, seatIndex);
		} else {
			// add a new player if all seats were taken
			seatIndex = players.size();
			players.addElement(newPlayer);
		}
		return seatIndex;
	}
	
	
	protected void playerJoined(GamePlayer newPlayer) {
		// notify all the other players
		for (GamePlayer p : players) {
			if ((p != newPlayer)) {
				p.notifyPlayerAdded(newPlayer.getUsername());
			}
		}
		if (this.isGameFull()) {
			sendToAll("Game is now full. Ready to begin.");
		}
	}
	
	/**
	 * Find out if the table is full
	 * 
	 * @return true if all seats at the table are taken
	 */
	public boolean isGameFull() {
		if ((players.size() < maxPlayers) || players.contains(null)) {
			return false;
		}
		return true;
	}

	/**
	 * Find out if a game is joinable
	 * 
	 * @return true if the table has an empty seat and the game hasn't been
	 *         started yet
	 */
	public boolean isJoinable() {
		if (!isGameFull() && status != GameStatus.RUNNING) {
			return true;
		}
		return false;
	}


	/**
	 * Get the hand for this type of game.
	 * 
	 * @return a DoublesHand
	 */
	protected Hand initNewHand(int size) {
		return new DoublesHand(size);
	}

	
	/**
	 * Assign points at the end of a hand. <br>
	 * Sums game points
	 * 
	 * @param thisHand
	 *            the hand which needs to be scored
	 */
	public void scoreHand(Hand thisHand) {
		thisHand.tallyGamePoints();
		// loop through players and adjust points
		for (int i = 0; i < players.size() / 2; i++) {
			gameRecord.getGamePlayerRecordAtIndex(i).adjustScore(-1* hand.getPointsMade(i));
		}

		// check for a winner:
		int winnerIndex = -1;
		for (int i = 0; i < players.size() / 2; i++) {
			int iScore = getPlayerScore(i);
			if ((iScore < 1) && (thisHand.getHighBid().getPlayerIndex() == i)) {
				winnerIndex = i;
				break;
			} else if (getWinnerIndex() > -1) {
				// already found someone at 0 - compare them to see who's lowest
				if ((iScore < 1) && (iScore < getPlayerScore(getWinnerIndex()))) {
					winnerIndex = i;
				}
			} else if (iScore < 1) {
				winnerIndex = i;
			}
		}

		if (winnerIndex > -1) {
			gameWon(winnerIndex);
		}
	}

	/**
	 * Send scores to the clients
	 * 
	 * @param gameOver
	 *            whether or not the game is over
	 */
	protected void showScores(Hand hand) {
		// construct the message:
		// "p1Name/p3Name,score,gamePoints;p2Name/p4Name,score,gamePoints"
		// TODO: Add construction of a message which shows what points each team earned
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < players.size() / 2; i++) {
			GamePlayer player = (GamePlayer) players.elementAt(i);
			GamePlayer partner = (GamePlayer) players.elementAt(i + 2);
			String teamName = player.getUsername() + "/"
					+ partner.getUsername();
			sb.append(teamName
					+ ","
					+ getPlayerScore(player)
					+ ","
					+ (hand.getPlayerGamePoints(i) + hand.getPlayerGamePoints(i + 2))
					+ (i < ((players.size() / 2) - 1) ? ";" : ""));
		}

		String msg = sb.toString();
		for (int i = 0, n = players.size(); i < n; i++) {
			((PitchPlayer) players.elementAt(i)).notifyScores(msg);
		}
	}

	/**
	 * Get game information.
	 * 
	 * @return a String containing the game id, the current status, and a list
	 *         of players
	 */
	public String getInfo() {
		StringBuffer infoStr = new StringBuffer(getGameId() + ",d,"
				+ this.status);
		for (int i = 0, n = players.size(); i < n; i++) {
			if (players.elementAt(i) == null) {
				infoStr.append(",empty");
			} else {
				infoStr
						.append(","
								+ ((GamePlayer) (players.elementAt(i)))
										.getUsername());
			}
		}
		return infoStr.toString() + ";";
	}
	
	/**
	 * Get the winning player's name.
	 */
	protected String getWinnerName() {
		if (getWinnerIndex() > -1) {
			return ((GamePlayer) (players.elementAt(getWinnerIndex())))
					.getUsername()
					+ "/"
					+ ((GamePlayer) (players.elementAt(getWinnerIndex() + 2)))
							.getUsername();
		} else {
			return null;
		}
	}

	/**
	 * Set the index of the winner
	 */
	void setWinnerIndex(int winnerIndex) {
		winner = winnerIndex;
		int i=0;
		for (GamePlayerRecord player : gameRecord.getGamePlayers()) {
			if (player.getSeat()%2 == winnerIndex%2) {
				player.setWinner((byte)1);
			}
			else {
				player.setWinner((byte)0);
			}
		}
	}
	
}

