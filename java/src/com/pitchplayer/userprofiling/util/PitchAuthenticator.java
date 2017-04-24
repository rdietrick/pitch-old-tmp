//Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
//Jad home page: http://www.geocities.com/kpdus/jad.html
//Decompiler options: packimports(3) 
//Source File Name:   PitchAuthenticator.java

package com.pitchplayer.userprofiling.util;

import com.pitchplayer.userprofiling.om.User;
import com.mvnforum.auth.Authenticator;
import com.mvnforum.db.*;
import com.mvnforum.search.member.MemberIndexer;
import java.sql.Date;
import java.sql.Timestamp;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import net.myvietnam.mvncore.exception.*;
import net.myvietnam.mvncore.security.Encoder;
import net.myvietnam.mvncore.util.DateUtil;
import net.myvietnam.mvncore.web.GenericRequest;

import org.apache.log4j.Logger;

//Referenced classes of package com.mvnforum.auth:
//Authenticator

public class PitchAuthenticator implements Authenticator {

	protected Logger log = Logger.getLogger(getClass().getName());

	public PitchAuthenticator() {
	}

	public String getRemoteUser(HttpServletRequest request) {
		User user = null;
		Cookie cookies[] = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++)
				if (cookies[i].getName().equals("user")) {
					log.debug("found user cookie");
					user = UserCookieUtil.fromCookieString(cookies[i]
							.getValue());
				}

		}
		if (user == null) {
			log.debug("no user found in cookie; logging in as Guest");
			return null;
		}
		String memberName = user.getUsername();
		try {
			MemberDAO dao = DAOFactory.getMemberDAO();
			dao.findByAlternateKey_MemberName(memberName);
		} catch (ObjectNotFoundException onfe) {
			try {
				createAccount(user, request);
			} catch (Exception e) {
				log.error("Could not create user account", e);
				return null;
			}
			memberName = user.getUsername();
		} catch (DatabaseException de) {
			log.error("DB error finding user", de);
			return null;
		}
		return memberName;
	}

	public void createAccount(User user, HttpServletRequest request)
			throws ObjectNotFoundException, CreateException, DatabaseException,
			DuplicateKeyException, ForeignKeyNotFoundException {
		Timestamp now = DateUtil.getCurrentGMTTimestamp();
		Date memberBirthday = new Date(user.getBirthDate().getTime());
		String email = user.getEmailAddress();
		DAOFactory.getMemberDAO().create(
				user.getUsername(),
				user.getPasswd(),
				email,
				email,
				0,
				1,
				request.getRemoteAddr(),
				request.getRemoteAddr(),
				0,
				0,
				now,
				now,
				now,
				now,
				now,
				0,
				0,
				"",
				"",
				0,
				0,
				10,
				0,
				0,
				0,
				0,
				"",
				-8,
				"",
				"",
				"",
				"",
				user.getFirstName(),
				user.getLastName(),
				1,
				memberBirthday,
				"",
				(user.getUserHomeAddress().getCity() == null ? "" : user
						.getUserHomeAddress().getCity()),
				(user.getUserHomeAddress().getState() == null ? "" : user
						.getUserHomeAddress().getState()), "", "", "", "", "",
				"", "", "", "", "", "", "");
		int memberID = DAOFactory.getMemberDAO().getMemberIDFromMemberName(
				user.getUsername());
		int folderStatus = 0;
		int folderOption = 0;
		int folderType = 0;
		DAOFactory.getMessageFolderDAO().create("Inbox", memberID, 0,
				folderStatus, folderOption, folderType, now, now);
		DAOFactory.getMessageFolderDAO().create("Draft", memberID, 1,
				folderStatus, folderOption, folderType, now, now);
		DAOFactory.getMessageFolderDAO().create("Sent", memberID, 2,
				folderStatus, folderOption, folderType, now, now);
		DAOFactory.getMessageFolderDAO().create("Trash", memberID, 3,
				folderStatus, folderOption, folderType, now, now);
		com.mvnforum.db.MemberBean memberBean = DAOFactory.getMemberDAO()
				.getMember(memberID);
		MemberIndexer.scheduleAddMemberTask(memberBean);
	}

	public boolean isCorrectCurrentPassword(String memberName, String password, boolean encoded) {
		if (password == null || memberName == null) {
			log.warn("cannot authenticate null memberName or password");
			return false;
		}
		String crntPassword = null;
		try {
			MemberDAO dao = DAOFactory.getMemberDAO();
			int memberID = dao.getMemberIDFromMemberName(memberName);
			crntPassword = dao.getPassword(memberID);
		} catch (ObjectNotFoundException e) {
			return false;
		} catch (DatabaseException e) {
			log.error("Error authenticating user", e);
			return false;
		}
		return crntPassword.equals((encoded?password:Encoder.getMD5_Base64(password)));
	}

	public String getRemoteUser(GenericRequest req) {
		log.debug("getRemoteUser(GenericRequest) called");
		System.out.println("getRemoteUser(GenericRequest) called");
		return getRemoteUser(req.getServletRequest());
	}
}
