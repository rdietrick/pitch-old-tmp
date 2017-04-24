package com.pitchplayer.server.game.player;

import com.pitchplayer.util.DWRProxy;

/**
 * A base class for remoting objects to web clients via DWR.
 * Provides a utility method for getting a reference to a game player.
 * @deprecated 
 * @author robd
 *
 */
public abstract class PlayerCapableDWRProxy extends DWRProxy {

	protected ReverseAjaxPlayerFactory reverseAjaxPlayerFactory;
	private ReverseAjaxPitchPlayer player;
	
	/**
	 * Get a reference to the ReverseAjaxPitchPlayer tied to the current user's
	 * HTTP session.
	 * The first time this is called, it must be called from a web thread, so that 
	 * the player can be retrieved from the current HTTP session. 
	 * @return
	 */
	public ReverseAjaxPitchPlayer getPlayer() {
		if (player == null) {
			player =  reverseAjaxPlayerFactory.getPlayer(getHttpSession());
		}
		return player;
	}

	public ReverseAjaxPlayerFactory getReverseAjaxPlayerFactory() {
		return reverseAjaxPlayerFactory;
	}

	public void setReverseAjaxPlayerFactory(
			ReverseAjaxPlayerFactory reverseAjaxPlayerFactory) {
		this.reverseAjaxPlayerFactory = reverseAjaxPlayerFactory;
	}

	
}
