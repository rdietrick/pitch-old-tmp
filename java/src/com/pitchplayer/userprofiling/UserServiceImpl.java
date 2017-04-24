package com.pitchplayer.userprofiling;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;

import com.pitchplayer.mail.EmailTemplate;
import com.pitchplayer.mail.EmailTemplateManager;
import com.pitchplayer.userprofiling.dao.CPUPlayerDao;
import com.pitchplayer.userprofiling.dao.UserDao;
import com.pitchplayer.userprofiling.om.CPUPlayerRecord;
import com.pitchplayer.userprofiling.om.EmailValidation;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserConstants;
import com.pitchplayer.userprofiling.om.UserGamePref;
import com.pitchplayer.userprofiling.om.UserPref;
import com.pitchplayer.userprofiling.util.PasswordGenerator;

public class UserServiceImpl implements UserService {
	
	private JavaMailSender mailSender;
	EmailTemplateManager emailTemplateManager;
	private String emailValidationTemplateName;
	private String welcomeMessageTemplateName;
	private String passwordReminderTemplateName;
	private UserDao userDao;
	private CPUPlayerDao cpuPlayerDao;

	Logger log = Logger.getLogger(this.getClass().getName());
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	/**
	 * Check a supplied email validation code against the one on record for a user.
	 * If they match, the user is marked as authenticated.
	 */
	public boolean validateEmail(User user, String validationCode) {
		EmailValidation eVal = user.getEmailValidation(); 
		if (eVal.getValidationCode().equals(validationCode)) {
			eVal.setValidatedDate(new Date());
			try {
				sendUserEmail(user, welcomeMessageTemplateName, new HashMap());
			} catch (MessagingException e) {
				log.error("Error sending welcome email", e);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Authenticate a user.
	 * Login status will be updated in DB only if the user has validated their email address.
	 * If athentication is successful, the user's DB record is updated to reflect 
	 * their new login status, last login date, and login count.
	 */
	@Transactional
	public User authenticateUser(String username, String password) {
		User user = userDao.getUserByUsernameAndPassword(username, password);
		if (user != null && user.getStatus().equals(UserStatus.ACTIVE.getNumericStatus())) {
			user.initAssociationsAfterLogin();
			return user;
		}
		else {
			return null;
		}
	}

	public void update(User user) {
		userDao.update(user);
	}

	public User getUserById(Integer userId) {
		return userDao.getUserById(userId);
	}

	public User getUserByUsername(String username) {
		return userDao.getUserByUsername(username);
	}

	public User getUserByEmailAddress(String emailAddress) {
		return userDao.getUserByEmailAddress(emailAddress);
	}

	/**
	 * Update a user's password, first authenticating the old password
	 */
	public boolean updatePassword(User user, String oldPassword, String newPassword) {
		String oldHash = userDao.generatePasswordHash(oldPassword);
		if (!oldHash.equals(user.getPasswdHash())) {
			return false;
		}
		user.setPasswdHash(userDao.generatePasswordHash(newPassword));
		userDao.update(user);
		user.setPasswd(newPassword);
		return true;
	}

	/**
	 * Force update of a user's password (without authenticating old password)
	 * @param user
	 * @param newPassword
	 * @return
	 */
	public boolean updatePassword(User user, String newPassword) {
		user.setPasswdHash(userDao.generatePasswordHash(newPassword));
		userDao.update(user);
		user.setPasswd(newPassword);
		return true;
	}

	public void createUser(User user, boolean emailValid) throws DuplicateUserException {
		if (userDao.userExists(user.getUsername())) {
			throw new DuplicateUserException("That username has already been registered.");
		}
		if (userDao.getUserByEmailAddress(user.getEmailAddress()) != null) {
			throw new DuplicateUserException("A user has already registered with that email address.");
		}
		if (user.getUserPref() == null) {
			UserPref p = UserPref.getDefaultUserPref();
			p.setUser(user);
			user.setUserPref(p);
		}
		if (user.getUserGamePref() == null) {
			UserGamePref gp = UserGamePref.getDefaultUserGamePref();
			gp.setUser(user);
			user.setUserGamePref(gp);
		}
		if (user.getStatus() == null) {
			user.setStatus(UserStatus.ACTIVE.getNumericStatus());
		}
		String hash = userDao.generatePasswordHash(user.getPasswd());
		user.setPasswdHash(hash);
		if (user.getUserType() == UserConstants.USER_TYPE_HUMAN && !emailValid) {
			sendValidationEmail(user);
		}
		else {
			EmailValidation val = generateEmailValidation(user);
			val.setValidatedDate(new Date());
			val.setSentDate(new Date());
    		user.setEmailValidation(val);
			userDao.update(user);
			if (user.getUserType() == UserConstants.USER_TYPE_HUMAN) {
				try {
					sendUserEmail(user, welcomeMessageTemplateName, new HashMap());
				} catch (MessagingException e) {
					log.error("Error sending welcome email", e);
				}
			}
		}
	}
	
	
	private void sendUserEmail(User user, String emailTemplateName, Map rootMap) throws MessagingException {
		rootMap.put("user", user);
		EmailTemplate emailTemplate;
		try {
			emailTemplate = emailTemplateManager.getEmailMessage(emailTemplateName, rootMap);
		} catch (Exception e) {
			log.error("Error loading email message/template", e);
			throw new MessagingException();
		}
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(user.getEmailAddress());
		helper.setText(emailTemplate.getMessageBody(), true);
		helper.setFrom(emailTemplate.getFromAddress());
		helper.setSubject(emailTemplate.getSubject());
		mailSender.send(message);
	}
	
    public void sendValidationEmail(User user) {
    	EmailValidation val = user.getEmailValidation();
    	if (val == null) {
    		val = generateEmailValidation(user);
    		user.setEmailValidation(val);
    		userDao.update(user);
    	}
    	HashMap map = new HashMap();
    	try {
			map.put("vCode", URLEncoder.encode(user.getEmailValidation().getValidationCode(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error(e);
		}
    	try {
			sendUserEmail(user, emailValidationTemplateName, map);
			log.debug("Email sent to " + user.getEmailAddress() + " with validation code " + user.getEmailValidation().getValidationCode());
		} catch (MessagingException e) {
			log.error("Error sending validation email", e);
		}
		val.setSentDate(new Date());
		userDao.update(user);
	}

	/**
     * Generate an EmailValidation object for the given user.
     * @param user
     * @return
     */
    private EmailValidation generateEmailValidation(User user) {
    	EmailValidation val = new EmailValidation();
    	val.setUser(user);
    	val.setValidationCode(generateValidationCode(user.getUsername()));
    	// val.setSentDate(new Date());
    	return val;
    }

	private String generateValidationCode(String username) {
		return UUID.randomUUID().toString();
	}



	public void logAllUsersOut() {
		userDao.logAllUsersOut();
	}

	public void sendUserPasswordReminderEmail(User user) {
		String newPassword = PasswordGenerator.generateRandomPassword();
		updatePassword(user, newPassword);		
		try {
			sendUserEmail(user, passwordReminderTemplateName, new HashMap());
		} catch (MessagingException e) {
			log.error("Error sending password reminder email");
		}
		
	}


	public User getUserBySessionId(String sessionId) {
		return userDao.getUserBySessionId(sessionId);
	}

	public String[] getActiveUsernames() {
		List<User> activeUsers = userDao.getActiveUsers(); 
		String[] usernames = new String[activeUsers.size()];
		int i = 0;
		for (User user : activeUsers) {
			usernames[i++] = user.getUsername();
		}
		return usernames;
	}
	
	public Collection getActiveUsers() {
		return userDao.getActiveUsers(); 
	}

	public List<User> searchUsers(String username) {
		return userDao.searchUsers(username);
	}

	public void setEmailTemplateManager(EmailTemplateManager emailTemplateManager) {
		this.emailTemplateManager = emailTemplateManager;
	}

	public void setEmailValidationTemplateName(String emailValidationTemplateName) {
		this.emailValidationTemplateName = emailValidationTemplateName;
	}

	public void setWelcomeMessageTemplateName(String welcomeMessageTemplateName) {
		this.welcomeMessageTemplateName = welcomeMessageTemplateName;
	}

	public void setPasswordReminderTemplateName(String passwordReminderTemplateName) {
		this.passwordReminderTemplateName = passwordReminderTemplateName;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void createCPUPlayer(CPUPlayerRecord cpuPlayer, String username) throws DuplicateUserException {
		User user = new User();
		user.setUsername(username);
		user.setPasswd("pos93kd09");
		user.setUserType(cpuPlayer.getPlayerType());
		user.setCpuPlayer(cpuPlayer);
		user.setRegistrationDate(new Date());
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1973);
		cal.set(Calendar.MONTH, Calendar.JULY);
		cal.set(Calendar.DATE, 27);
		user.setBirthDate(cal.getTime());
		cpuPlayer.setUser(user);
		createUser(user, false);
		cpuPlayerDao.update(cpuPlayer);
	}

	public void setCpuPlayerDao(CPUPlayerDao cpuPlayerDao) {
		this.cpuPlayerDao = cpuPlayerDao;
	}

	public void updateCPUPlayer(CPUPlayerRecord cpuPlayer) {
		cpuPlayerDao.update(cpuPlayer);
	}

	public List<CPUPlayerRecord> getAllCPUPlayers() {
		List<CPUPlayerRecord> players = cpuPlayerDao.list();
		return players;
	}

	public CPUPlayerRecord getCPUPlayer(Integer userId) {
		return cpuPlayerDao.getByUserId(userId);
	}

	public List<CPUPlayerRecord> getAllPlayableCPUPlayers() {
		return cpuPlayerDao.listPlayable();
	}

}
