package com.pitchplayer.userprofiling.action;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.pitchplayer.action.BaseAction;

public class LogoutAction extends BaseAction implements ServletRequestAware {

	private HttpServletRequest request;

	
	public String doDefualt() {
		return execute();
	}
	
	public String execute() {
		request.getSession().invalidate();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				log.debug("checking cookie " + c.getName());
				String name = c.getName();
				if (name.equals("user")) {
					log.debug("expiring cookie");
					c.setMaxAge(0);
					c.setPath("/");
					response.addCookie(c);
				}
			}
		}
		else {
			log.debug("cookies is null");
		}
		return SUCCESS;
	}

	public void setServletRequest(HttpServletRequest request) {
		log.debug("servlet request = " + request);
		this.request = request;
	}
	
//
//	public void setCookiesMap(Map cookieMap) {
//		log.debug("cookies set to " + cookieMap);
//		this.cookies = cookieMap;
//		if (cookies != null) {
//			for (Iterator it = cookies.keySet().iterator();it.hasNext();) {
//				String name = (String)it.next();
//				if (name.equals("user")) {
//					Object obj = cookies.get(name);
//					log.debug("cookie object is of class " + obj.getClass().getName());
//					log.debug("obj = " + obj.toString());
//				}
//			}
//		}
//		log.debug("cookies = " + cookies);
//	}


}
