package com.pitchplayer.temp;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ServerContext;
import org.directwebremoting.ServerContextFactory;

public class DwrTest implements DwrListener {

	private Logger log = Logger.getLogger(getClass().getName());
	private ServletContext servletCtx;
	
	public DwrTest() {
		
	}
	
	public String getString() {
		return "hello DWR world";
	}
	
	public void update(String s, String updatePage) {
		log.debug("listener updated: " + s);
		ScriptBuffer script = new ScriptBuffer();
	    script.appendScript("alert('" + s + "');");
	    ServerContext ctx = ServerContextFactory.get(servletCtx);
	    log.debug("ServerContext = " + ctx);
	    Collection<ScriptSession> c = ctx.getScriptSessionsByPage(updatePage);
	    log.debug(c.size() + " ScriptSessions found for URL " + updatePage);
	    for (ScriptSession sess : c) {
	    	if (sess != null && sess.getId() != null) {
			    sess.addScript(script);
			    log.debug("sent script");	    		
	    	}
	    }
	    c = ctx.getAllScriptSessions();
	    log.debug("found " + c.size() + " script sessions total");
	    for (ScriptSession sess : c) {
	    	if (sess != null && sess.getId() != null) {
	    		for (Iterator<String> i = sess.getAttributeNames();i.hasNext();) {
	    			String name = i.next();
	    			log.debug("\tscript session has attribute " + name + " with value = "+ sess.getAttribute(name));
	    		}
	    	}
	    }
	}

	public void setServletContext(ServletContext ctx) {
		this.servletCtx = ctx;
	}


}
