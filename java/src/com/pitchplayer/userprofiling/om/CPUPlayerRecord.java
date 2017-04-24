package com.pitchplayer.userprofiling.om;

public class CPUPlayerRecord {

	private Integer userId;
	private User user;
	private String className;
    private Byte playerType;
    private Integer skillLevel;
    private String status;

    public enum Status {
    	ENABLED, DISABLED, SIM_ONLY;
    }
    
	public CPUPlayerRecord() {
		
	}

	public CPUPlayerRecord(int userId, User user) {
        this.userId = userId;
        this.user = user;
    }
    public CPUPlayerRecord(int userId, User user, String className, Byte playerType, Integer skillLevel,
    		String status) {
       this.userId = userId;
       this.user = user;
       this.className = className;
       this.playerType = playerType;
       this.skillLevel = skillLevel;
       this.status = status;
    }
   	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
    public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Byte getPlayerType() {
		return playerType;
	}

	public void setPlayerType(Byte playerType) {
		this.playerType = playerType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(Integer skillLevel) {
		this.skillLevel = skillLevel;
	}
	
}
