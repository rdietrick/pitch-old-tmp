package com.pitchplayer.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.server.UserNotificationService;
import com.pitchplayer.userprofiling.om.User;

/**
 * Classes which are intended to be exposed to JavaScript clients
 * through DWR should extend this class.
 * Provides utility methods for referencing the current HttpSession 
 * and User. 
 * @author robd
 *
 */
public abstract class DWRProxy {
	
	private UserNotificationService userNotificationService;
	protected ServletContext servletContext;
	
	protected Logger log = Logger.getLogger(this.getClass().getName());
	

	/**
	 * Utility method to get the current HTTP session.
	 * Should only be called from a DWR thread 
	 * (i.e., from a remotely invoked method).
	 * @return
	 */
	protected HttpSession getHttpSession() {
		WebContext ctx = WebContextFactory.get();
		if (ctx == null) {
			Exception e = new RuntimeException("WebContext is null; call does not appear to be made from a web thread");
			log.error("WebContext is null; call does not appear to be made from a web thread", e);
		}
		if (this.servletContext == null) {
			this.servletContext = ctx.getServletContext();
		}
		return ctx.getSession();
	}
	
	/**
	 * Utility method to get the User in the current session.
	 * Like getSession(), this method should only be invoked 
	 * from a DWR thread.
	 * @return
	 */
	protected User getSessionUser() {
		return (User)getHttpSession().getAttribute(BaseAction.SESSION_ATTR_USER);
	}

	public UserNotificationService getUserNotificationService() {
		return userNotificationService;
	}

	public void setUserNotificationService(
			UserNotificationService userNotificationService) {
		this.userNotificationService = userNotificationService;
	}

}
