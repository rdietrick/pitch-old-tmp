package com.pitchplayer.userprofiling.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.pitchplayer.db.DuplicateRecordException;
import com.pitchplayer.userprofiling.UserAssociationType;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserAssociation;

public class UserAssociationDaoHibernate extends HibernateDaoSupport implements
		UserAssociationDao {

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public List<UserAssociation> getUserAssociations(Integer userId, UserAssociationType assocType) {
		return getHibernateTemplate().find("from UserAssociation u where u.userByUserId.userId = ? and u.associationType = ? and u.status = ?", 
				new Object[] {userId, new Byte(assocType.getDbValue()), new Integer(UserAssociation.STATUS_CONFIRMED)});

	}
	
	public void update(UserAssociation assoc) throws DuplicateRecordException {	
		try {
			getHibernateTemplate().saveOrUpdate(assoc);
		} catch (DataAccessException dae) {
			if (ConstraintViolationException.class.isAssignableFrom(dae.getCause().getClass())) {
				throw new DuplicateRecordException("Association already exists");					
			}
			else {
				throw dae;
			}
		}
	}

	public UserAssociation getUserAssociation(Integer userId,
			Integer associateId, UserAssociationType associationType) {
		List l = getHibernateTemplate().find("from UserAssociation u where u.userByUserId.userId = ? and u.userByAssociateId.userId = ? and u.associationType = ?",
				new Object[] {userId, associateId, new Byte(associationType.getDbValue())});
		if (l.size() > 0) {
			return (UserAssociation)l.get(0);
		}
		return null;
	}

	public void delete(UserAssociation assoc) {
		getHibernateTemplate().delete(assoc);
	}

	public List<UserAssociation> getUserAssociations(User user, UserAssociationType associationType, Integer status) {
		String query = "from UserAssociation ua where ua.userByUserId.userId = ? "
			+ " and ua.associationType = ?"
			+ (status != null ? " and status = ?":"");
		List<UserAssociation> assocs;
		if (status == null) {
			assocs = (List<UserAssociation>)getHibernateTemplate().find(query, new Object[] {user.getUserId(), new Byte(associationType.getDbValue())});
		}
		else {
			assocs = (List<UserAssociation>)getHibernateTemplate().find(query, new Object[] {user.getUserId(), new Byte(associationType.getDbValue()), status});
		}
// not sure why this mess was here or what it was doing, so commented out
//		if (assocs != null && assocs.size() > 1) {
//			TreeSet<UserAssociation> set = new TreeSet<UserAssociation>(); 
//			set.addAll(assocs);
//			log.debug("added all");
//			assocs.clear();
//			assocs.addAll(set);
//		}
//		else {
//			log.debug("no assocs");
//		}
		return assocs;
	}

	public List<UserAssociation> getPendingAssociations(User user,
			UserAssociationType type) {
		String query = "from UserAssociation ua where ua.userByAssociateId.userId = ? and ua.associationType = ? and status = ?";
		return getHibernateTemplate().find(query, new Object[] {
				user.getUserId(), type.getDbValue(), UserAssociation.STATUS_REQUESTED });
	}

}
