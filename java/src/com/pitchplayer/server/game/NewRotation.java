package com.pitchplayer.server.game;

public class NewRotation  {

	int initialTurn;
	int crntTurn = 0;
	int size = 0;
	int turnsExecuted = 0;
	
	public NewRotation(int size) {
		this(size, 0);
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
	public NewRotation(int size, int crntTurn) {
		this.size = size;
		this.crntTurn = crntTurn;
		this.initialTurn = crntTurn;
	}

	public void reinit(int crntTurn) {
		this.crntTurn = crntTurn;
		this.turnsExecuted = 0;
		this.initialTurn = crntTurn;
	}
	
	/**
	 * Find out if the end of the rotation has been reached
	 * 
	 * @return true if not at the end of the rotation
	 */
	public boolean hasMoreTurns() {
		return (turnsExecuted < size); 
	}

	/**
	 * Increment the current turn pointer
	 */
	public void increment() {
		crntTurn++;
		turnsExecuted++;
	}

	/**
	 * @return current turn's index
	 */
	public int turn() {
		return (crntTurn % size);
	}

}
