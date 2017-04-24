package com.pitchplayer.userprofiling.om;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import com.pitchplayer.common.DbCapableManager;
import com.pitchplayer.db.DbException;
import com.pitchplayer.userprofiling.DuplicateUserException;
import com.pitchplayer.util.HibernateUtil;

public class UserManager extends DbCapableManager {
	
	Logger log = Logger.getLogger(this.getClass().getName());
    
	public static void main(String[] args) {
        UserManager mgr = new UserManager();
        HibernateUtil.getSessionFactory().close();
    }

    /**
     * Save a user (and associated UserInfo) to the database. 
     * @param user
     * @throws DbException
     * @throws MessagingException 
     */
    public void createUser(User user) throws DbException, DuplicateUserException, MessagingException {
    	Session session = null;
    	Transaction tx = null;
    	try {
			session = getCurrentSession();
    		tx = session.beginTransaction();
			String hash = generatePasswordHash(session, user.getPasswd());
			user.setPasswdHash(hash);
			EmailValidation val = generateEmailValidation(user);
			user.setEmailValidation(val);
    		session.save(user);
         	tx.commit();
    	} catch (Exception he) {
    		if (tx != null) {
    			tx.rollback();
    		}
    		log.error("Error retrieving session/factory", he);
    		throw new DbException("Error saving user", he);
    	}
    }
    
    
    /**
     * Generate an EmailValidation object for the given user.
     * @param user
     * @return
     */
    private EmailValidation generateEmailValidation(User user) {
    	EmailValidation val = new EmailValidation();
    	val.setUser(user);
    	val.setValidationCode(generateValidationCode(user.getUsername()));
    	val.setSentDate(new Date());
    	return val;
    }
    
    
    /**
     * TODO: re-implement method to return a unique code for the given username.
     * @param username
     * @return
     */
    private String generateValidationCode(String username) {
		return "code";
	}

	public User authenticateUser(String username, String password) throws DbException {
    	Session session = null;
       	Transaction tx = null;
       	try {
       		session = getCurrentSession();
       		tx = session.beginTransaction();
       		Criteria crit = session.createCriteria(User.class);
        	crit.add(Restrictions.eq("username", username));
    		crit.add(Restrictions.eq("passwdHash", generatePasswordHash(password)));
    		List l = crit.list();
    		if (!l.isEmpty()) {
    			return (User)l.get(0);
    		}
          	tx.commit();
          	return null;
    	} catch (HibernateException he) {
    		log.error("Error querying for user", he);
    		if (tx != null) {
    			try { tx.rollback(); } catch (Throwable t) {}
    		}
    		throw new DbException("Error querying for user", he);
    	} finally {
    		// session.close();
    	}
    }
    
    public User getUser(String username) throws DbException {
    	return null;
    }
    
    public String getUser(long userId) throws DbException {
    	return null;
    }

    
	public String generatePasswordHash(String password) {
    	Session session = getCurrentSession();
    	try {
    		return generatePasswordHash(session, password);
    	} finally {
    		session.close();
    	}
	}
	
	public static String generatePasswordHash(Session session, String password) {
       	Query query = session.createSQLQuery("SELECT password(?) FROM DUAL");
       	query.setString(0, password);
       	List l = query.list();
       	return (String)l.get(0);
	}

}
