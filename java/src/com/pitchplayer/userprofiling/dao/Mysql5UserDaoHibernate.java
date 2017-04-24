package com.pitchplayer.userprofiling.dao;

import java.util.List;

import com.pitchplayer.userprofiling.om.User;

public class Mysql5UserDaoHibernate extends UserDaoHibernate {
	
	public User getUserByUsernameAndPassword(String username, String password) {
		List l = getHibernateTemplate().find("from User u where u.username = ? and (password(?) = u.passwdHash or old_password(?) = u.passwdHash)",
				new Object[] {username, password, password});
		if (l.size() > 0) {
			User user = (User)l.get(0); 
			if (user != null) {
				user.setPasswd(password);
			}
			return user;
		}
		else {
			return null;
		}
	}

}
