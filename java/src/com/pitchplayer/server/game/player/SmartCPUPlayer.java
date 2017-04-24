package com.pitchplayer.server.game.player;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.Bid;
import com.pitchplayer.server.game.PitchGame;
import com.pitchplayer.server.game.Trick;
import com.pitchplayer.userprofiling.om.User;

/**
 * A computer player
 */
public class SmartCPUPlayer extends CPUPlayer {

	// the suit this player bid in
	int bidSuit = 0;

	// matrix for players and what suits they have
	boolean[][] playerSuits;

	// matrix for players and whether they are possibly protecting a 10 in a
	// suit
	boolean[][] protecting;

	// the player with the lead
	int leadPlayer = 0;

	public SmartCPUPlayer(User user) {
		super(user);
		// TODO Auto-generated constructor stub
	}

	protected void initHand() {
		this.bidSuit = 0;
		// defaults all cells to false, so we have to ! the value all the time
		playerSuits = new boolean[getGame().getNumPlayers()][4];
		// defaults all cells to false
		protecting = new boolean[getGame().getNumPlayers()][4];
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
			return myBid;
		}
	}

	/**
	 * Find out if a player is trumped, etc.
	 */
	protected void cardPlayed(int index, Card card) {
		Trick trick = getTrick();
		if (trick.getPlayCount() == 1) {
			leadPlayer = index;
			return;
		}
		if (card.getSuit() != trick.getLeadSuit()) { // player didn't follow
													 // suit
			if (card.getSuit() != trick.getTrump()) {
				setPlayerOutOfSuit(index, card.getSuit()); // player doesn't
														   // have this suit
			} else {
				setPlayerProtecting(index, card.getSuit()); // he's possibly
															// protecting
															// something
			}
		}
	}

	/**
	 * Find out if a player possibly has a card in a suit.
	 */
	protected boolean hasSuit(int playerIndex, int suit) {
		return !playerSuits[playerIndex][suit];
	}

	/**
	 * Note that a player is out of a particular suit
	 */
	protected void setPlayerOutOfSuit(int playerIndex, int suit) {
		playerSuits[playerIndex][suit] = true;
		protecting[index][suit] = false;
	}

	/**
	 * Find out if a player is protecting a card in the given suit
	 */
	protected boolean isProtecting(int playerIndex, int suit) {
		return protecting[playerIndex][suit];
	}

	/**
	 * Note that a player may be protecting something valuable
	 */
	protected void setPlayerProtecting(int playerIndex, int suit) {
		protecting[index][suit] = true;
	}

	/**
	 * Find out if any of the players throwing after me are likely to be
	 * protecting a 10 in a particular suit.
	 * 
	 * @return the index of a player who might be protecting a 10 in the suit;
	 *         or -1
	 */
	protected int getProtecingIndex(int suit) {
		int[] players = getPlayersAfterMe();
		for (int i = 0; i < players.length; i++) {
			if (isProtecting(players[i], suit)) {
				return players[i];
			}
		}
		return -1;
	}

	/**
	 * Try to take the lead, if possible
	 * 
	 * @return whether or not 
	 */
	protected Card takeLead() {
		return null;
	}
	
	
	/**
	 * Playing logic
	 */
	protected Card play() {
		Trick trick = getTrick();
		Card[] hand = getHand();
		int trump = trick.getTrump();

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
					// I HAVE THE JACK
					if (holdingJack()) {
						// others are all trumped
						if (allTrumped()) {
							// if all i have is trump, just throw one
							if (getSuitCount(trump) == hand.length) {
								return hand[0];
							}
							// regardless of the # of trump i have, i want to
							// throw
							// shit or trump
							return getShitOrTrump();
						}
						// others may not all be trumped
						else {
							// i have more than one trump: clear the way for
							// johnson
							if (getSuitCount(trump) > 1) {
								Card[] trumpCards = getTrump();
								for (int i = trumpCards.length - 1; i >= 0; i--) {
									if (trumpCards[i].getValue() != Card.JACK) {
										playIndex = i;
										break;
									}
								}
								return trumpCards[playIndex];
							}
							// i only have one trump
							else {
								return getShit(false);
							}
						}
					}
					// I DON'T HAVE THE JACK
					else {
						// others are all trumped
						if (allTrumped()) {
							// if all i have is trump, just throw one
							if (getSuitCount(trump) == hand.length) {
								return hand[0];
							}
							return getShitOrTrump();
						}
						// others may not all be trumped
						else {
							// if jack is not out yet, try to steal it
							if (!isJackScored()) {
								Card[] trumpCards = getSuit(trump);
								if (trumpCards[0].getValue() > Card.JACK) {
									return trumpCards[0];
								}
							}
							// all i have is trump
							if (getSuitCount(trump) == hand.length) {
								return getShit(true);
							}
							// get rid of the lead or throw shit
							else {
								return getShitOrTrump();
							}
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
			// get the card currently winning the trick
			Card winningCard = trick.getWinningCard();

			// clear the johnson if i can:
			if (!(winningCard.getSuit() == trick.getTrump() && winningCard.getValue() > Card.JACK)
					&& holdingJack() 
					&&willSuitClear(trump) ) {
				Card[] trumpCards = getTrump();
				for (int i = 0, n = trumpCards.length; i < n; i++) {
					if (Card.JACK == trumpCards[i].getValue()) {
						return trumpCards[i];
					}
				}
			}

			// get the suit which was lead
			int leadSuit = trick.getLeadSuit();

			// if there's nothing i can do, throw shit:
			if ((getSuitCount(leadSuit) == 0) && trumped()) {
				return getShit(false);
			}

			// always play for the jack
			if (trick.getJackOut() && !trumped()) {
				Card[] trumpCards = getTrump();
				if (trumpCards[0].getValue() > Card.JACK) {
					return trumpCards[0];
				}
			}

			boolean jackScored = isJackScored();

			// trump was lead, the jack's not been played yet, and not everyone
			// after me is trumped, throw something > JACK if i have it
			// CHANGE! if the person who lead is the bidder, they're trying to
			// clear
			// the way for the jack; don't beat it if you have > JACK
			if ((leadSuit == trump) && !jackScored && !willSuitClear(trump)) {
				Card[] trumpCards = getTrump();
				if (trumpCards[0].getValue() > Card.JACK) {
					return trumpCards[0];
				}
			}

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

	public Card getShitOrTrump() {
		Card c = getShit(false);
		if (c == null || c.getValue() == 8) {
			return getShit(true);
		}
		return c;
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
			if (!trumpOk && hand[i].getSuit() == trump) {
				continue;
			} else if (play == null) {
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
		float[] suitPoints = new float[4];
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
			// queen or higher is worth the following values for High
			// A=1,K=.67,Q=.33
			int highValue = suit[0].getValue();
			if (highValue == Card.ACE) {
				suitPoints[i] += 1;
			} else if (highValue == Card.KING) {
				suitPoints[i] += .67;
			} else if (highValue == Card.QUEEN) {
				suitPoints[i] += .33;
			}
			int lowValue = suit[suit.length - 1].getValue();
			// 3 or lower is worth the following values for Low
			// 2=1, 3=.67,4=.33
			if (lowValue == 0) {
				suitPoints[i] += 1;
			} else if (lowValue == 1) {
				suitPoints[i] += .67;
			} else if (lowValue == 2) {
				suitPoints[i] += .33;
			}
			// have the jack
			for (int j = 0, n = suit.length; j < n; j++) {
				if (suit[j].getValue() == Card.JACK) {
					suitPoints[i] += 1;
				}
			}
			// three or more trump gives us a good shot at game
			int numTrump = getSuitCount(i);
			if (numTrump == 2) {
				suitPoints[i] += .33;
			} else if (numTrump == 3) {
				suitPoints[i] += .67;
			} else if (numTrump == 4) {
				suitPoints[i] += 1;
			}

			if (suitPoints[i] > suitPoints[bidSuit]) {
				bidSuit = i;
				bid = (int) Math.floor(suitPoints[i]);
			} else if (suitPoints[i] == suitPoints[bidSuit]) {
				if (getSuitCount(i) > getSuitCount(bidSuit)) {
					bidSuit = i;
					bid = (int) Math.floor(suitPoints[i]);
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
	 */
	protected boolean willSuitClear(int suit) {
		int[] players = getPlayersAfterMe();
		if (players.length == 0) {
			return true;
		}
		int trump = getTrick().getTrump();

		for (int i = 0, n = players.length; i < n; i++) {
			if (hasSuit(i, suit) || (suit != trump && hasSuit(i, trump))) {
				return false;
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

	protected boolean isJackScored() {
		return (((PitchGame) getGame()).getJackWinner() > -1);
	}

}