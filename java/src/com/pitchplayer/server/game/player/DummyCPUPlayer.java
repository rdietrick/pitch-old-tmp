package com.pitchplayer.server.game.player;

import org.apache.log4j.Logger;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.Bid;
import com.pitchplayer.server.game.PitchGame;
import com.pitchplayer.server.game.Trick;
import com.pitchplayer.userprofiling.om.User;

public class DummyCPUPlayer extends CPUPlayer {

	public DummyCPUPlayer(User user) {
		super(user);
	}

	protected Logger log = Logger.getLogger(this.getClass().getName());


	int bidSuit = 0;

	protected void initHand() {
		this.bidSuit = 0;
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
					// if i have the jack & one more, try to clear the jack
					if (holdingJack() && (getSuitCount(trump) > 1)) {
						Card[] trumpCards = getTrump();
						for (int i = trumpCards.length - 1; i >= 0; i--) {
							if (trumpCards[i].getValue() != Card.JACK) {
								playIndex = i;
								break;
							}
						}
						return trumpCards[playIndex];
					} else if (holdingJack()) {
						// if i only have the jack of trump, throw something
						// else to get rid of the lead
						return getShit(false);
					} else {
						// try to take the jack if it's not been thrown yet
						Card[] trumpCards = getTrump();
						if ((((PitchGame) getGame()).getJackWinner() < 0)
								&& (trumpCards[0].getValue() > Card.JACK)) {
							return trumpCards[0];
						}
						// i don't have the jack, get rid of the lead:
						return getShit(false);
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

			// clear the johnson if i can:
			if ((trick.getPlayCount() == getGame().getNumPlayers() - 1)
					&& holdingJack() &&
					!(trick.getWinningCard().getSuit() == trick.getTrump() &&
							trick.getWinningCard().getValue() > Card.JACK)
					) {
				Card[] trumpCards = getTrump();
				for (int i = 0; i < trumpCards.length; i++) {
					if (Card.JACK == trumpCards[i].getValue()) {
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
					return trumpCards[0];
				}
			}

			// get the card currently winning the trick
			Card winningCard = trick.getWinningCard();

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
			for (int j = 0; j < suit.length; j++) {
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
		for (int i = 0; i < trump.length; i++) {
			if (trump[i].getValue() == Card.JACK) {
				return true;
			}
		}
		return false;
	}

}