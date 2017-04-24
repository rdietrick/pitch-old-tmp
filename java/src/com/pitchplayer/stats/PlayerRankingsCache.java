package com.pitchplayer.stats;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pitchplayer.common.ResultsPage;
import com.pitchplayer.stats.om.PlayerStat;
import com.pitchplayer.userprofiling.UserStore;


public class PlayerRankingsCache {
	
	private PlayerStatService playerStatService;
	private Map<String, Integer> rankingsCache = new HashMap<String, Integer>(100);
	Logger log = Logger.getLogger(this.getClass().getName());
	Date lastUpdateDate;
	
	
	@SuppressWarnings("unchecked")
	public void refreshCache() {
		ResultsPage results = playerStatService.getPlayerStats(0, -1);
		List<PlayerStat> stats = results.getResults();
		int i=1;
		HashMap<String, Integer> map = new HashMap<String, Integer>(stats.size());
		for (PlayerStat stat : stats) {
			map.put(stat.getUsername(), i++);
		}
		synchronized(this) {
			rankingsCache = map;
			lastUpdateDate = new Date();
		}
 	}
	
	/**
	 * Returns a player's cached rank or -1 if they are not ranked.
	 * @param username
	 * @return
	 */
	public int getPlayerRank(String username) {
		Integer rank = rankingsCache.get(username);
		if (rank != null) {
			return rank;
		}
		else return -1;
	}
	
	public void setPlayerStatService(PlayerStatService playerStatService) {
		this.playerStatService = playerStatService;
	}

	public void setUserStore(UserStore userStore) {
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	
}
