package com.pitchplayer.server.game.player;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.Bid;
import com.pitchplayer.server.game.PitchGame;
import com.pitchplayer.server.game.Trick;
import com.pitchplayer.userprofiling.om.User;

/**
 * A computer player
 */
public class SmartAssCPUPlayer extends CPUPlayer {

	// the suit this player bid in
	int bidSuit = 0;

	// matrix for players and what suits they have
	boolean[][] playerSuits;

	// the player with the lead
	int leadPlayer = 0;

	public SmartAssCPUPlayer(User user) {
		super(user);
		// TODO Auto-generated constructor stub
	}

	/**
	 *  
	 */
	protected void initHand() {
		this.bidSuit = 0;
		// create a matrix of players & suits
		// defaults all cells to false, so we have to ! the value all the time
		playerSuits = new boolean[getGame().getNumPlayers()][4];
	}

	/**
	 * Bidding logic. Only bids two if he's forced to.
	 */
	protected int bid() {
		int myBid = Bid.PASS;
		Bid highBid = ((PitchGame) getGame()).getHighBid();
		if (highBid == null) {
			myBid = evaluateBid(Bid.PASS);
		} else {
			myBid = evaluateBid(highBid.getBid());
		}
		if ((myBid == Bid.PASS) && mayPass()) {
			return Bid.PASS;
		} else if (!mayPass()) {
			return 2;
		} else {
			if (myBid == 3) {
				say("Look out!");
			} else if (myBid == 4) {
				say("Step back!");
			}
			return myBid;
		}
	}

	/**
	 * Note if a player is trumped, etc.
	 */
	protected void cardPlayed(int index, Card card) {
		Trick trick = getTrick();
		if (trick.getPlayCount() == 1) {
			leadPlayer = index;
			return;
		}
		if ((card.getSuit() != trick.getLeadSuit())
				&& (card.getSuit() != trick.getTrump())) {
			playerSuits[index][card.getSuit()] = true;
		}
	}

	/**
	 * Find out if a player possibly has a card in a suit.
	 */
	protected boolean hasSuit(int playerIndex, int suit) {
		return !playerSuits[playerIndex][suit];
	}

