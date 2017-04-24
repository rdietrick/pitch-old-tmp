package com.pitchplayer.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.userprofiling.om.User;

public class HibernateReattachUserFilter extends OncePerRequestFilter {

	private static final String DEFAULT_SESSION_FACTORY_BEAN_NAME = "sessionFactory";
	private Logger log = Logger.getLogger(this.getClass().getName());
	private String sessionFactoryBeanName = DEFAULT_SESSION_FACTORY_BEAN_NAME;

	public void destroy() {
		// TODO Auto-generated method stub

	}

	
	/**
	 * Look up the SessionFactory that this filter should use.
	 * <p>The default implementation looks for a bean with the specified name
	 * in Spring's root application context.
	 * @return the SessionFactory to use
	 * @see #getSessionFactoryBeanName
	 */
	protected SessionFactory lookupSessionFactory() {
		log.debug("Using SessionFactory '" + getSessionFactoryBeanName() + "' for OpenSessionInViewFilter");
		WebApplicationContext wac =
				WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		return (SessionFactory) wac.getBean(getSessionFactoryBeanName(), SessionFactory.class);
	}

	public String getSessionFactoryBeanName() {
		return sessionFactoryBeanName;
	}

	public void setSessionFactoryBeanName(String sessionFactoryBeanName) {
		this.sessionFactoryBeanName = sessionFactoryBeanName;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		SessionFactory sessionFactory = lookupSessionFactory();
		User user = (User) request.getSession().getAttribute(BaseAction.SESSION_ATTR_USER);
		if (user != null) {
			sessionFactory.getCurrentSession().lock(user, LockMode.NONE);
		}
		filterChain.doFilter(request, response);
	}


}
