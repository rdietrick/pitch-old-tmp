package com.pitchplayer.userprofiling.om;
// Generated Oct 21, 2007 12:24:53 PM by Hibernate Tools 3.2.0.b11


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.pitchplayer.stats.om.GamePlayerRecord;
import com.pitchplayer.stats.om.GameRecord;
import com.pitchplayer.userprofiling.UserAssociationType;

/**
 * User generated by hbm2java
 */
public class User  implements java.io.Serializable {

	private Logger log = Logger.getLogger(this.getClass().getName());

     private Integer userId;
     private String username;
     private String passwd;
     private Byte status;
     private Date registrationDate;
     private Date birthDate;
     private Byte userType;
     private Boolean loggedIn;
     private String firstName;
     private String lastName;
     private String emailAddress;
     private Date lastLogin;
     private Integer loginCount;
     private String passwdHash;
     private String sessionId;
     private Set<GameRecord> gameRecordsForQuitterId = new HashSet<GameRecord>(0);
     private Set<GamePlayerRecord> gamePlayerRecords = new HashSet<GamePlayerRecord>(0);
     private Set<GameRecord> gameRecordsForWinnerId = new HashSet<GameRecord>(0);
     private UserHomeAddress userHomeAddress;
     private EmailValidation emailValidation;
     private UserPref userPref;
     private UserGamePref userGamePref;
     private CPUPlayerRecord cpuPlayer;
     private Set<UserInvitation> userInvitations = new HashSet<UserInvitation>(0);
     private Set<UserAssociation> userAssociations = new HashSet<UserAssociation>(0);
     private int currentRank;
     private Date lastRankUpdateDate;
     
    public User() {
    }

	
    public User(String username, String passwd, Date registrationDate) {
        this.username = username;
        this.passwd = passwd;
        this.registrationDate = registrationDate;
    }
    
    public User(String username, String passwd, Byte status,
    			Date registrationDate, Date birthDate,
    			Byte userType, Boolean loggedIn, String sessionId, String firstName, 
    			String lastName, String emailAddress, Date lastLogin, Integer loginCount, 
    			Set<GameRecord> gameRecordsForQuitterId, Set<GamePlayerRecord> gamePlayerRecords, 
    			Set<GameRecord> gameRecordsForWinnerId, UserHomeAddress userHomeAddress, 
    			EmailValidation emailVal, CPUPlayerRecord cpuPlayer,
    			Set<UserInvitation> userInvitations, 
    			Set<UserAssociation> userAssociations, UserPref userPref, 
    			UserGamePref userGamePref) {
       this.username = username;
       this.passwd = passwd;
       this.status = status;
       this.registrationDate = registrationDate;
       this.birthDate = birthDate;
       this.userType = userType;
       this.loggedIn = loggedIn;
       this.sessionId = sessionId;
       this.firstName = firstName;
       this.lastName = lastName;
       this.emailAddress = emailAddress;
       this.lastLogin = lastLogin;
       this.loginCount = loginCount;
       this.gameRecordsForQuitterId = gameRecordsForQuitterId;
       this.gamePlayerRecords = gamePlayerRecords;
       this.gameRecordsForWinnerId = gameRecordsForWinnerId;
       this.userHomeAddress = userHomeAddress;
       this.emailValidation = emailVal;
       this.cpuPlayer = cpuPlayer;
       this.userInvitations = userInvitations;
       this.userAssociations = userAssociations;
       this.userPref = userPref;
       this.userGamePref = userGamePref;
    }
   
    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPasswd() {
        return this.passwd;
    }
    
    public void setPasswd(String passwd) {
       this.passwd = passwd;
    }
    
	public String getPasswdHash() {
		return this.passwdHash;
	}
	
	public void setPasswdHash(String s) {
		this.passwdHash = s;
    }
    
    public Date getRegistrationDate() {
        return this.registrationDate;
    }
    
    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
    public Byte getUserType() {
        return this.userType;
    }
    
