package com.pitchplayer.server.game.player;

import org.apache.log4j.Logger;

import com.pitchplayer.*;
import com.pitchplayer.server.game.Bid;
import com.pitchplayer.server.game.CheaterException;
import com.pitchplayer.server.game.Trick;
import com.pitchplayer.userprofiling.om.User;

/**
 * Base class for a computer player in a Pitch game
 */
public abstract class CPUPlayer extends PitchPlayer {

	private Card[] hand;

	private Bid[] bids;

	private Trick trick;

	private int[] suits;

	protected Logger log = Logger.getLogger(this.getClass().getName());

	private final User user;
	
	protected Bid currentBid;
	
	/**
	 * Create a new CPU player.
	 */
	public CPUPlayer(User user) {
		this.user = user;
	}

	/**
	 * Called from game when a trick is won.
	 */
	public void notifyTrickWon(int playerIndex, Card card) {
		trick.reset();
		initTrick();
	}

	/**
	 * Called from game when a player has played a card.
	 * 
	 * @param playerIndex
	 *            the index of the player playing the card
	 * @param card
	 *            the card being played
	 */
	public final void notifyPlay(int playerIndex, Card card) {
		trick.cardPlayed(card, playerIndex);
		cardPlayed(playerIndex, card);
	}

	/**
	 * Called to notify this player of the current scores. Does nothing.
	 */
	public void notifyScores(String scores) {
	}

	/**
	 * Subclasses should override this method to take advantage of play
	 * information. Default implementation does nothing.
	 */
	protected void cardPlayed(int playerIndex, Card card) {
	}

	/**
	 * Called from the game to inform this player that it is their turn. <br>
	 * Instantly calls cardPlayed() on the CardGame this player is in.
	 */
	public final Card notifyTurn() throws CheaterException {
		Card playedCard = play();
		if (playedCard == null) {
			throw new CheaterException(this, "threw null card");
		}
		else if (removeCard(playedCard)) {
			return playedCard;
		} else {
			throw new CheaterException(this, " threw a card that was not in his hand");
		}
	}

	/**
	 * Remove a card from the player's hand
	 * 
	 * @param card
	 *            the card to be removed.
	 * @return true if the card was in the player's hand
	 */
	private boolean removeCard(Card card) {
		Card[] tmpArray = new Card[hand.length - 1];
		int tmpCount = 0;
		boolean removed = false;
		for (int i = 0, n = hand.length; i < n; i++) {
			if (card.equals(hand[i])) {
				removed = true;
			} else {
				tmpArray[tmpCount++] = hand[i];
			}
		}
		this.hand = tmpArray;
		suits[card.getSuit()]--;
		return removed;
	}

	/**
	 * Get a card to play. The card will be removed from the player's hand.
	 * Subclass' playing strategy should be implemented through this method.
	 */
	protected abstract Card play();

	/**
	 * Accept a hand of cards from the game.
	 * 
	 * @param newHand
	 *            an array of cards to be sent to the client
	 */
	public final void takeHand(Card[] newHand) {
		sortCards(newHand);
		handToString(newHand);
		suits = new int[4];
		this.hand = newHand;
		this.bids = new Bid[0];
		for (int i = 0, n = hand.length; i < n; i++) {
			suits[hand[i].getSuit()]++;
		}
		if (getGame() == null) {
			System.out.println("game is null");
		}
		trick = new Trick(getGame().getNumPlayers());
		initHand();
		initTrick();
	}

	/**
	 * Get this player's hand
	 * 
	 * @return an array of Cards left in the player's hand
	 */
	protected Card[] getHand() {
		return this.hand;
	}

	/**
	 * Subclasses should do any custom initialization between hands here. This
	 * implementation is empty.
	 */
	protected void initHand() {
	}

	/**
	 * Subclasses should do any custom initialization between tricks here. This
	 * implementation is empty.
	 */
	protected void initTrick() {
	}

	/**
	 * Make a bid. <br>
	 * All bidding logic should take place here.
	 * 
	 * @return the player's bid (0 for pass)
	 */
	protected abstract int bid();

	/**
	 * Called from the game to inform this player that it is their bid.
	 * Instantly calls makeBid on the CardGame this player is in.
	 */
	public int notifyBidTurn(Bid[] bids) {
		this.bids = bids;
		return  bid();
		// ((PitchGame) getGame()).makeBid(this, bid());
	}

	/**
	 * Get this player's score
	 */
	protected final int getMyScore() {
		return getGame().getPlayerScore(this);
	}

	/**
	 * Get a particular player's score
	 */
	protected final int getPlayerScore(int index) {
		return getGame().getPlayerScore(index);
	}

