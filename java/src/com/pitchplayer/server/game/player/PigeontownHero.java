package com.pitchplayer.server.game.player;

import java.util.ArrayList;
import java.util.Random;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.Bid;
import com.pitchplayer.server.game.PitchGame;
import com.pitchplayer.userprofiling.om.User;

public class PigeontownHero extends CPUPlayer {
	private static org.apache.log4j.Logger Log = org.apache.log4j.LogManager.getLogger(PigeontownHero.class.getName());

	private static Random _sRandom = new Random();
	private SuitSet[] _sortedCards = new SuitSet[4];
	private Bid _myBid = null;
	private int _myBidSuit = -1;
	
	private class SuitSet {
		public SuitSet(Random r, Card[] cards) {
			_cards = cards;
			HaveJack = false;					
			
			for (Card c : cards) {								
				GamePoints += c.getGamePoints();
				switch (c.getValue()) {
					case Card.ACE:
						SurePoints++;
						NumFaceCards++;
						break;
					case Card.KING:
						NumFaceCards++;
						PossiblePoints++;
						break;
					case Card.QUEEN:
						NumFaceCards++;
						break;
					case Card.JACK:
						NumFaceCards++;						
						HaveJack = true;
						break;
					case 0:
						SurePoints++;
						break;
					case 1:
						PossiblePoints++;
						break;
					case 8: // ???
						NumTens++;
						break;
					default:
						break;
				}
			}
		}
		
		
		public ArrayList<Card> Cards;
		
		public int SurePoints;
		public int GamePoints;
		public int NumTens;
		public int NumFaceCards;
		public boolean HaveJack;
		public int PossiblePoints;
		
		public boolean hasHighProbabilityForGame() {
			return NumFaceCards > 2;		
		}
		
		public boolean isLikelyToCaptureJack(Random r) {						
			if (!HaveJack) {				
				return NumFaceCards > 1 && r.nextBoolean();
			} else {
				// we have jack, is it likely that we'll 
				// end the trick with it given the current suite
				// we still need to consider the other cards we
				// have in our hand. that's v2.0
				
				// let's just say that if we have two other trump
				// cards we're fine. that's J + 2 or 3 or more
				// cards in this suite
				return NumFaceCards > 2;
			}
		}
		
		public Bid calculateBestBid(Random r) {
			
			int points = SurePoints;
			
			if (hasHighProbabilityForGame() || isLikelyToCaptureJack(_sRandom)) {
				points++;				
			}
			
			if (points < 2) {
				// let's get a little more ambitious here. Do we have the king? Then
				// let's try bidding with that. THere's an 80% chance the king is high. 
				// I think...
				boolean bHaveKing = false;
				for (Card c: _cards) { 
					if (c.getValue() == Card.KING) {
						bHaveKing = true;
						break;
					}
				}
				if (bHaveKing && r.nextFloat() < 0.8f) {
					points++;
				}
			}
			
			// check out "possible" points. If we're not bidding, then up bid
			if (points == 1 && PossiblePoints > 0) {
				points++;
			} 
			
			//else if (points == 2 && PossiblePoints > 1) {
			//	points++;
			//}
			return new Bid(index, points);
			
		}
		
		public Card getHighCard() {
			Card highCard = null;
			for (Card c : _cards) {
				if (highCard == null || (highCard.getValue() < c.getValue())) {
					highCard = c;
				}
			}
			return highCard;
		}
		
		private Card[] _cards = null;		
	}
	
	public PigeontownHero(User u)  {
		super(u);
	}
		

	@Override
	protected int bid() {					
		Bid bestBidSingleSuit = null;
		
		if (Log.isDebugEnabled()) {
			Log.debug("My bid");
			for (Card c : super.getHand()) {
				Log.debug(" " + c.toString());
			}
		}
		
		Card[] cards = null;
		
		for (int i = 0; i < 4; ++i) {
			if ((cards = getSuit(i)) != null && cards.length > 0) {
				Bid b = (_sortedCards[i] = new SuitSet(_sRandom, cards)).calculateBestBid(_sRandom);
				if (bestBidSingleSuit == null || b.getBid() > bestBidSingleSuit.getBid()) {
					bestBidSingleSuit = b;				
					_myBidSuit = i;
					// deal with the case where we have two 2 bids and one is something like
					// k,2 and one is a,3 or a,2. we want to go with the suit that has the higher 
					// card. We'd rather ensure high than low, in case we can steal some points or jack
				} else if (bestBidSingleSuit != null && b.getBid() == bestBidSingleSuit.getBid()) {
					if (_sortedCards[i].getHighCard().getValue() > _sortedCards[_myBidSuit].getHighCard().getValue()) {
						_myBidSuit = i;
						bestBidSingleSuit = b;
					}
				}
			}			
		}
		
		Bid highBid = ((PitchGame) getGame()).getHighBid();
		if (highBid != null && (highBid.getBid() >= bestBidSingleSuit.getBid()) ) {
			Log.debug( "Bid is pass.");
			return Bid.PASS;
		}				
		
		_myBid = bestBidSingleSuit;
		
		if (!this.mayPass()) {
			// we're being forced to bid and we have zero points.
			// find the highest card and go with that suit.
			if (_myBidSuit == -1) {				

			}
			// why bid more than we have to. On the other hand, we
			// may be bidding more than we have to.	
			
			Log.debug( "Force bid, bidding 2 in " + _myBidSuit);
			for (Card c : getSuit(_myBidSuit)) {
				Log.debug("    " + c.getStrValue() + c.getStrSuit());
			}
			return 2;
		}
					
		Log.debug( "Bidding " + _myBid.getBid() + " in " + _myBidSuit);
		for (Card c : getSuit(_myBidSuit)){
			Log.debug( "    " + c.getStrValue() + c.getStrSuit());
		}
		return _myBid.getBid();
	}
	
