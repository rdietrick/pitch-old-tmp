package com.pitchplayer.util;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.cfg.*;

public class HibernateUtil {
	
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
            Logger.getLogger("com.pitchplayer.util.HibernateUtil").error("Initial SessionFactory creation successful.");
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            Logger.getLogger("com.pitchplayer.util.HibernateUtil").error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static Session getCurrentSession() {
    	return sessionFactory.getCurrentSession();
    }
    
    public static Session openSession(Interceptor interceptor) {
    	return sessionFactory.openSession(interceptor);
    }
    
}
