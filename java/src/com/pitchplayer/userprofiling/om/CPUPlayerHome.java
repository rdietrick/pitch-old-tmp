package com.pitchplayer.userprofiling.om;

// Generated Oct 10, 2008 12:00:14 PM by Hibernate Tools 3.2.1.GA

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class CPUPlayerRecord.
 * @see com.pitchplayer.userprofiling.om.CPUPlayerRecord
 * @author Hibernate Tools
 */
public class CPUPlayerHome {

	private static final Log log = LogFactory.getLog(CPUPlayerHome.class);

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void persist(CPUPlayerRecord transientInstance) {
		log.debug("persisting CPUPlayerRecord instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(CPUPlayerRecord instance) {
		log.debug("attaching dirty CPUPlayerRecord instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(CPUPlayerRecord instance) {
		log.debug("attaching clean CPUPlayerRecord instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(CPUPlayerRecord persistentInstance) {
		log.debug("deleting CPUPlayerRecord instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public CPUPlayerRecord merge(CPUPlayerRecord detachedInstance) {
		log.debug("merging CPUPlayerRecord instance");
		try {
			CPUPlayerRecord result = (CPUPlayerRecord) sessionFactory.getCurrentSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public CPUPlayerRecord findById(java.lang.Integer id) {
		log.debug("getting CPUPlayerRecord instance with id: " + id);
		try {
			CPUPlayerRecord instance = (CPUPlayerRecord) sessionFactory.getCurrentSession().get(
					"com.pitchplayer.userprofiling.om.CPUPlayerRecord", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(CPUPlayerRecord instance) {
		log.debug("finding CPUPlayerRecord instance by example");
		try {
			List results = sessionFactory.getCurrentSession().createCriteria(
					"com.pitchplayer.userprofiling.om.CPUPlayerRecord").add(
					Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
