package com.pitchplayer.userprofiling.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pitchplayer.userprofiling.om.CPUPlayerRecord;

public class CPUPlayerDao extends HibernateDaoSupport {

	public void update(CPUPlayerRecord cpuPlayer) {
		getHibernateTemplate().saveOrUpdate(cpuPlayer);
	}
	
	public List<CPUPlayerRecord> list() {
		return getHibernateTemplate().find("from CPUPlayerRecord");
	}

	public CPUPlayerRecord getByUserId(Integer userId) {
		return (CPUPlayerRecord)getHibernateTemplate().get(CPUPlayerRecord.class, userId);
	}

	public List<CPUPlayerRecord> listPlayable() {
		return getHibernateTemplate().find("from CPUPlayerRecord p where p.status != 'DISABLED'");
	}
	
	
}
