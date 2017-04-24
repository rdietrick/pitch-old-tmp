package com.pitchplayer.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;

public class LoggingManager {
	private static final String LOG4J_XML_FILE = "log4jxml";
	
	public void init(ServletContext ctx) {
		// initialize log4j
		// String path = ctx.getRealPath("/");
		// chop off any filename at the end of the path
		// String docBase = path.substring(0, path.lastIndexOf(File.separator));
		String log4jXml = ctx.getInitParameter(LOG4J_XML_FILE);
		InputStream input = null;
		input = ctx.getResourceAsStream(log4jXml);
		// input = new FileInputStream(docBase + log4jXml); 
		new DOMConfigurator().doConfigure(input, LogManager.getLoggerRepository());
		System.out.println("Log4J initialized");
	}
	
}
