package com.pitchplayer.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.pitchplayer.action.BaseAction;

/**
 * Filter which prevents unauthenticated requests from calling DWR methods.
 * @author robd
 *
 */
public class DWRAuthFilter implements Filter {

	
	public void destroy() {
		// TODO Auto-generated method stub

	}

	
	
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest)req).getSession(false);
		if (session == null || session.getAttribute(BaseAction.SESSION_ATTR_USER) == null) {
			throw new ServletException("Unauthorized request");
		} 
		else {
			// OK
			chain.doFilter(req, res);
		}
		
	}

	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub

	}

}
