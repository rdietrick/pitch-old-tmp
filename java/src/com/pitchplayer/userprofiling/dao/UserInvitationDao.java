package com.pitchplayer.userprofiling.dao;

import com.pitchplayer.userprofiling.om.UserInvitation;

public interface UserInvitationDao {

	public void update(UserInvitation invitation);
	
	public UserInvitation getByInvitationCode(String code);
}
