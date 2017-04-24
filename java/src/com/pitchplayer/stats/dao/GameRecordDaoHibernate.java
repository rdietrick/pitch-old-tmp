package com.pitchplayer.stats.dao;

import java.util.List;
import java.util.Set;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pitchplayer.stats.om.GamePlayerRecord;
import com.pitchplayer.stats.om.GameRecord;

public class GameRecordDaoHibernate extends HibernateDaoSupport implements GameRecordDao {

	public GameRecord getGameRecordById(Integer gameRecordId) {
		return (GameRecord)getHibernateTemplate().get(GameRecord.class, gameRecordId);
	}

	public List<GameRecord> getGameRecordsByPlayerId(Integer playerId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void update(GameRecord gameRecord) {
		getHibernateTemplate().saveOrUpdate(gameRecord);
	}

	public void delete(GameRecord gameRecord) {
		getHibernateTemplate().delete(gameRecord);
	}

}
