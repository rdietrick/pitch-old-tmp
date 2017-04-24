package com.pitchplayer.server.game.player;

/**
 * Marker interface that denotes a remotely connected human game player.
 * @author robd
 *
 */
public interface HumanPlayer {

	/**
	 * Find out if this player is actively connected.
	 * @return
	 */
	public boolean isConnected();
	
}
