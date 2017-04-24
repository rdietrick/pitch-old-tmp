 package com.pitchplayer.servlet;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.server.ChallengeListener;
import com.pitchplayer.server.GameChallengeService;
import com.pitchplayer.server.UserNotificationListener;
import com.pitchplayer.server.UserNotificationService;
import com.pitchplayer.server.game.player.DWRPlayerProxy;
import com.pitchplayer.server.game.player.ReverseAjaxPitchPlayer;
import com.pitchplayer.userprofiling.UserAware;
import com.pitchplayer.userprofiling.UserStore;
import com.pitchplayer.userprofiling.om.User;

/**
 * 
 * @author robd
 *
 */
public class PitchSessionListener implements HttpSessionListener, HttpSessionAttributeListener {

	private static final String USER_NOTIFICATION_SERVICE = "userNotificationService";
	public static final String CHALLENGE_LISTENER_ATTR = "chlng_listener";
	private static final String GAME_CHALLENGE_SERVICE = "gameChallengeService";
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public void sessionCreated(HttpSessionEvent event) {
		// do nothing for now
	}


	/**
	 * Get a Spring bean by bean name from the servlet context.
	 * @param session
	 * @param beanName
	 * @return the bean with the specified name or null if no such bean could be found.
	 */
	private static Object getBean(HttpSession session, String beanName) {
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
		return ctx.getBean(beanName);
	}
	
	
	private UserStore getUserStore(HttpSession session) {
		return (UserStore)getBean(session, "userStore");
	}
	
	
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		User user = (User) session.getAttribute(BaseAction.SESSION_ATTR_USER);
		if (user != null) {
			getUserStore(session).removeUser(user);
		}
		ChallengeListener listener = (ChallengeListener)session.getAttribute(CHALLENGE_LISTENER_ATTR);
		if (listener != null) {
			GameChallengeService svc = (GameChallengeService)getBean(session, GAME_CHALLENGE_SERVICE);
			svc.removeChallengeListener(listener);
			session.removeAttribute(CHALLENGE_LISTENER_ATTR);
		}
		// TODO: need to kill any active games
		// kill user's game if he's in one
		DWRPlayerProxy player = (DWRPlayerProxy) getSessionAttributeByClass(session, DWRPlayerProxy.class);
		if (player != null) {
			player.leaveGame();
		}
		
		// remove all UserNotificationListeners from the UserNotificationService
		UserNotificationService svc = (UserNotificationService)getBean(event.getSession(), USER_NOTIFICATION_SERVICE);
		for (Enumeration keys = session.getAttributeNames(); keys.hasMoreElements();) {
			Object obj = session.getAttribute((String) keys.nextElement());
			if (UserNotificationListener.class.isAssignableFrom(obj.getClass())) {
				svc.removeNotificationListener((UserNotificationListener)obj);
			}
		}		
	}

	protected Object getSessionAttributeByClass(HttpSession session, Class clazz) {
		Enumeration<String> names = session.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			Object obj = session.getAttribute(name);
			if (clazz.isAssignableFrom(obj.getClass())) {
				return obj;
			}
		}
		return null;
	}
	
	public void attributeAdded(HttpSessionBindingEvent event) {
		Object name = event.getName();
		log.debug("Object added to session: name=" + event.getName() + " class = " + event.getValue().getClass().getName());
		if (name.equals(BaseAction.SESSION_ATTR_USER)) {
			HttpSession session = event.getSession();
			User user = (User)event.getValue();
			getUserStore(session).addUser((String) session.getId(),	user);
		}
		else if (ChallengeListener.class.isAssignableFrom(event.getValue().getClass())) {
			GameChallengeService svc = (GameChallengeService)getBean(event.getSession(), GAME_CHALLENGE_SERVICE);
			svc.addChallengeListener((ChallengeListener) event.getValue());
		}
		if (UserAware.class.isAssignableFrom(event.getValue().getClass())) {
			HttpSession session = event.getSession();
			User user = (User) session.getAttribute(BaseAction.SESSION_ATTR_USER);
			if (user != null) {
				((UserAware)event.getValue()).setUser(user);
			}
		}
//		else if (UserNotificationListener.class.isAssignableFrom(event.getValue().getClass())) {
//			UserNotificationService svc = (UserNotificationService)getBean(event.getSession(), USER_NOTIFICATION_SERVICE);
//			log.debug("adding listener to UserNotificationService");
//			svc.addNotificationListener((UserNotificationListener) event.getValue());
//		}
	}

	public void attributeRemoved(HttpSessionBindingEvent event) {
		// Object attr = event.getValue();
		Object name = event.getName();
		if (name.equals(BaseAction.SESSION_ATTR_USER)) {
			User user = (User)event.getValue();
			log.debug("user " + user.getUsername() + " removed from session");
//			getFriendsListManager(event.getSession()).updateUserStatus(user, false);
			getUserStore(event.getSession()).removeUser(user);
		}
		else {
			log.debug(event.getValue().getClass().getName() + " removed from session");
		}
	}

	public void attributeReplaced(HttpSessionBindingEvent event) {

	}

//	
//	private void addFriendsList(final HttpSession httpSession, final User user) {
//		log.debug("addFriendsList called");
//		List<UserAssociation> assocs = getUserAssociationService(httpSession).getUserAssociations(user, UserAssociationType.FRIEND, 
//				UserAssociation.STATUS_CONFIRMED);
//		List<String> onlineFriends = new ArrayList<String>();
//		List<String> offlineFriends = new ArrayList<String>();
//		for (UserAssociation a : assocs) {
//			User friend = a.getUserByAssociateId();
//			if (friend.getLoggedIn()) {
//				onlineFriends.add(friend.getUsername());
//			}
//			else {
//				offlineFriends.add(friend.getUsername());
//			}
//		}
//		UserFriendsList friendsList = new UserFriendsList(user.getUsername(), onlineFriends, offlineFriends);
//		getFriendsListManager(httpSession).addListener(friendsList);
//		httpSession.setAttribute(FRIENDS_LIST_ATTR, friendsList);
//		log.debug("friendsList added to session");
//	}

}
