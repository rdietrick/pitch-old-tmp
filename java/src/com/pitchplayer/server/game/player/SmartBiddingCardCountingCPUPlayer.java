package com.pitchplayer.server.game.player;

import org.apache.log4j.Logger;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.Bid;
import com.pitchplayer.server.game.PitchGame;
import com.pitchplayer.server.game.Trick;
import com.pitchplayer.userprofiling.om.User;

/**
 * A computer player
 */
public class SmartBiddingCardCountingCPUPlayer extends CPUPlayer {

	public SmartBiddingCardCountingCPUPlayer(User user) {
		super(user);
	}

	Logger log = Logger.getLogger(this.getClass().getName());
	
	ProbabilityCalculator probCalc = new ProbabilityCalculatorImpl();



	// the suit this player bid in
	int bidSuit = 0;

	// matrix for players and what suits they have
	boolean[][] playerSuits;

	// matrix for players and whether they are possibly protecting a 10 in a
	// suit
	boolean[][] protecting;

	// the player with the lead
	int leadPlayer = 0;

	protected void initHand() {
		this.bidSuit = 0;
		// defaults all cells to false, so we have to negate the value all the time
		playerSuits = new boolean[getGame().getNumPlayers()][4];
		// defaults all cells to false
		protecting = new boolean[getGame().getNumPlayers()][4];
	}

	/**
	 * Bidding logic.
	 */
	protected int bid() {
		int myBid = Bid.PASS;
		Bid highBid = ((PitchGame) getGame()).getHighBid();
		if (highBid == null) {
			myBid = evaluateBid(Bid.PASS);
		} else {
			myBid = evaluateBid(highBid.getBid());
		}
		if ((myBid == Bid.PASS) && mayPass()) {  // if i should pass and i can, do it
			return Bid.PASS;
		} else if (!mayPass()) {  // force bid: bid 2
			return 2;
		} else { // bid what i think i can make
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
							// throw shit or trump
							return getShitOrTrump();
						}
						// others may not all be trumped
						else {
							// i have more than one trump: clear the way for
							// the jack
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

			// clear the Jack if i can:
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
			// CHANGE! if the person who lead is the bidder, they're probably trying to
			// clear the way for the jack; don't beat it if you have > JACK
			if ((leadSuit == trump) && !jackScored && !willSuitClear(trump)) {
				Card[] trumpCards = getTrump();
				if (trumpCards[0].getValue() > Card.JACK) {
					return trumpCards[0];
				}
			}

			
			// if there are 10 or more game points out, try to take the trick
			if (trick.getGamePoints() >= 7) {
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
					// if i got here, i couldn't safely trump the trick to win it
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
	 * Get a shit card. Tries to get a non-10 first.
	 * 
	 * @param trumpOk
	 *            whether or not it's OK to throw trump
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
		return play;
	}

	/**
	 * Choose the card to lead with in the first trick (my bid). 
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
			Card[] suitCards = getSuit(i); // get the array of cards in the current suit
			float highProb = probCalc.getProbHigh(suitCards[0], getGame().getHandSize(), getGame().getNumPlayers()); 
			suitPoints[i] += highProb;
			float lowProb = probCalc.getProbLow(suitCards[suitCards.length - 1], getGame().getHandSize(), getGame().getNumPlayers());
			suitPoints[i] += lowProb;
			// log.debug("high/low probabilities: " + suit[0] + "/" + highProb + ", " + suit[suit.length-1] + "/" + lowProb);
			float jackTakers = 0f;
			boolean holdingJack = false;
			for (Card c : suitCards) {
				// do i have the jack?
				if (c.getValue() == Card.JACK) {
					// this might be a little too conservative
					if (suitCards.length > 3) { // if i have 4 cards in this suit, i can (almost) definitely make the jack
						suitPoints[i] += 1;
						holdingJack = true;
					}
					else {
						// estimate the odds of making the jack as: number of trump cards / (number of players +1)
						suitPoints[i] += (suitCards.length-1) / (float)getGame().getNumPlayers();
					}
				}
				else {
					if (c.getValue() > Card.JACK) {
						jackTakers++;
					}
				}
			}
			if (!holdingJack && jackTakers > 0) {
				float jackProb = probCalc.getProbabilityCardsDealt(1, getGame().getHandSize(), getGame().getNumPlayers());
				float stealProb = jackProb * (jackTakers/4.75f);
				suitPoints[i] += stealProb;
			}

			// three or more trump gives us a good shot at game
			// TODO: need to take into consideration what i have in other suits 
			// and how many players are in the game
			if (suitCards.length >= 4) {
				suitPoints[i] += 1;
			}
			else if (suitCards.length == 2) {
				suitPoints[i] += .33;
			} else if (suitCards.length == 3) {
				suitPoints[i] += .67;
			}

			if (suitPoints[i] > suitPoints[bidSuit]) {
				bidSuit = i;
				bid = (int) Math.round(suitPoints[i]);
			} else if (suitPoints[i] == suitPoints[bidSuit]) {
				// same potential bid in two suits
				// first check the # of trump in the two suits
				if (getSuitCount(i) > getSuitCount(bidSuit)) {
					bidSuit = i;
					bid = (int) Math.floor(suitPoints[i]);
				}
				// second check the highest card in the two suits
				else if (getSuit(i)[0].getValue() > getSuit(bidSuit)[0].getValue()) {
					bidSuit = i;
					bid = (int) Math.floor(suitPoints[i]);
				}
			}
		}
		this.bidSuit = bidSuit;
		if ((bid > 1) && (bid > goingBid)) {
			StringBuffer handStr = new StringBuffer();
			for (Card c : getSuit(bidSuit)) {
				if (handStr.length() != 0) {
					handStr.append(",");
				}
				handStr.append(c.toString());
			}
//			log.debug("Bidding " + bid + " (" + suitPoints[bidSuit] + ") with hand=" + handStr.toString() + (!mayPass()?" force bid":""));
			
			// check to make sure I shouldn't try to force the dealer to bid
			int dealerScore = getGame().getPlayerScore(getGame().getDealerIndex());
			int myScore = getGame().getPlayerScore(getIndex());

			if (dealerScore < 3 && myScore > 4) {
				if (myScore - bid > 3) {
					return Bid.PASS;
				}
				else {
					return bid;
				}
			}
			return bid;
		} else {
			return Bid.PASS;
		}
	}

	/**
	 * Find out if this player is holding the Jack
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
	 * Get the indexes of the players throwing after me.
	 * Assumes it is currently this player's turn
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