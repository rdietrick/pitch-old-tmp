package com.pitchplayer.userprofiling.util;

import com.pitchplayer.util.RandomNumberGenerator;

public class PasswordGenerator {

	private final static char[] passwordChars = { '0','1','2','3','4','5','6','7','8','9',
		'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};; 
	
	public static String generateRandomPassword() {
		int len = 8; // password length
		StringBuffer pass = new StringBuffer();
		for (int i=0;i<len;i++) {
			pass.append(passwordChars[RandomNumberGenerator.getInt(0,passwordChars.length)]);
		}
		return pass.toString();
	}
	
}
