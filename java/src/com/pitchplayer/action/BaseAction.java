package com.pitchplayer.action;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.util.UserCookieUtil;

public class BaseAction extends ActionSupport implements SessionAware, ServletResponseAware {

	private Map sessionMap;
	protected HttpServletResponse response;
	public static final String SESSION_ATTR_USER = "user";
	public static final String SESSION_ATTR_TMP_USER = "tmp_user";
	protected Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * This logic should be moved to an interceptor in order to decouple
	 * servlet-related code from the Action layer.
	 * @param user
	 */
	public void setSessionUser(User user) {
		if (sessionMap != null) {
			sessionMap.put(SESSION_ATTR_USER, user);
		}
		else {
			log.warn("session map is null; could not add user to session");
		}
		Cookie userCookie = new Cookie("user", UserCookieUtil.toCookieString(user));
		userCookie.setPath("/");
		// cookieMap.put(userCookie.getName(), userCookie);
		if (response != null) {
			response.addCookie(userCookie);
		}
		else {
			log.warn("ServletResposne is null; could not add cookie");
		}
	}
	
	public User getSessionUser() {
		if (sessionMap.containsKey(SESSION_ATTR_USER)) {
			return (User)sessionMap.get(SESSION_ATTR_USER);
		}
		else {
			return null;
		}
	}

	public void setSession(Map map) {
		this.sessionMap = map;
	}
	
	protected Map getSession() {
		return this.sessionMap;
	}

	public void setServletResponse(HttpServletResponse res) {
		response = res;
	}
	
}
