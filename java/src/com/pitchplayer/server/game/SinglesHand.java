package com.pitchplayer.server.game;

/**
 * Represents a hand of cut-throat Pitch. <br>
 * Used for keeping score.
 */
public class SinglesHand extends Hand {

	/**
	 * Create a new singles hand
	 */
	SinglesHand(int n) {
		super(n);
	}

	/**
	 * Total up the game points for each player, and find out if there was a tie
	 * for game.
	 */
	public void tallyGamePoints() {
		boolean tie = true;
		int test = 0;
		// loop thru the players and see who has the most points
		for (int i = 0, n = getNumPlayers(); i < n; i++) {
			int iGamePoints = getPlayerGamePoints(i);
			if (iGamePoints > test) {
				test = iGamePoints;
				setGameWinner(i);
				tie = false;
			} else if ((iGamePoints == test) && (test != 0)) {
				tie = true;
			}
		}
		// if there was a tie for Game, no-one gets it
		if (tie) {
			setGameWinner(GAME_POINT_TIE);
		}
	}

	/**
	 * Find out how many points a player made during this hand. Values returned
	 * will be negative if the player did not make his/her bid.
	 * 
	 * @param playerIndex
	 *            the index of the player whose point total is to be returned.
	 * @return the number of points a player made during this hand
	 */
	public int getPointsMade(int playerIndex) {
		int points = 0;
		if (getLowWinner() == playerIndex) {
			points++;
		}
		if (getHighWinner() == playerIndex) {
			points++;
		}
		if (getJackWinner() == playerIndex) {
			points++;
		}
		if (getGameWinner() == playerIndex) {
			points++;
		}
		if ((getHighBid().getPlayerIndex() == playerIndex)
				&& (points < getHighBid().getBid())) {
			return getHighBid().getBid() * -1;
		} else {
			return points;
		}
	}

}