    public void setUserType(Byte userType) {
        this.userType = userType;
    }
    public Boolean getLoggedIn() {
        return this.loggedIn;
    }
    
    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    public String getFirstName() {
        return this.firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmailAddress() {
        return this.emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public Date getLastLogin() {
        return this.lastLogin;
    }
    
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
    public Integer getLoginCount() {
        return this.loginCount;
    }
    
    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }
    public Set<GameRecord> getGameRecordsForQuitterId() {
        return this.gameRecordsForQuitterId;
    }
    
    public void setGameRecordsForQuitterId(Set<GameRecord> gameRecordsForQuitterId) {
        this.gameRecordsForQuitterId = gameRecordsForQuitterId;
    }
    public Set<GamePlayerRecord> getGamePlayerRecords() {
        return this.gamePlayerRecords;
    }
    
    public void setGamePlayerRecords(Set<GamePlayerRecord> gamePlayerRecords) {
        this.gamePlayerRecords = gamePlayerRecords;
    }
    public Set<GameRecord> getGameRecordsForWinnerId() {
        return this.gameRecordsForWinnerId;
    }
    
    public void setGameRecordsForWinnerId(Set<GameRecord> gameRecordsForWinnerId) {
        this.gameRecordsForWinnerId = gameRecordsForWinnerId;
    }
    public UserHomeAddress getUserHomeAddress() {
        return this.userHomeAddress;
    }
    
    public void setUserHomeAddress(UserHomeAddress userHomeAddress) {
        this.userHomeAddress = userHomeAddress;
    }

	public EmailValidation getEmailValidation() {
		return emailValidation;
	}


	public void setEmailValidation(EmailValidation emailValidation) {
		this.emailValidation = emailValidation;
	}


	public Date getBirthDate() {
		return birthDate;
	}


	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}


	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	public boolean isEmailValidated() {
		EmailValidation emailVal = getEmailValidation();
		return (emailVal != null && emailVal.getValidatedDate() != null);
	}


	public CPUPlayerRecord getCpuPlayer() {
		return cpuPlayer;
	}


	public void setCpuPlayer(CPUPlayerRecord cpuPlayer) {
		this.cpuPlayer = cpuPlayer;
	}


    public Set<UserInvitation> getUserInvitations() {
        return this.userInvitations;
    }
    
    public void setUserInvitations(Set<UserInvitation> userInvitations) {
        this.userInvitations = userInvitations;
    }
    public Set<UserAssociation> getUserAssociations() {
        return this.userAssociations;
    }
    
    public void setUserAssociations(Set<UserAssociation> userAssociations) {
        this.userAssociations = userAssociations;
    }


	public UserPref getUserPref() {
		return userPref;
	}


	public void setUserPref(UserPref userPref) {
		this.userPref = userPref;
	}


	public UserGamePref getUserGamePref() {
		return userGamePref;
	}


	public void setUserGamePref(UserGamePref userGamePref) {
		this.userGamePref = userGamePref;
	}


	public Byte getStatus() {
		return status;
	}


	public void setStatus(Byte status) {
		this.status = status;
	}

	
	private List<User> friends = null;
	
	public Collection<User> getFriends() {
		// TODO: might want to eventually force a refresh of user associations from the DB
		
		if (friends == null) {
			log.debug("initializing friends list");
			friends = new ArrayList<User>();
			for (UserAssociation assoc : userAssociations) {
				if (assoc.getAssociationType() == UserAssociationType.FRIEND.getDbValue() && 
						assoc.getStatus() == UserAssociation.STATUS_CONFIRMED) {
					User friend = assoc.getUserByAssociateId();
					friends.add(friend);
				}
			}
		}
		return friends;
	}


	public void initAssociationsAfterLogin() {
		getFriends();
	}


	public int getCurrentRank() {
		return currentRank;
	}


	public void setCurrentRank(int currentRank) {
		this.currentRank = currentRank;
		this.lastRankUpdateDate = new Date();
	}


	public Date getLastRankUpdateDate() {
		return lastRankUpdateDate;
	}

}

