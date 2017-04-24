package com.pitchplayer.userprofiling.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserManager;

public class UserDaoHibernate extends HibernateDaoSupport implements UserDao {

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public User getUserById(Integer userId) {
		return (User)getHibernateTemplate().get(User.class, userId);
	}

	public User getUserByUsername(String username) {
		List l = getHibernateTemplate().find("from User u where u.username = ?", username);
		if (l.size() > 0) {
			return (User)l.get(0);
		}
		else {
			return null;
		}
	}
	
	public User getUserByEmailAddress(String emailAddress) {
		List l = getHibernateTemplate().find("from User u where u.emailAddress = ?", emailAddress);
		if (l.size() > 0) {
			return (User)l.get(0);
		}
		else {
			return null;
		}
	}

	public User getUserByUsernameAndPassword(String username, String password) {
		List l = getHibernateTemplate().find("from User u where u.username = ? and u.passwdHash = ?",
				new Object[] {username, generatePasswordHash(password)});
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

	public void update(User user) {
		getHibernateTemplate().saveOrUpdate(user);
	}

	public String generatePasswordHash(final String password) {
		return (String)getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
						return UserManager.generatePasswordHash(session, password);
					}

				}
		);
	}

	public void logAllUsersOut() {
		String hqlUpdate = "update User u set u.loggedIn = 0";
		getHibernateTemplate().bulkUpdate(hqlUpdate, new Object[] {});
	}

	public boolean userExists(String username) {
		User user = getUserByUsername(username);
		return user != null;
	}

	public User getUserBySessionId(String sessionId) {
		List l = getHibernateTemplate().find("from User u where u.sessionId = ?", sessionId);
		if (l.size() > 0) {
			return (User)l.get(0);
		}
		else {
			return null;
		}
	}

	public List<User> getActiveUsers() {
		List l = getHibernateTemplate().find("from User u where u.loggedIn = ? order by u.username", true);
		return l;
	}

	public List<User> searchUsers(String username) {
		List l = getHibernateTemplate().find("from User u where u.username like ? order by u.username", username+"%");
		return l;
	}


	
	
}
