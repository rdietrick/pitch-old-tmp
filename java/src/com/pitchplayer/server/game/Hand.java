package com.pitchplayer.server.game;

import java.util.ArrayList;

import com.pitchplayer.Card;

/**
 * Represents a hand of cards (Pitch-specific) Used for scorekeeping purposes.
 */
public abstract class Hand {

	private int numPlayers; // the number of players in this game

	private int low; // the lowest trump card out so far

	private int lowWinner; // index of player currently winning the low

	private int high; // the highest trump card out so far

	private int highWinner; // index of the player currently winning the high

	private boolean jackOut; // whether or not the jack has been played this
							 // trick

	private int jackThrower; // index of the player who threw the jack

	private int jackWinner; // index of player with the jack

	private ArrayList<Bid> bids = new ArrayList<Bid>(); // an array of all placed bids (including passes)

	private int highBidIndex; // the current high bid

	private int trump; // suit value

	private Card winningCard; // current winning card

	private int winningPlayer; // player with current winning card

	private boolean handStarted = false;// whether or not the hand was started

	private int trickGamePoints; // keep track of game points in each trick

	private int[] gamePoints; // array of game points for each player

	private int gameWinner = -1; // winner of Game point

	public static final int GAME_POINT_TIE = -1;

	/**
	 * Create a new hand with no initial high, low, jack, and 0 game points for
	 * all.
	 * 
	 * @param numPlayers
	 *            the number of players in the hand.
	 */
	Hand(int numPlayers) {
		// initialize points
		this.numPlayers = numPlayers;
		low = 13;
		lowWinner = -1;
		high = -1;
		highWinner = -1;
		jackOut = false;
		jackThrower = -1;
		jackWinner = -1;
		highBidIndex = -1;
		winningCard = null;
		winningPlayer = -1;
		handStarted = false;
		trickGamePoints = 0;
		gamePoints = new int[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			gamePoints[i] = 0;
		}
	}

	/**
	 * Get the number of players in the hand
	 */
	public int getNumPlayers() {
		return numPlayers;
	}

	/**
	 * Get the winning player (player who won the last trick)
	 */
	protected final int getWinningPlayer() {
		return winningPlayer;
	}

	/**
	 * Set the index of the player currently winning the trick
	 */
	protected final void setWinningPlayer(int index) {
		winningPlayer = index;
	}

	/**
	 * Get the winning Card (the card which won the last trick)
	 */
	protected final Card getWinningCard() {
		return winningCard;
	}

	/**
	 * Set the winning Card
	 */
	protected final void setWinningCard(Card c) {
		winningCard = c;
	}

	/**
	 * Note a new bid.
	 * 
	 * @param newBid
	 *            the new Bid
	 */
	public void noteBid(Bid newBid) {
		bids.add(newBid);
		if ((highBidIndex == -1)
				|| (newBid.getBid() > bids.get(highBidIndex).getBid())) {
			highBidIndex = bids.size() - 1;
		}
	}

	/**
	 * Get all the placed bids as an array.
	 */
	public Bid[] getBids() {
		return bids.toArray(new Bid[] {});
	}

	/**
	 * Get the bid
	 */
	public Bid getHighBid() {
		if (bids.size() == 0) {
			return null;
		}
		return bids.get(highBidIndex);
	}

	/**
	 * Set the trump suit. <br>
	 * Called when first card of a hand is played to note the trump suit.
	 * 
	 * @param firstCard
	 *            the card being thrown
	 */
	public final void setTrump(Card firstCard) {
		trump = firstCard.getSuit();
	}

	/**
	 * Set the current High
	 */
	protected final void setHigh(int i) {
		high = i;
	}

	/**
	 * Get the current High
	 */
	protected final int getHigh() {
		return high;
	}

	/**
	 * Set the current Low
	 */
	protected final void setLow(int i) {
		low = i;
	}

	/**
	 * Get the current Low
	 */
	protected final int getLow() {
		return low;
	}

	/**
	 * Set the winner of the High point
	 */
	protected final void setHighWinner(int playerIndex) {
		highWinner = playerIndex;
	}

	/**
	 * Get the index of the player currently winning the High point
	 */
	protected final int getHighWinner() {
		return highWinner;
	}

	/**
	 * Set the winner of the Low point
	 */
	protected final void setLowWinner(int playerIndex) {
		lowWinner = playerIndex;
	}

	/**
	 * Get the index of the player currently winning the Low point
	 */
	protected final int getLowWinner() {
		return lowWinner;
	}

	/**
	 * Set the winner of the Jack point
	 */
	protected final void setJackWinner(int playerIndex) {
		jackWinner = playerIndex;
	}

