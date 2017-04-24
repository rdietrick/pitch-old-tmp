package com.pitchplayer.stats;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.userprofiling.om.User;

public class UserRankingUpdateFilter implements Filter {

	private PlayerRankingsCache playerRankingsCache;
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest)req).getSession(false);
		if (session != null && session.getAttribute(BaseAction.SESSION_ATTR_USER) != null) {
			// update rank if necessary
			User user = (User)session.getAttribute(BaseAction.SESSION_ATTR_USER);
			if (user.getLastRankUpdateDate() == null || 
					user.getLastRankUpdateDate().before(playerRankingsCache.getLastUpdateDate())) {
				int rank = playerRankingsCache.getPlayerRank(user.getUsername());
				if (rank > -1) {
					user.setCurrentRank(rank);
				}
			}
		}
		chain.doFilter(req, res);
	}

	public void init(FilterConfig config) throws ServletException {
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		this.playerRankingsCache = (PlayerRankingsCache)ctx.getBean("rankingsCache");
	}

}
