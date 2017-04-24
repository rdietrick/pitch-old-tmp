package com.pitchplayer.util;
import org.apache.log4j.Logger;
import org.directwebremoting.*;

public class DwrTest {

	private Logger log = Logger.getLogger(getClass().getName());
	
	public DwrTest() {
		
	}
	
	public String getString() {
		return "hello DWR world";
	}
	
	public void sendUpdate() {
		log.debug("sendUpdate called!");
		ScriptBuffer script = new ScriptBuffer();
	    script.appendScript("alert('you have been updated');");

	    ScriptSession scriptSess = WebContextFactory.get().getScriptSession();
	    scriptSess.addScript(script);
	}
	
}
