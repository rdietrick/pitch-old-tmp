package com.pitchplayer.server.game.player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pitchplayer.server.game.GameType;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.CPUPlayerRecord;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.util.CPUPlayerRecordUpdateListener;
import com.pitchplayer.util.PitchUtil;

/**
 * Factory for creating computer players to be added to games.
 * CPUPlayers must be requested by GameType (SINGLES/DOUBLES).
 * @author robd
 *
 */
public class CPUPlayerFactory implements CPUPlayerRecordUpdateListener {

	private UserService userService;
	private Logger log = Logger.getLogger(this.getClass().getName());

	List<com.pitchplayer.userprofiling.om.CPUPlayerRecord> allCPUPlayers;
	List<com.pitchplayer.userprofiling.om.CPUPlayerRecord> singlesCPUPlayers;
	List<com.pitchplayer.userprofiling.om.CPUPlayerRecord> doublesCPUPlayers;
	List<com.pitchplayer.userprofiling.om.CPUPlayerRecord> simSinglesCPUPlayers;
	List<com.pitchplayer.userprofiling.om.CPUPlayerRecord> simDoublesCPUPlayers;
	private Object playersLock = new Object();
	
	public CPUPlayerFactory() {	
	}
	
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void init() {
		synchronized(playersLock) {
			allCPUPlayers = userService.getAllPlayableCPUPlayers();
			singlesCPUPlayers = new ArrayList<com.pitchplayer.userprofiling.om.CPUPlayerRecord>();
			doublesCPUPlayers = new ArrayList<com.pitchplayer.userprofiling.om.CPUPlayerRecord>();
			simSinglesCPUPlayers = new ArrayList<com.pitchplayer.userprofiling.om.CPUPlayerRecord>();
			simDoublesCPUPlayers = new ArrayList<com.pitchplayer.userprofiling.om.CPUPlayerRecord>();
			for (CPUPlayerRecord p : allCPUPlayers) {
				if (p.getPlayerType() == GameType.SINGLES.getDbFlag()) {
					simSinglesCPUPlayers.add(p);
					if (!p.getStatus().equals(CPUPlayerRecord.Status.SIM_ONLY.toString())) {
						singlesCPUPlayers.add(p);
					}
				}
				else if (p.getPlayerType() == GameType.DOUBLES.getDbFlag()) {
					simDoublesCPUPlayers.add(p);
					if (!p.getStatus().equals(CPUPlayerRecord.Status.SIM_ONLY.toString())) {
						doublesCPUPlayers.add(p);
					}
				}
			}
		}
	}
	
	/**
	 * Get a computer player using one of the enum values.
	 * If playerName is null, the name in the database for that player will be used.
	 * @param player
	 * @return
	 */
	protected CPUPlayer loadCPUPlayer(com.pitchplayer.userprofiling.om.CPUPlayerRecord player) {
		String className = player.getClassName();
		Class playerClass = null;
		Class[] constructorArgs = new Class[] {User.class};
		Object[] args = new Object[] { player.getUser() };
		Constructor constructor = null;
		CPUPlayer cpuPlayer = null;
		try {
			playerClass = Class.forName(className);
			constructor = playerClass.getConstructor(constructorArgs);
			cpuPlayer = (CPUPlayer)createObject(constructor, args);
		} catch (Exception e) {
			log.error("Error creating CPUPlayer", e);
		}
		return cpuPlayer;
	}
	
	private static Object createObject(Constructor constructor,
			Object[] arguments) throws Exception {
		Object object = null;
		object = constructor.newInstance(arguments);
		return object;
	}

	private List<CPUPlayerRecord> getPlayerPool(GameType gameType) {
		List<CPUPlayerRecord> pool = allCPUPlayers;
		if (gameType == GameType.SINGLES && singlesCPUPlayers.size() >= 3) {
			pool = singlesCPUPlayers;
		}
		else if (gameType == GameType.DOUBLES && doublesCPUPlayers.size() > 3) {
			pool = doublesCPUPlayers;
		}
		else if (gameType == GameType.SIM_SINGLES && simSinglesCPUPlayers.size() >= 3) {
			pool = simSinglesCPUPlayers;
		}
		else if (gameType == GameType.SIM_DOUBLES && simDoublesCPUPlayers.size() > 3) {
			pool = simDoublesCPUPlayers;
		}
		return pool;
	}
	
	/**
	 * Get a random computer player.
	 * @param playerName
	 * @return
	 */
	public CPUPlayer getRandomCPUPlayer(GameType gameType) {
		List<CPUPlayerRecord> pool = getPlayerPool(gameType);
		com.pitchplayer.userprofiling.om.CPUPlayerRecord cpuPlayer = 
			pool.get(PitchUtil.getRandomInt(0, pool.size()));
		return loadCPUPlayer(cpuPlayer);
	}
	
	/**
	 * Returns a random CPUPlayer from the pool of configured players for the given game type.
	 * @param gameType the type of game for which the player is requested.
	 * @param excludeUserIds the IDs of users already in the game, so that a duplicate is not retrieved
	 * @return
	 */
	public CPUPlayer getRandomCPUPlayer(GameType gameType, int[] excludeUserIds) {
		List<CPUPlayerRecord> pool = getPlayerPool(gameType);
		
		// build an array of players who are not in the exclude list
		com.pitchplayer.userprofiling.om.CPUPlayerRecord[] includes = 
			new com.pitchplayer.userprofiling.om.CPUPlayerRecord[pool.size() - excludeUserIds.length];
		int i = 0;
		outer: for (com.pitchplayer.userprofiling.om.CPUPlayerRecord p : pool) {
			if (excludeUserIds != null && excludeUserIds.length > 0) {
				for (int userId : excludeUserIds) {
					if (userId == p.getUserId()) {
						continue outer;
					}
				}
			}
			includes[i++] = p;
		}
		
		return loadCPUPlayer(includes[PitchUtil.getRandomInt(0, includes.length)]);
	}

	/**
	 * Notify this object that a CPUPlayer was updated so that it can be refreshed in the cache.
	 */
	public void playerUpdated(CPUPlayerRecord player) {
		// for now just re-read the whole list of players from the DB, it's very small
			init();
	}

	/**
	 * Get a CPUPlayer by userId
	 * @param userId
	 * @return
	 */
	public CPUPlayer getCPUPlayer(int userId) {
		CPUPlayer player = null;
		for (CPUPlayerRecord p : allCPUPlayers) {
			if (p.getUserId() == userId) {
				player = loadCPUPlayer(p);
			}
		}
		return player;
	}


}
