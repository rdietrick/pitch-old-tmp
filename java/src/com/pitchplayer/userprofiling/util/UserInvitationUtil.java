package com.pitchplayer.userprofiling.util;

import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserInvitationUtil {

	private static Pattern regExp = Pattern.compile(com.opensymphony.xwork2.validator.validators.EmailValidator.emailAddressPattern);
	
	public static String generateInvitationCode(String emailAddress) {
		return UUID.randomUUID().toString();
	}

	public static String[] parseEmailAddresses(String emailAddresses) 
		throws IllegalArgumentException {
		if (emailAddresses == null) {
			return new String[] {};
		}
		StringTokenizer st = new StringTokenizer(emailAddresses, " \t\n\r\f,");
		String[] addresses = new String[st.countTokens()];
		int i=0;
		while (st.hasMoreTokens()) {
			String addr = st.nextToken();
			if (!validateEmail(addr)) {
				throw new IllegalArgumentException(addr + " is not a valid email address");
			}
			else {
				addresses[i++] = addr;
			}
		}
		return addresses;
	}

	private static boolean validateEmail(String addr) {
		Matcher m = regExp.matcher(addr);
		return m.matches();
	}
	
}
