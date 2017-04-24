package com.pitchplayer.userprofiling;

import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.pitchplayer.userprofiling.om.User;

/**
 * Keeps a reference to all logged-in users
 */
public class MemoryBasedUserStore implements UserStore {

	private HashMap hash;

	protected Logger log = Logger.getLogger(this.getClass().getName());

	private static UserStore instance = null;

	private MemoryBasedUserStore() {
		hash = new HashMap();
	}

	public static UserStore getInstance() {
		if (null == instance) {
			instance = new MemoryBasedUserStore();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see com.pitchplayer.userprofiling.UserStore#addUser(java.lang.String, com.pitchplayer.userprofiling.om.User)
	 */
	public void addUser(String sessionId, User user) {
		log.debug("adding user with session ID " + sessionId);
		hash.put(sessionId, user);
	}

	/* (non-Javadoc)
	 * @see com.pitchplayer.userprofiling.UserStore#removeUser(java.lang.String)
	 */
	public void removeUser(User user) {
		if (hash.containsValue(user)) {
			Set entries = hash.entrySet();
			for (Iterator i = entries.iterator();i.hasNext();) {
				Map.Entry entry = (Map.Entry)i.next();
				if (entry.getValue() == user) {
					entries.remove(entry);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.pitchplayer.userprofiling.UserStore#getUser(java.lang.String)
	 */
	public User connectUser(String sessionId) {
		if (hash.containsKey(sessionId)) {
			return (User) hash.get(sessionId);
		} else
			return null;
	}

	/* (non-Javadoc)
	 * @see com.pitchplayer.userprofiling.UserStore#getUsers()
	 */
	public Collection getUsers() {
		return hash.values();
	}
	
	/* (non-Javadoc)
	 * @see com.pitchplayer.userprofiling.UserStore#getUsernames()
	 */
	public String[] getUsernames() {
		Collection c = hash.values();
		if (c.isEmpty()) {
			return new String[] {};
		}
		else {
			String[] usernames = new String[c.size()];
			int i =0;
			for (Iterator it = c.iterator();it.hasNext();) {
				usernames[i++] = ((User)it.next()).getUsername();
			}
			return usernames;
		}
	}

	public void disconnectUser(User user) {
		user.setLoggedIn(false);
	}

}