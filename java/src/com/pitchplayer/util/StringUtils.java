package com.pitchplayer.util;

public class StringUtils {

	public static boolean isBlank(String s) {
		return ((s == null) || (s.trim().length() == 0));
	}

	public static boolean isNull(String s) {
		return isBlank(s);
	}
	
}
