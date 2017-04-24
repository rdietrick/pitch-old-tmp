package com.pitchplayer.userprofiling;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.pitchplayer.userprofiling.dao.UserDao;
import com.pitchplayer.userprofiling.om.CPUPlayerRecord;
import com.pitchplayer.userprofiling.om.User;

@Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
public interface UserService {
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void update(User user);
	
	public User getUserById(Integer userId);

	public User getUserByUsername(String username);

	public User getUserByEmailAddress(String emailAddress);

	public User authenticateUser(String username, String password);
	
	public void setUserDao(UserDao userDao);
	
	public UserDao getUserDao();
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void createUser(User user, boolean sendEmailValidation) throws DuplicateUserException;

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public boolean updatePassword(User user, String oldPassword, String newPassword);

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public boolean updatePassword(User user, String newPassword);

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void logAllUsersOut();

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public boolean validateEmail(User user, String code);

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void sendUserPasswordReminderEmail(User user);
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void sendValidationEmail(User user);

	public User getUserBySessionId(String sessionId);

	public List<User> searchUsers(String username);
	
	public Collection getActiveUsers();

	public String[] getActiveUsernames();
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void createCPUPlayer(CPUPlayerRecord player, String username) throws DuplicateUserException;
	
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void updateCPUPlayer(CPUPlayerRecord cpuPlayer);

	public List<CPUPlayerRecord> getAllCPUPlayers();

	public CPUPlayerRecord getCPUPlayer(Integer userId);

	public List<CPUPlayerRecord> getAllPlayableCPUPlayers();
	
}
