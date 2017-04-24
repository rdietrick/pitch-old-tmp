package com.pitchplayer.userprofiling.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import net.myvietnam.mvncore.security.Encoder;

import org.apache.log4j.Logger;

import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserHomeAddress;

public class UserCookieUtil {

	private static final String COOKIE_DELIM = "\t";
	
		
	/**
	 * Constructs cookie to be read by message boards site.
	 * Cookie format is expected to be:
	 * [username];[password];[firstName|null];[lastName|null];[emailAddress|null]
	 * @return
	 */
	public static String toCookieString(User user) {
		StringBuffer sb = new StringBuffer();
		sb.append(user.getUsername()).append(COOKIE_DELIM).append(Encoder.getMD5_Base64(user.getPasswd()));
		sb.append(COOKIE_DELIM);
	    sb.append(user.getFirstName());
		sb.append(COOKIE_DELIM);
		sb.append(user.getLastName());
		sb.append(COOKIE_DELIM);
		sb.append(user.getEmailAddress());
		sb.append(COOKIE_DELIM).append(new SimpleDateFormat("yyyyMMdd").format(user.getBirthDate()));
		sb.append(COOKIE_DELIM).append(user.getUserHomeAddress().getCity());
		sb.append(COOKIE_DELIM).append(user.getUserHomeAddress().getState());
		// TODO: pass user privacy settings
		Logger.getLogger(UserCookieUtil.class.getName()).debug("cookie value = " + sb.toString());
		String enc = null;
		try {
		    enc = URLEncoder.encode(sb.toString(), "UTF-8");
		} catch (UnsupportedEncodingException nee) {
		    // can't happen
		}
		return enc;
	}

	/**
	 * Parses a user object from a cookie string.
	 * Cookie is expected to be in the following format:
	 * [username];[password];[firstName|null];[lastName|null];[emailAddress|null]
	 * @param s
	 * @return
	 */
	public static User fromCookieString(String s) {
	    String dec = null;
		try {
		    dec = URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException nee) {
		    // can't happen
		}
		StringTokenizer toker = new StringTokenizer(dec, COOKIE_DELIM);
		User user = new User();
		user.setUsername(toker.nextToken());
		user.setPasswd(toker.nextToken());
		user.setFirstName(toker.nextToken());
		user.setLastName(toker.nextToken());
		user.setEmailAddress(toker.nextToken());
		try {
			user.setBirthDate(new SimpleDateFormat("yyyyMMdd").parse(toker.nextToken()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UserHomeAddress addr = new UserHomeAddress();
		user.setUserHomeAddress(addr);
		addr.setCity(toker.nextToken());
		addr.setState(toker.nextToken());
		// TODO: parse user privacy settings
		return user;
	}
}
