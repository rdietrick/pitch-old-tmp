package com.pitchplayer.server.game.player;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ServerContext;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.util.DWRProxy;

/**
 * Base class for Reverse Ajax DWR client proxies.
 * Provides methods for sending messages to clients from non-web threads.
 * @author robd
 *
 */
public class ReverseAjaxDWRProxy extends DWRProxy {

	private static final String USER_SCRIPT_ATTR = "userScriptId";
	private String userScriptId;
	protected Logger log = Logger.getLogger(this.getClass().getName());
	private boolean connected = false;
	private Collection<ScriptBuffer> scriptQueue = new ArrayList<ScriptBuffer>(3);
	
	/**
	 * Clear any queued scripts.
	 */
	protected void clearScriptQueue() {
		if (!scriptQueue.isEmpty()) {
			scriptQueue.clear();
		}
	}
	
	/**
	 * Send a script to the client.
	 * This method should only be invoked asynchronously (by non-web threads).
	 * @param sb
	 */
	protected synchronized void sendScript(ScriptBuffer sb) {
		if (!connected) {
			queueScript(sb);
			return;
		}
		ScriptSession sess = getScriptSession();
		if (sess != null) {
			sess.addScript(sb);
		}
		else {
			log.error("no script session found");
		}
	}

	private void queueScript(ScriptBuffer sb) {
		log.debug("script queued: " + sb.toString());
		scriptQueue.add(sb);
	}

	private void sendQueuedScripts() {
		for (ScriptBuffer sb : scriptQueue) {
			sendScript(sb);
		}
	}

	private ScriptSession getScriptSession() {
		ServerContext serverCtx = ServerContextFactory.get(servletContext);
		Collection<ScriptSession> sessions = serverCtx.getAllScriptSessions();
		for (ScriptSession sess : sessions) {
			if (sess == null || sess.getId() == null) continue; 
			String sessUserScriptId = (String) sess.getAttribute(USER_SCRIPT_ATTR); 
			if (sessUserScriptId != null && sessUserScriptId.equals(userScriptId)) {
				return sess;
			}
		}
		return null;
	}

	private void invalidateScriptSession() {
		ScriptSession sess = getScriptSession();
		if (sess != null) {
			sess.invalidate();
		}
	}

	/**
	 * Establish this object as a proxy between a client (making this call through DWR)
	 * and a player in the user's session.
	 * TODO: need better error handling in the case that join failed
	 * @param session
	 * @return
	 */
	public void connect() {
		WebContext ctx = WebContextFactory.get();
		this.servletContext = ctx.getServletContext(); 
		ScriptSession scriptSess = ctx.getScriptSession();
		User user = getSessionUser();
		user.getUsername();
		log.debug("registering script session with ID " + scriptSess.getId() + " to user " + user.getUsername());
		userScriptId = user.getUsername() + System.currentTimeMillis();
		scriptSess.setAttribute(USER_SCRIPT_ATTR, userScriptId);
		this.connected = true;
		sendQueuedScripts();
	}

	public void disconnect() {
		log.debug("disconnect() called; script session invalidated");
		connected = false;
		invalidateScriptSession();
	}

	public boolean isConnected() {
		return connected;
	}

}
