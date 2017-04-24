package com.pitchplayer.server.game;

/**
 * Represents a round of players. <BR>
 * Can be used to track the dealerRotation across hands, or the play or bid turn in a single hand.
 */
public class Rotation {

	// the players
	private int[] players;

	// the current turn's index in the players array
	private int crntIndex;

	/**
	 * Create a new Rotation of specified size. <br>
	 * The first element in the rotation gets the first turn.
	 * 
	 * @param size
	 *            the number of players to track in the rotation
	 */
	public Rotation(int size) {
		players = new int[size];
		for (int i = 0; i < size; i++) {
			players[i] = i;
		}
		crntIndex = 0;
	}

	/**
	 * Create a new Rotation of specified size and a current turn indicator
	 * (index)
	 * 
	 * @param size
	 *            the number of players to track in the rotation
	 * @param crntTurn
	 *            the index of the current turn
	 */
	public Rotation(int size, int crntTurn) {
		int x = crntTurn;
		players = new int[size];
		for (int i = 0; i < size; i++) {
			if (x < size) {
				players[i] = x;
			} else {
				x = 0;
				players[i] = x;
			}
			x++;
		}
		crntIndex = 0;
	}

	/**
	 * Find out if the end of the rotation has been reached
	 * 
	 * @return true if not at the end of the rotation
	 */
	public boolean hasMoreTurns() {
		if (crntIndex < players.length)
			return true;
		else
			return false;
	}

	/**
	 * Increment the current turn pointer
	 */
	public void increment() {
		crntIndex++;
	}

	/**
	 * @return current turn's index
	 */
	public int turn() {
		return players[crntIndex];
	}

}