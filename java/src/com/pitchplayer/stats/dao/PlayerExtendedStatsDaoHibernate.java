package com.pitchplayer.stats.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pitchplayer.stats.om.PlayerExtendedStats;

public class PlayerExtendedStatsDaoHibernate extends HibernateDaoSupport implements PlayerExtendedStatsDao {

	public PlayerExtendedStats getExtendedStats(String username) {
		List l = getHibernateTemplate().find("from PlayerExtendedStats p where p.username = ?", username);
		if (l.size() > 0) {
			return (PlayerExtendedStats)l.get(0);
		}
		else {
			return null;
		}		
	}

	public PlayerExtendedStats getExtendedStats(Integer userId) {
		List l = getHibernateTemplate().find("from PlayerExtendedStats p where p.userId = ?", userId);
		if (l.size() > 0) {
			return (PlayerExtendedStats)l.get(0);
		}
		else {
			return null;
		}		
	}

}