	/**
	 * Get an array of the bids placed before this player's turn
	 */
	protected final Bid[] getBids() {
		return this.bids;
	}

	/**
	 * Find out whether this player can legally pass on a bid
	 */
	protected final boolean mayPass() {
		if (bids.length < getGame().getNumPlayers() - 1) {
			return true;
		} else {
			for (int i = 0, n = bids.length; i < n; i++) {
				if (bids[i].getBid() >= 2) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Get the trick, which provides information about the cards played so far.
	 */
	protected Trick getTrick() {
		return this.trick;
	}

	/**
	 * Find out if a card is trump
	 */
	protected boolean isTrump(Card c) {
		return (c.getSuit() == getTrick().getTrump());
	}

	/**
	 * Get an array of all cards in this player's hand of a particular suit.
	 * 
	 * @param suit
	 *            the suit requested
	 * @return an array of cards of the given suit in the player's hand sorted
	 *         from highest to lowest
	 */
	protected Card[] getSuit(int suit) {
		Card[] cards = new Card[suits[suit]];
		int cardCount = 0;
		for (Card c : hand) {
			if (c.getSuit() == suit) {
				cards[cardCount++] = c;
				if (cardCount == cards.length) {
					return cards;
				}
			}
		}
		return cards;
	}

	/**
	 * Get the number of cards in this player's hand of a particular suit
	 * 
	 * @param suit
	 *            the suit whose number of cards is requested.
	 * @return the number of cards in the given suit
	 */
	protected int getSuitCount(int suit) {
		return suits[suit];
	}

	/**
	 * Convenience method for getting all trump cards Same as
	 * getSuit(getTrick().getTrump())
	 * 
	 * @return an array of all the trump cards in this player's hand sorted from
	 *         highest to lowest.
	 */
	protected Card[] getTrump() {
		return getSuit(getTrick().getTrump());
	}

	/**
	 * Find out if this player has no trump left
	 * 
	 * @return true if this player has no trump left in his hand
	 */
	public boolean trumped() {
		return (suits[getTrick().getTrump()] == 0);
	}

	/**
	 * Get the jack of trump if it's in this player's hand
	 * 
	 * @return null if no jack
	 */
	protected Card getJack() {
		if (getSuitCount(getTrick().getTrump()) == 0) {
			return null;
		} else {
			Card[] trump = getTrump();
			for (int i = 0, n = trump.length; i < n; i++) {
				if (trump[i].getValue() == Card.JACK) {
					return trump[i];
				}
			}
			return null;
		}
	}

	/**
	 * Notify this player of the winning bid information
	 */
	public void notifyBidder(Bid winningBid) {
		this.currentBid = winningBid;
	}


	/**
	 * Get the string representation of this player's hand.
	 * @param hand
	 * @return a string of the format "CC, CC, CC"
	 */
	protected String handToString(Card[] hand) {
		StringBuffer cardStr = new StringBuffer();
		for (Card c : hand) {
			if (cardStr.length() > 0) {
				cardStr.append(", ");
			}
			cardStr.append(c.toString());
		}
		return cardStr.toString();
	}
	
	/**
	 * Retrieves a shit card in a particular suit out of the hand
	 */
	protected Card getShit(int suit) {
		Card[] cards = getSuit(suit);
		if (cards.length == 1) {
			return cards[0];
		} else {
			for (int i = cards.length - 1; i >= 0; i--) {
				// don't throw the jack of trump!
				if (suit == getTrick().getTrump()) {
					if (cards[i].getValue() == Card.JACK ) {
						continue;
					}
					else {
						return cards[i];
					}
				}
				// don't throw a ten
				if ((cards[i].getValue() < 8) || (cards[i].getValue() > 8)) {
					return cards[i];
				}
			}
			return cards[0];
		}
	}

	/**
	 * Get a shit card.
	 * 
	 * @param trumpOk
	 *            whether or not it's ok to throw trump
	 * @return a shit card
	 */
	public Card getShit(boolean trumpOk) {
		for (int i = 0; i < 4; i++) {
			if ((getSuitCount(i) == 0)
					|| (!trumpOk && (i == getTrick().getTrump()))) {
				continue;
			}
			Card[] suit = getSuit(i);
			for (int j = suit.length - 1; j >= 0; j--) {
				if (suit[j].getValue() < 8) {
					return suit[j];
				}
			}
		}
		return getHand()[0];
	}

	public final User getUser() {
		return this.user;
	}
	
	public final String getUsername() {
		return user.getUsername();
	}
	
	/**
	 * Notify this player that the game session has ended.
	 */
	@Override
	public void gameEnded() {
		setGame(null);
	}

	
	
}