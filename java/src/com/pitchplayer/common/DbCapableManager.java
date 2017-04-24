package com.pitchplayer.common;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class DbCapableManager {

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	protected Session getCurrentSession() {
		return this.sessionFactory.getCurrentSession();
	}
	
}
