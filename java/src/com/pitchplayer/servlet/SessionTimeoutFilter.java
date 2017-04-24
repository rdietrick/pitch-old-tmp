package com.pitchplayer.servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class SessionTimeoutFilter implements Filter {

	private static final int TIMEOUT_IN_MINUTES = 45;
	
	private Logger log = Logger.getLogger(this.getClass().getName());

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpReq = (HttpServletRequest)req;
		String uri = httpReq.getRequestURI();
		HttpSession session = httpReq.getSession();
		if (uri.contains("dwrLobby")) {
			if (isSessionExpired(session)) {
				log.debug("session expired");
				session.invalidate();
			}
		}
		else {
			resetTimeout(session);
		}
		chain.doFilter(req, res);
	}


	/**
	 * Check to see if this session is beyond it's expiration date.
	 * If there is no expiration date in the session, set one.
	 * @param session
	 * @return true if the session is beyond its expiration date
	 */
	private boolean isSessionExpired(HttpSession session) {
		Date expDate = (Date) session.getAttribute("timeout_date");
		if (expDate == null) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MINUTE, TIMEOUT_IN_MINUTES);
			session.setAttribute("timeout_date", c.getTime());
			return false;
		}
		else {
			return Calendar.getInstance().getTime().after(expDate);
		}
	}

	/**
	 * Reset the session timeout for this usr
	 * @param req
	 */
	private void resetTimeout(HttpSession session) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, TIMEOUT_IN_MINUTES);
		session.setAttribute("timeout_date", c.getTime());
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
