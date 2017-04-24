package com.pitchplayer.userprofiling;

import javax.mail.MessagingException;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pitchplayer.userprofiling.om.UserInvitation;

@Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
public interface UserInvitationService {

	public static final byte STATUS_SENT = 0;
	public static final byte STATUS_ACCEPTED = 1;

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void update(UserInvitation invitation);
	
	public UserInvitation getByInvitationCode(String code);

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void sendUserInvitation(UserInvitation invitation) throws MessagingException;
	
}
