package com.pitchplayer.server.game.strategy;

public class StrategyFactory {
	
	private int trumpSuit = -1;
	private int bidAmount = -1;
	
	private class Player {
		int score;
		int seat;
		boolean[] deSuited = new boolean[4];
		boolean isBidder;
		int gamePoints;
		boolean scoredLow;
		boolean scoredHigh;
		boolean scoredJack;
		boolean madeBid;
		
		Player() {
		}
		
		void initHand(int seat) {
			deSuited = new boolean[4];
			isBidder = false;
		}
	}
	
	/**
	 * Should initialize internal state to indicate the
	 * beginning of a new hand.
	 */
	public void initHand() {
		int trumpSuit = -1;
		bidAmount = -1;
	}
	
	
	public PlayingStrategy getStrategy() {
		return null;
		
	}
}