	public boolean iAmBidder() {
		return _winningBid.getPlayerIndex() == this.index;
	}
	
	public boolean iAmLeading() {
		return getTrick().getPlayCount() == 0;				
	}
	
	private Bid _winningBid = null;
	public void notifyBidder(Bid winningBid) {
		Log.debug("My bid suite " + _myBidSuit);
		_winningBid = winningBid;		
	}
	
	public Card highestCard(Card[] cards) {
		return cards != null && cards.length > 0 ? cards[0] : null;
	}
	
	public Card lowestCard(Card[] cards) {
		return cards != null && cards.length > 0 ? cards[cards.length-1] : null;
	}
	
	public boolean trumpWasLead() {
		int trump = getTrick().getTrump();
		if (trump == -1) return false;
		return getTrick().getLeadSuit() == trump;
	}

	@Override
	protected Card play() {
		Card[] cards = null;		

		if (iAmLeading()) {
			if (getTrick().getTrickCount() == 0) {
				cards = getSuit(_myBidSuit);
				if (cards[0].getValue() == Card.JACK && cards.length > 1) {
					if (cards[1].getValue() == 8 && cards.length > 2) {
						assert cards[2] != null;
						return cards[2]; // lead whatever else we have besides 10.
					} else {
						assert cards[1] != null;
						return cards[1]; // lead 10
					}
				}	
				// we're leading first trick, we get to select trump!
				// lead with best card in the suite we're bidding in.
				assert getSuit(_myBidSuit)[0] != null;
				return getSuit(_myBidSuit)[0];											
			}
			// if we're leading and it's the first 
			// just simply play best trump card.
			if (((cards = this.getTrump()) != null) && cards.length > 0) {				
				// Don't lead with the jack, or the ten if we can prevent that too.
				if (cards[0].getValue() == Card.JACK && cards.length > 1) {
					if (cards[1].getValue() == 8 && cards.length > 2) {
						assert cards[2] != null;
						return cards[2]; // lead whatever else we have besides 10.
					} else {
						assert cards[1] != null;
						return cards[1]; // lead 10
					}
				}			
				assert cards[0] != null;
				return cards[0]; // lead highest card, may be jack
			}
			
			// if no trump just play best other random suit.
			Card bestNonTrumpCard = null;
			for (int i = 0; i < 4; ++i) {
				if ((cards = getSuit(i)) != null && cards.length > 0) {										
					if (bestNonTrumpCard == null || (highestCard(cards).getValue() > bestNonTrumpCard.getValue())) {
							bestNonTrumpCard = highestCard(cards);											
					}					
				}
			}
			assert bestNonTrumpCard != null;
			return bestNonTrumpCard;
		} 
		
		//
		// I'm not leading, just throw highest card following suit if possible.	
		// TODO: A couple things.
		//  1.) If our highest is not going to do us any good, check to see if we have
		//      any other card to dump.
		//  2.) Deal with jack. If the current suit is trump, either let it fly if we 
		//      have a shot at taking it, or try to protect it.
		//
		
		Card lowCard = null;
		if ((cards = getSuit(getTrick().getLeadSuit())) != null && cards.length > 0) {
			lowCard = lowestCard(cards);
			// should we throw a high or low card
			// If there's a chance at scoring game points, then throw out
			// high card in the suit
			if (getTrick().getGamePoints() > 5) {
				// TODO: check to make sure we can actually take the hand
				if (highestCard(cards).getValue() > highestCard(getTrick().getCards()).getValue()) {
					assert highestCard(cards) != null;
					return highestCard(cards);
				}						
				
				// let's try tumping for game points.
				if ((cards = getSuit(getTrick().getTrump())) != null && cards.length > 0) {
					assert highestCard(cards) != null;
					return highestCard(cards);
				}
			}
				
			// XXX: Hitting this assertion.
			// nothing interesting for game points, throw low card following suit
			assert lowCard != null;
			return lowCard;
		}
		
		// we gotta throw something.
		Card myLowestNonTrumpCard = null;
		for (int i = 0; i < 4; ++i) {
			if ((cards = getSuit(i)) != null && cards.length > 0) {
				if (myLowestNonTrumpCard == null || (lowestCard(cards).getValue() < myLowestNonTrumpCard.getValue())) {
					myLowestNonTrumpCard = lowestCard(cards);
				}
			}			
		}	
				
		assert myLowestNonTrumpCard != null : "Null card";
		return myLowestNonTrumpCard;
	}
}
