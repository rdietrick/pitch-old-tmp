 package com.pitchplayer.stats.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pitchplayer.common.ResultsPage;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.stats.om.PlayerExtendedStats;
import com.pitchplayer.stats.om.PlayerStat;

public class PlayerStatDaoHibernate extends HibernateDaoSupport implements PlayerStatDao {

    
	public ResultsPage getPlayerStats(final GameType gameType, final int offset, final int count) {
		final String queryString = "from PlayerStat as p where p.gameType = :gameType and p.games > 0 order by p.winPct desc";
		int size = ((List<PlayerStat>)getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query q = session.createQuery(queryString);
				q.setParameter("gameType", gameType.getDbFlag());
				return q.list(); 
			}
		})).size();
		List results = (List<PlayerStat>)getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
			throws HibernateException, SQLException {
				final Query q = session.createQuery(queryString);
				q.setParameter("gameType", gameType.getDbFlag());
				q.setFirstResult(offset);
				if (count > -1) {
					q.setMaxResults(count);
				}
				return q.list(); 
			}
		});
		return new ResultsPage(results, size, offset);
	}

	public Map<GameType, PlayerStat> getPlayerStats(Integer userId) {
		List l = getHibernateTemplate().find("from PlayerStat p where p.user.userId = ? order by p.gameType asc", userId);
		return null;
	}

	public Map<GameType, PlayerStat> getPlayerStats(String username) {
		List l = getHibernateTemplate().find("from PlayerStat p where p.username = ? order by p.gameType asc", username);
		return null;
	}

	public ResultsPage getPlayerStats(int offset, int limit) {
		throw new RuntimeException("Method not implemented");
	}
	

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

	public Map<Integer, PlayerExtendedStats> getExtendedStatsForUserIds(
			Collection<Integer> userIds) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, PlayerExtendedStats> getExtendedStatsForUsernames(
			Collection<String> usernames) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<Integer, PlayerStat> getPlayerStatsForUserIds(
			Collection<Integer> userIds) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, PlayerStat> getPlayerStatsForUsernames(
			Collection<String> usernames) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getRankForUser(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getRankForUser(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<Integer, Integer> getRanksForUserIds(Collection<Integer> userIds) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Integer> getRanksForUsernames(
			Collection<String> usernames) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteSimStats(GameType gameType) {
		// TODO Auto-generated method stub
	}

	public ResultsPage getPlayersRankingsPage(Integer userId, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

}
