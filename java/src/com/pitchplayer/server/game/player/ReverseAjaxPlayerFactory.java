package com.pitchplayer.server.game.player;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.server.game.GameFactory;
import com.pitchplayer.userprofiling.om.User;

public class ReverseAjaxPlayerFactory {

	private GameFactory gameFactory;
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public static final String ATTR_PITCH_PLAYER = "raPlayer";

	/**
	 * Utility method to get (or create) a ReverseAjaxPitchPlayer for the
	 * current user.
	 * If a new player is created, it is added to the user's session before 
	 * it is returned.
	 * This method should only be called from a DWR thread.
	 * @return
	 */
	protected ReverseAjaxPitchPlayer getPlayer(HttpSession session) {
		ReverseAjaxPitchPlayer player = (ReverseAjaxPitchPlayer) session.getAttribute(ATTR_PITCH_PLAYER); 
		if (player == null) {
			player = new ReverseAjaxPitchPlayer((User)session.getAttribute(BaseAction.SESSION_ATTR_USER));
			player.setGameFactory(gameFactory);
			log.debug("created new player with game factory = " + gameFactory.toString());
			session.setAttribute(ATTR_PITCH_PLAYER, player);
		}
		else {
			log.debug("reusing existing ReverseAjaxPitchPlayer");
		}
		return player;
	}


	public GameFactory getGameFactory() {
		return gameFactory;
	}

	public void setGameFactory(GameFactory gameFactory) {
		this.gameFactory = gameFactory;
	}

	
}
