package com.pitchplayer.userprofiling.dao;

import java.util.List;

import com.pitchplayer.userprofiling.om.User;

public interface UserDao {

	public User getUserByUsername(String username);
	
	public User getUserById(Integer userId);
	
	public void update(User user);
	
	public User getUserByUsernameAndPassword(String username, String password);
	
	public String generatePasswordHash(String password);

	public void logAllUsersOut();

	public boolean userExists(String username);

	public User getUserByEmailAddress(String emailAddress);

	public User getUserBySessionId(String sessionId);

	public List<User> getActiveUsers();
	
	public List<User> searchUsers(String username);
	
}
