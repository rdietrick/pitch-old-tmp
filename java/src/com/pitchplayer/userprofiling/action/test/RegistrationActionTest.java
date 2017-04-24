package com.pitchplayer.userprofiling.action.test;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import com.opensymphony.xwork2.ActionSupport;
import com.pitchplayer.userprofiling.action.RegistrationAction;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserConstants;
import com.pitchplayer.userprofiling.om.UserHomeAddress;

public class RegistrationActionTest extends TestCase {
	
	Logger log = Logger.getLogger(this.getClass().getName());
	
	  public void testRegistration() throws Exception {
		  RegistrationAction reg = new RegistrationAction();
		  // reg.setCookiesMap(new HashMap());
		  reg.setSession(new HashMap());
		  //reg.setCookiesMap(new HashMap());
		  reg.prepare();
		  User user = reg.getUser();
		  UserHomeAddress addr = user.getUserHomeAddress();
		  user.setUsername("rizmaniac123");
		  user.setPasswd("rizman");
		  user.setPasswdHash("");
		  user.setUserType(UserConstants.USER_TYPE_HUMAN);
		  user.setLoggedIn(false);
		  Date d = new Date();
		  user.setLastLogin(d);
		  user.setRegistrationDate(d);
		  addr.setCity("SF");
		  addr.setState("CA");
		  user.setEmailAddress("rizbert@gmail.com");
		  user.setFirstName("rizbert");
		  user.setLastName("dietrock");
		  user.setLoginCount(1);
		  String result = reg.execute();
		  assertTrue("Expected a success result!",
				  ActionSupport.SUCCESS.equals(result));
		  
	  }
}