	/**
	 * Playing logic
	 */
	protected Card play() {
		Trick trick = getTrick();
		Card[] hand = getHand();
		int trump = trick.getTrump();

		// the index of the card to eventually return
		int playIndex = 0;

		// if it's the last trick, just throw the card
		if (hand.length == 1) {
			return hand[0];
		}

		if (trick.getPlayCount() == 0) {
			/*******************************************************************
			 * ******************** MY LEAD **************************
			 ******************************************************************/
			if (trick.getTrickCount() == 0) {
				// 1st trick, choose a trump suit and lead
				return leadHand();
			} else {
				if (trumped()) {
					// trumped. no worries about jack protection/stealing
					return getShit(true);
				}
				/***************************************************************
				 * *************** begin not trumped ****************
				 **************************************************************/
				else {
					// if i have the jack & at least one more & !(all trumped),
					// try to clear the jack
					if (holdingJack() && (getSuitCount(trump) > 1)
							&& !allTrumped()) {
						Card[] trumpCards = getTrump();
						for (int i = trumpCards.length - 1; i >= 0; i--) {
							if (trumpCards[i].getValue() != Card.JACK) {
								playIndex = i;
								break;
							}
						}
						return trumpCards[playIndex];
					} else if (holdingJack()) { // either they're not all
												// trumped or i only have one
												// trump
						// if i only have the jack of trump, throw something
						// else to get rid of the lead
						if (getTrump().length == hand.length) {
							return getShit(true);
						} else {
							return getShit(false);
						}
					} else {
						// try to take the jack if it's not been thrown yet
						Card[] trumpCards = getTrump();
						if ((((PitchGame) getGame()).getJackWinner() < 0)
								&& (trumpCards[0].getValue() > Card.JACK)) {
							say("Cough it up!");
							return trumpCards[0];
						}

						// i don't have the jack, get rid of the lead:
						// get rid of the lead:
						if (getTrump().length == hand.length) {
							return getShit(true);
						} else {
							return getShit(false);
						}
					}
				}
				/***************************************************************
				 * *************** end not trumped ******************
				 **************************************************************/
			}
			/*******************************************************************
			 * ******************* END MY LEAD ***********************
			 ******************************************************************/
		} else {
			/*******************************************************************
			 * **************** SOMEONE ELSE LEAD ********************
			 ******************************************************************/
			Card winningCard = trick.getWinningCard();
			
			// clear the johnson if i can:
			if (!(winningCard.getSuit() == trick.getTrump() && winningCard.getValue() > Card.JACK)
					&& holdingJack() 
					&&willSuitClear(trump) ) {
				Card[] trumpCards = getTrump();
				for (int i = 0, n = trumpCards.length; i < n; i++) {
					if (Card.JACK == trumpCards[i].getValue()) {
						say("How you like me now?");
						return trumpCards[i];
					}
				}
			}

			// get the suit which was lead
			int leadSuit = trick.getLeadSuit();

			// if there's nothing i can do, throw shit:
			if ((getSuitCount(leadSuit) == 0) && trumped()) {
				return getShit(true);
			}

			// always play for the jack
			if (trick.getJackOut() && !trumped()) {
				Card[] trumpCards = getTrump();
				if (trumpCards[0].getValue() > Card.JACK) {
					say("I'll take that.");
					return trumpCards[0];
				}
			}

			// find out if the jack was already scored
			boolean jackScored = (((PitchGame) getGame()).getJackWinner() > -1);

			// if there are 10 or more game points out, try to take the trick
			if (trick.getGamePoints() >= 10) {
				/***************************************************************
				 * ************** BEGIN WANT GAME POINTS *******************
				 **************************************************************/
				if (isTrump(winningCard) && !trumped()) {
					// need trump to win trick
					Card[] trumpCards = getTrump();
					for (int i = trumpCards.length - 1; i >= 0; i--) {
						if (!jackScored
								&& (trumpCards[i].getValue() >= Card.JACK)) {
							// don't waste a card that can take the jack,
							// if it's not out yet
							break;
						} else if (trumpCards[i].getValue() > winningCard
								.getValue()) {
							return trumpCards[i];
						}
					}
					// if i got here, i couldn't safely trump the trick to win
					// it
					if (getSuitCount(leadSuit) > 0) {
						// i have the lead suit
						if (trumped()) {
							// couldn't out-trump it. throw shit:
							return getShit(leadSuit);
						} else {
							// i have trump, but don't want to throw it unless i
							// have to
							Card c = getShit(leadSuit);
							if (c.getValue() == 8) {
								// rather throw trump than a ten
								return getShit(trump);
							} else {
								return c;
							}
						}
					} else {
						// i don't have the suit that was lead, throw whatever
						say("I'll tell you what: you can have it.");
						return getShit(true);
					}
					// end need trump to beat it
				} else {
					if (getSuitCount(leadSuit) > 0) {
						// if i can beat it in the same suit, do it:
						Card[] suitCards = getSuit(leadSuit);
						if (suitCards[0].getValue() > winningCard.getValue()) {
							return suitCards[0];
						}
					}

					if (trumped()) {
						// i can't beat it, throw whatever
						return getShit(true);
					} else { // trump that bad boy?
						Card[] trumpCards = getTrump();
						if (jackScored) {
							// if the jack's already been scored, drop whatever
							return trumpCards[0];
						} else {
							if (trumpCards[0].getValue() >= Card.JACK) {
								// throw something higher than a 10?
								if (trumpCards[trumpCards.length - 1]
										.getValue() < Card.JACK) {
									// if i have something lower, throw it
									return trumpCards[trumpCards.length - 1];
								} else if (getSuitCount(leadSuit) > 0) {
									return getShit(leadSuit);
								} else if (trumpCards.length == hand.length) {
									// all i have is trump, throw highest one
									return trumpCards[0];
								} else {
									return getShit(false);
								}
							} else {
								return trumpCards[0];
							}
						}
					}
				}
				/***************************************************************
				 * *************** END WANT GAME POINTS ********************
				 **************************************************************/
			} else {
				// throw shit
				if (getSuitCount(leadSuit) > 0) {
					return getShit(leadSuit);
				} else {
					// throw shit in any suit
					return getShit(true);
				}
			}
			/*******************************************************************
			 * *************** END SOMEONE ELSE LEAD *****************
			 ******************************************************************/
		}
	}

	/**
	 * Get a shit card. Tries to get a non-10, first.
	 * 
	 * @param trumpOk
	 *            whether or not it's ok to throw trump
	 * @return a shit card
	 */
	public Card getShit(boolean trumpOk) {
		Card play = null;
		int trump = getTrick().getTrump();

		Card[] hand = getHand();
		for (int i = hand.length - 1; i >= 0; i--) {
			if (hand[i].getSuit() == trump) {
				if (!trumpOk || (hand[i].getValue() == Card.JACK)) {
					continue;
				}
			}
			if (play == null) {
				play = hand[i];
			} else if ((hand[i].getValue() < play.getValue())
					&& (hand[i].getValue() != 8)) {
				play = hand[i];
			} else if ((play.getValue() == 8) && (hand[i].getValue() != 8)) {
				play = hand[i];
			}
		}
		if (play == null) {
			System.out.println("returning null from getShit(" + trumpOk + ")");
		}
		return play;
	}

