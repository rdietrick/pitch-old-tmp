package com.pitchplayer.userprofiling.action;

import java.util.List;

import com.pitchplayer.action.BaseAction;
import com.pitchplayer.userprofiling.DuplicateUserException;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.CPUPlayerRecord;
import com.pitchplayer.userprofiling.util.CPUPlayerRecordUpdateListener;
import com.pitchplayer.userprofiling.util.CPUPlayerRecordUpdateNotifier;

public class CPUPlayerAction extends BaseAction implements CPUPlayerRecordUpdateNotifier {

	private UserService userService;
	private CPUPlayerRecord cpuPlayer;
	private String username;
	
	private List<CPUPlayerRecord> allCPUPlayers = null;
	private Integer userId;
	private List<CPUPlayerRecordUpdateListener> updateListeners;
	
	public String showAllCPUPlayers() {
		allCPUPlayers = userService.getAllCPUPlayers();
		return SUCCESS;
	}
	
	public String createCPUPlayer() {
		try {
			if (!checkCPUClass(cpuPlayer.getClassName())) {
				addActionError("Class does not appear to be a subclass of com.pitchplayer.server.game.player.CPUPlayer");
				return ERROR;
			}
		} catch (ClassNotFoundException e1) {
			addActionError("Class '" + cpuPlayer.getClassName() + "' not found");
			return ERROR;
		}
		try {
			userService.createCPUPlayer(cpuPlayer, username);
		} catch (DuplicateUserException e) {
			addActionError("Username already taken; please choose another");
			return ERROR;
		} catch (Exception e) {
			log.error("Error creating player", e);
			addActionError("Error adding player :" + e.getMessage());
			return ERROR;
		}
		notifyPlayerUpdated(cpuPlayer);
		return SUCCESS;
	}
	
	public String updateCPUPlayer() {
		CPUPlayerRecord savedPlayer = null;
		try {
			savedPlayer = userService.getCPUPlayer(cpuPlayer.getUserId());
		} catch (Exception e) {
			log.error("Error retrieving player", e);
			addActionError("Error retrieving player to update");
			return ERROR;
		}
		savedPlayer.setClassName(cpuPlayer.getClassName());
		savedPlayer.setPlayerType(cpuPlayer.getPlayerType());
		savedPlayer.setSkillLevel(cpuPlayer.getSkillLevel());
		savedPlayer.setStatus(cpuPlayer.getStatus());
		cpuPlayer = savedPlayer;
		try {
			if (!checkCPUClass(cpuPlayer.getClassName())) {
				addActionError("Class does not appear to be a subclass of com.pitchplayer.server.game.player.CPUPlayer");
				return ERROR;
			}
		} catch (ClassNotFoundException e1) {
			addActionError("Class '" + cpuPlayer.getClassName() + "' not found");
			return ERROR;
		}
		try {
			userService.updateCPUPlayer(cpuPlayer);
		} catch (Exception e) {
			log.error("Error updating CPUPlayerRecord", e);
			addActionError(e.getMessage());
			return ERROR;
		}
		notifyPlayerUpdated(cpuPlayer);
		return SUCCESS;
		
	}
	
	private boolean checkCPUClass(String className) throws ClassNotFoundException {
		Class c = Class.forName(className);
		if (com.pitchplayer.server.game.player.CPUPlayer.class.isAssignableFrom(c)) {
			return true;
		}
		return false;
	}

	public String viewCPUPlayer() {
		try {
			cpuPlayer = userService.getCPUPlayer(userId);
		} catch (Exception e) {
			log.error("Error retrieving user", e);
			addActionError("Error retrieving user");
			return ERROR;
		}
		return SUCCESS;
	}


	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public List getAllCPUPlayers() {
		return allCPUPlayers;
	}

	public CPUPlayerRecord getCpuPlayer() {
		return cpuPlayer;
	}

	public void setCpuPlayer(CPUPlayerRecord cpuPlayer) {
		this.cpuPlayer = cpuPlayer;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void addListener(CPUPlayerRecordUpdateListener listener) {
		synchronized(updateListeners) {
			updateListeners.add(listener);
		}
	}

	public void notifyPlayerUpdated(CPUPlayerRecord player) {
		for (CPUPlayerRecordUpdateListener listener : updateListeners) {
			listener.playerUpdated(player);
		}
	}

	public void setUpdateListeners(
			List<CPUPlayerRecordUpdateListener> updateListeners) {
		this.updateListeners = updateListeners;
	}

}
