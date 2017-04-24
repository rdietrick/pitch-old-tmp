package com.pitchplayer.userprofiling.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pitchplayer.userprofiling.om.UserInvitation;

public class UserInvitationDaoHibernate extends HibernateDaoSupport implements UserInvitationDao {

	public UserInvitation getByInvitationCode(String code) {
		List l = getHibernateTemplate().find("from UserInvitation u where u.invitationCode = ?", code);
		if (l.size() > 0) {
			return (UserInvitation)l.get(0);
		}
		return null;
	}

	public void update(UserInvitation invitation) {
		getHibernateTemplate().saveOrUpdate(invitation);
	}

}
