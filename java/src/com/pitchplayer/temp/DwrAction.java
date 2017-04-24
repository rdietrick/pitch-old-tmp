package com.pitchplayer.temp;

import javax.servlet.ServletContext;

import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.util.ServletContextAware;

import com.pitchplayer.action.BaseAction;

public class DwrAction extends BaseAction implements ServletContextAware {

	DwrService dwrService;
	private ServletContext servletCtx;
	
	public String execute() {
		DwrTest dwr = new DwrTest();
		dwr.setServletContext(servletCtx);
		getSession().put("dwr", dwr);
		dwrService.addDwrListener(dwr);
		return SUCCESS;
	}

	public DwrService getDwrService() {
		return dwrService;
	}

	public void setDwrService(DwrService dwrService) {
		this.dwrService = dwrService;
	}

	public void setServletContext(ServletContext ctx) {
		this.servletCtx = ctx;
	}

}
