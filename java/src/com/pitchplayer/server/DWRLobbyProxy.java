package com.pitchplayer.server;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.pitchplayer.server.ServerException.StatusCode;
import com.pitchplayer.server.game.GameFactory;
import com.pitchplayer.server.game.GameInfo;
import com.pitchplayer.servlet.BaseServlet;
import com.pitchplayer.userprofiling.UserAware;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.util.DWRProxy;

public class DWRLobbyProxy extends DWRProxy implements RemoteLobby, ChallengeListener, UserAware {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private UserService userService;
	private GameFactory gameFactory;
	private Collection<Challenge> allChallenges = new Vector<Challenge>();
	private User user;

	private void checkAuth() throws ServerException {
		WebContext ctx = WebContextFactory.get();
		HttpSession session = ctx.getSession();
		if (session.getAttribute(BaseServlet.SESSION_USER_ATTR) == null) {
			throw new ServerException(StatusCode.SESSION_TIMEOUT, "Your session has timed out.  Please log in again.");
		}
	}
	
	/**
	 * Get all challenges.
	 * @return
	 */
	public Collection<Challenge> getAllChallenges() throws ServerException {
		checkAuth();
		return allChallenges;
	}
	
	public List<GameInfo> getGameList() throws ServerException {
		checkAuth();
		try {
			return gameFactory.getGameInfoList();
		} catch (Exception e) {
			log.error("Error getting game list", e);
			throw new ServerException(StatusCode.SERVER_ERROR, e.getMessage());
		}
	}

	/**
	 * Receive a challenge from another player.
	 * TODO: Should filter for challenges that are relevant to this user (from friends or to-all).
	 */
	public void receiveChallenge(Challenge challenge) {
		log.debug("received challenge");
		if (user != null && challenge.getChallenger().getUserId() != user.getUserId()) {			
			allChallenges.add(challenge);
		}
	}

	public void revokeChallenge(Challenge challenge) {
		if (user != null && challenge.getChallenger().getUserId() != user.getUserId()) {			
			if (allChallenges.contains(challenge)) {
				allChallenges.remove(challenge);
			}
		}
	}
	
	
//	public Collection<UserFriend> getFriendUpdates() {
//		log.debug("getFriendUpdates() called");
//		UserFriendsList friendsList = (UserFriendsList)getHttpSession().getAttribute(PitchSessionListener.FRIENDS_LIST_ATTR);
//		if (friendsList != null) {
//			return friendsList.getFriendUpdates();
//		}
//		else {
//			log.debug("friends list is null");
//			return null;
//		}
//	}
	

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public String[] getOnlineUsers() {
		return userService.getActiveUsernames();
	}

	public GameFactory getGameFactory() {
		return gameFactory;
	}

	public void setGameFactory(GameFactory gameFactory) {
		this.gameFactory = gameFactory;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
