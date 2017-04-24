package com.pitchplayer.userprofiling.om;

// Generated Oct 10, 2008 4:02:44 PM by Hibernate Tools 3.2.1.GA

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class EmailValidation.
 * @see com.pitchplayer.userprofiling.om.EmailValidation
 * @author Hibernate Tools
 */
public class EmailValidationHome {

	private static final Log log = LogFactory.getLog(EmailValidationHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(EmailValidation transientInstance) {
		log.debug("persisting EmailValidation instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(EmailValidation instance) {
		log.debug("attaching dirty EmailValidation instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(EmailValidation instance) {
		log.debug("attaching clean EmailValidation instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(EmailValidation persistentInstance) {
		log.debug("deleting EmailValidation instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public EmailValidation merge(EmailValidation detachedInstance) {
		log.debug("merging EmailValidation instance");
		try {
			EmailValidation result = (EmailValidation) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public EmailValidation findById(int id) {
		log.debug("getting EmailValidation instance with id: " + id);
		try {
			EmailValidation instance = (EmailValidation) sessionFactory
					.getCurrentSession().get(
							"com.pitchplayer.userprofiling.om.EmailValidation",
							id);
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

	public List findByExample(EmailValidation instance) {
		log.debug("finding EmailValidation instance by example");
		try {
			List results = sessionFactory.getCurrentSession().createCriteria(
					"com.pitchplayer.userprofiling.om.EmailValidation").add(
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
