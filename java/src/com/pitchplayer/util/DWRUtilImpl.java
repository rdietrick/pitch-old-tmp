package com.pitchplayer.util;

import java.io.IOException;

import javax.servlet.ServletException;

import org.directwebremoting.WebContextFactory;

public class DWRUtilImpl implements DWRUtil {

	public String getUrl(String url) throws ServletException, IOException {
		return WebContextFactory.get().forwardToString(url);
	}

}