	/**
	 * Pick a trump suit and get the lead card Picks a trump suit based on the
	 * difference b/w the highest and lowest cards in a suit multiplied by the
	 * number of cards in the suit.
	 */
	private Card leadHand() {
		Card[] trump = getSuit(bidSuit);
		if (trump[0].getValue() > Card.JACK) {
			return trump[0];
		} else {
			return trump[trump.length - 1];
		}
	}

	/**
	 * Evaluate whether or not this player should bid
	 * 
	 * @return 0 for pass or the amount the player should bid
	 */
	protected int evaluateBid(int goingBid) {
		int[] suitPoints = new int[4];
		// loop through each suit, and figure out how many
		// potential points the player is holding
		int bidSuit = 0;
		int bid = 0;
		for (int i = 0; i < 4; i++) {
			suitPoints[i] = 0;
			// if i don't have any cards in this suit, continue
			if (getSuitCount(i) == 0) {
				continue;
			}
			Card[] suit = getSuit(i);
			// queen or higher
			if (suit[0].getValue() > 10) {
				suitPoints[i]++;
			}
			// 3 or lower
			if (suit[suit.length - 1].getValue() < 2) {
				suitPoints[i]++;
			}
			// have the jack
			for (int j = 0, n = suit.length; j < n; j++) {
				if (suit[j].getValue() == Card.JACK) {
					suitPoints[i]++;
				}
			}
			// three or more trump gives us a good shot at game
			if (getSuitCount(i) > 2) {
				suitPoints[i]++;
			}

			if (suitPoints[i] > suitPoints[bidSuit]) {
				bidSuit = i;
				bid = suitPoints[i];
			} else if (suitPoints[i] == suitPoints[bidSuit]) {
				if (getSuitCount(i) > getSuitCount(bidSuit)) {
					bidSuit = i;
					bid = suitPoints[i];
				}
			}
		}
		this.bidSuit = bidSuit;
		if ((bid > 1) && (bid > goingBid)) {
			return bid;
		} else {
			return Bid.PASS;
		}
	}

	/**
	 * Find out if this player is holding the infamous Johnson
	 */
	private boolean holdingJack() {
		if (trumped()) {
			return false;
		}
		Card[] trump = getTrump();
		for (int i = 0, n = trump.length; i < n; i++) {
			if (trump[i].getValue() == Card.JACK) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the indexes of the players throwing after me Assumes it is currently
	 * this player's turn
	 */
	protected int[] getPlayersAfterMe() {
		int numPlayers = getGame().getNumPlayers();
		int cardsPlayed = getTrick().getPlayCount();
		int[] players = new int[(numPlayers - cardsPlayed) - 1];
		int turn = leadPlayer + cardsPlayed + 1;
		for (int i = 0, n = players.length; i < n; i++) {
			players[i] = turn++ % numPlayers;
		}
		return players;
	}

	/**
	 * Find out if the players after me can beat a particular card.
	 * This method doesn't check the card against the cards already thrown (if any).
	 */
	protected boolean willSuitClear(int suit) {
		int[] players = getPlayersAfterMe();
		if (players.length == 0) {
			return true;
		}
		int trump = getTrick().getTrump();

		if (suit == trump) {
			for (int i = 0, n = players.length; i < n; i++) {
				if (hasSuit(i, suit)) {
					return false;
				}
			}
		} else {
			for (int i = 0, n = players.length; i < n; i++) {
				if (hasSuit(i, suit) || hasSuit(i, trump)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Find out if all other players are trumped.
	 */
	protected boolean allTrumped() {
		int numPlayers = getGame().getNumPlayers();
		int myIndex = this.getIndex();
		int trumpSuit = getTrick().getTrump();
		for (int i = 0; i < numPlayers; i++) {
			if ((i != myIndex) && hasSuit(i, trumpSuit)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Accept a message from someone another player. Called from a CardGame.
	 * 
	 * @param player
	 *            the name of the player sending the message
	 * @param quote
	 *            the message
	 */
	public void sendQuote(String player, String quote) {
		if (player.equals(getUsername())) {
			return;
		}
		if (containsBadWords(quote)) {
			say("Watch the language, please.");
		}
	}

	public boolean containsBadWords(String msg) {
		for (int i = 0; i < BAD_WORDS.length; i++) {
			if (msg.toUpperCase().indexOf(BAD_WORDS[i]) > -1) {
				return true;
			}
		}
		return false;
	}

	static final String[] BAD_WORDS = { "SHIT", "FUCK", "DICK", "COCK", "CUNT" };

	/**
	 * Say something to the rest of the players
	 */
	protected void say(String msg) {
		getGame().say(getUsername(), msg);
	}

}