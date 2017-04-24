package com.pitchplayer.server.game;

/**
 * Represents a hand of doubles Pitch
 */
public class DoublesHand extends Hand {

	/**
	 * Create a new DoublesHand with a given number of players.
	 * 
	 * @param numPlayers
	 *            the number of players in the hand.
	 */
	DoublesHand(int numPlayers) {
		super(numPlayers);
	}

	/**
	 * Total up the game points for each team, and find out if there was a tie
	 * for game.
	 */
	public void tallyGamePoints() {
		boolean tie = true;
		int test = 0;
		// loop thru the teams and see who has the most points
		for (int i = 0; i < getNumPlayers() / 2; i++) {
			// get the team's total
			int iGamePoints = getPlayerGamePoints(i)
					+ getPlayerGamePoints(i + getNumPlayers() / 2);
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
	 * @param teamIndex
	 *            the index of the team whose point total is to be returned.
	 * @return the number of points a player made during this hand
	 */
	public int getPointsMade(int teamIndex) {
		int points = 0;
		if ((getLowWinner() % (getNumPlayers() / 2)) == teamIndex) {
			points++;
		}
		if ((getHighWinner() % (getNumPlayers() / 2)) == teamIndex) {
			points++;
		}
		if ((getJackWinner() % (getNumPlayers() / 2)) == teamIndex) {
			points++;
		}
		if (getGameWinner() == teamIndex) {
			points++;
		}
		if (((getHighBid().getPlayerIndex() % (getNumPlayers() / 2)) == teamIndex)
				&& (points < getHighBid().getBid())) {
			return getHighBid().getBid() * -1;
		} else {
			return points;
		}
	}

}

