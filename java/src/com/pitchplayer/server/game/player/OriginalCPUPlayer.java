package com.pitchplayer.server.game.player;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.Bid;
import com.pitchplayer.server.game.PitchGame;
import com.pitchplayer.server.game.Trick;
import com.pitchplayer.userprofiling.om.User;

/**
 * Basic CPU player
 * @author robd
 *
 */
public class OriginalCPUPlayer extends CPUPlayer {

	public static final int ORIGINAL_USER_ID = 3;

	public OriginalCPUPlayer(User user) {
		super(user);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Bidding logic. Only bids two if he's forced to.
	 */
	protected int bid() {
		if (mayPass()) {
			return Bid.PASS;
		} else {
			return 2;
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
			// my lead
			if (trick.getTrickCount() == 0) {
				// 1st trick, choose a trump suit
				return leadHand();
			} else {
				if (trumped()) {
					// trumped. no worries about jack protection/stealing
					for (int i = hand.length - 1; i >= 0; i--) {
						if (hand[i].getValue() != 8) {
							return hand[i];
						}
					}
					return hand[playIndex];
				} else {
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
					} else {
						// if i only have the jack of trump, throw something
						// else
						if (holdingJack()) {
							for (int i = hand.length - 1; i >= 0; i--) {
								if ((hand[i].getValue() != 8)
										&& (hand[i].getSuit() != trump)) {
									return hand[i];
								}
							}
							//if i have to throw a 10, so be it
							for (int i = 0; i < hand.length; i++) {
								if (hand[i].getSuit() != trump) {
									playIndex = i;
								}
							}
							return hand[playIndex];
						} else {
							// i don't have the jack.
							// try to refrain from throwing trump or a 10
							for (int i = hand.length - 1; i >= 0; i--) {
								if ((hand[i].getValue() != 8)
										&& (hand[i].getSuit() != trump)) {
									return hand[i];
								}
							}
							//try not to throw a 10
							for (int i = 0; i < hand.length; i++) {
								if (hand[i].getValue() != 8) {
									playIndex = i;
								}
							}
							return hand[playIndex];
						}
					}
				}
			}
		} else {
			// someone else lead

			// get the suit which was lead
			int leadSuit = trick.getLeadSuit();

			// if there's nothing i can do, throw shit:
			if ((getSuitCount(leadSuit) == 0) && trumped()) {
				for (int i = hand.length - 1; i >= 0; i--) {
					if (hand[i].getValue() < 8)
						return hand[i];
				}
				for (int i = hand.length - 1; i >= 0; i--) {
					if (hand[i].getValue() > 8)
						return hand[i];
				}
				// tried to avoid throwing a 10, but couldn't
				return hand[playIndex];
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

			// if there are 10 or more game points out, try to take the trick
			if (trick.getGamePoints() >= 10) {
				if (isTrump(winningCard) && !trumped()) {
					Card[] trumpCards = getTrump();
					for (int i = trumpCards.length - 1; i >= 0; i--) {
						if ((((PitchGame) getGame()).getJackWinner() < 0)
								&& (trumpCards[i].getValue() >= Card.JACK)) {
							// don't waste a card that can take the jack,
							// if it's not out yet
							break;
						} else if (trumpCards[i].getValue() > winningCard
								.getValue()) {
							return trumpCards[i];
						}
					}
					// couldn't out-trump it. throw shit:
					if (getSuitCount(leadSuit) > 0) {
						return getShit(leadSuit);
					} else {
						return getShit();
					}
				} else if (trumped()) {
					// i can't take the trick, throw shit
					return getShit(leadSuit);
				} else {
					// trump that bad boy
					return getTrump()[getSuitCount(trump) - 1];
				}
			} else {
				// throw shit
				if (getSuitCount(leadSuit) > 0) {
					return getShit(leadSuit);
				} else {
					// throw shit in any suit
					return getShit();
				}
			}
		}
	}

	/**
	 * Get a shit card in any suit.
	 */
	public Card getShit() {
		for (int i = 0; i < 4; i++) {
			Card[] suit = getSuit(i);
			if (suit.length > 0) {
				for (int j = suit.length - 1; j >= 0; j--) {
					if (suit[j].getValue() < 8) {
						return suit[j];
					}
				}
			}
		}
		return getHand()[0];
	}

	/**
	 * Pick a trump suit and get the lead card Picks a trump suit based on the
	 * difference b/w the highest and lowest cards in a suit times the number of
	 * cards in the suit.
	 */
	private Card leadHand() {
		int trumpSuit = -1;
		int trumpRating = 0;
		for (int i = 0; i < 4; i++) {
			Card[] suit = getSuit(i);
			if (suit.length > 1) {
				int diff = suit[0].getValue()
						- suit[suit.length - 1].getValue();
				int iRating = diff * suit.length;
				if (iRating > trumpRating) {
					trumpSuit = i;
					trumpRating = iRating;
				} else if ((iRating == trumpRating)
						&& (suit.length < getSuitCount(trumpSuit))) {
					trumpSuit = i;
					trumpRating = iRating;
				}
			}
		}

		Card[] trump = getSuit(trumpSuit);
		if (trump[0].getValue() != Card.JACK) {
			return trump[0];
		} else {
			return trump[trump.length - 1];
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