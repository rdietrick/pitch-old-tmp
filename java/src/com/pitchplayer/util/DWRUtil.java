package com.pitchplayer.util;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Remote interface which provides some generic utilities.
 * @author robd
 *
 */
public interface DWRUtil {

	public String getUrl(String url) throws ServletException, IOException;
	
}