	/**
	 * Get the index of the player currently winning the Jack point
	 */
	protected final int getJackWinner() {
		return jackWinner;
	}

	/**
	 * Get the number of game points a player has taken in this trick
	 */
	public int getPlayerGamePoints(int playerIndex) {
		return gamePoints[playerIndex];
	}

	/**
	 * Set the winner of the Game point
	 */
	protected final void setGameWinner(int playerIndex) {
		gameWinner = playerIndex;
	}

	/**
	 * Get the Game point winner
	 */
	protected final int getGameWinner() {
		return gameWinner;
	}

	/**
	 * Check a card for the Low point
	 * 
	 * @param playerIndex
	 *            the index of the player throwing the card
	 * @param card
	 *            the card being played
	 */
	protected final void checkLow(int playerIndex, Card card) {
		if (card.getValue() < low) {
			setLow(card.getValue());
			setLowWinner(playerIndex);
		}
	}

	/**
	 * Check a card for the High point
	 * 
	 * @param playerIndex
	 *            the index of the player throwing the card
	 * @param card
	 *            the card being played
	 */
	protected final void checkHigh(int playerIndex, Card card) {
		if (card.getValue() > high) {
			setHigh(card.getValue());
			setHighWinner(playerIndex);
		}
	}

	/**
	 * Checks a thrown card for high, low, jack, and game points
	 * 
	 * @param playerIndex
	 *            the index of the player throwing the card
	 * @param card
	 *            the card being played
	 */
	private final void checkForPoints(int playerIndex, Card newCard) {
		// trump-related checks (H,L,J)
		if (newCard.getSuit() == trump) {
			checkLow(playerIndex, newCard);
			checkHigh(playerIndex, newCard);
			if (newCard.getStrValue().equals("J")) {
				jackOut = true;
				jackThrower = playerIndex;
			}
		}
		// check for game points
		trickGamePoints += newCard.getGamePoints();
	}

	/**
	 * Score a played card.
	 * 
	 * @param playerIndex
	 *            the index of the player throwing the card
	 * @param newCard
	 *            the card being played
	 */
	public final void scoreCard(int playerIndex, Card newCard) {
		if (!handStarted) {
			// if it's the first card of the hand, note the trump suit
			setTrump(newCard);
			handStarted = true;
		}

		if (winningCard == null) {
			// first card of trick was thrown - mark it as the winner
			winningCard = newCard;
			winningPlayer = playerIndex;
		} else {
			// check if new card beats winning card
			if (
			// beat a card of the same suit
			((newCard.getSuit() == winningCard.getSuit()) && (newCard
					.getValue() > winningCard.getValue()))
					||
					// trumped a non-trump card
					((newCard.getSuit() != winningCard.getSuit()) && (newCard
							.getSuit() == trump)))
			// mark this card/player as the winner of the trick
			{
				setWinningCard(newCard);
				setWinningPlayer(playerIndex);
			}
		}
		// check for points
		checkForPoints(playerIndex, newCard);
	}

	/**
	 * Reset trick information, after a trick is cleared. <br>
	 * Resets the winning player, winning card, and Game points
	 */
	public final void newTrick() {
		winningPlayer = -1;
		winningCard = null;
		trickGamePoints = 0;
	}

	/**
	 * Find out who the current winner of the trick is.
	 * 
	 * @return the index of the player currently winning the trick
	 */
	public final int getTrickWinner() {
		return winningPlayer;
	}

	/**
	 * Score the trick. <br>
	 * Assigns the Game points to the winner of the trick. Assigns the Jack to
	 * the winner of the trick, IF the jack was thrown.
	 */
	public final void scoreTrick() {
		gamePoints[winningPlayer] += trickGamePoints;
		if (jackOut) {
			jackWinner = winningPlayer;
			jackOut = false;
		}
	}

	/**
	 * Find out if the Jack was stolen
	 */
	public boolean wasJackStolen() {
		return (jackThrower != jackWinner);
	}

	/**
	 * Find out which player threw the Jack
	 */
	public int getJackThrower() {
		return jackThrower;
	}

	/**
	 * Total up the game points for each player, and find out if there was a tie
	 * for game.
	 */
	public abstract void tallyGamePoints();

	/**
	 * Find out how many points a player made during this hand. Values returned
	 * will be negative if the player did not make his/her bid.
	 * 
	 * @param playerIndex
	 *            the index of the player whose point total is to be returned.
	 * @return the number of points a player made during this hand
	 */
	public abstract int getPointsMade(int playerIndex);

}

