package com.pitchplayer.userprofiling;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.LockMode;

import com.pitchplayer.db.DbException;
import com.pitchplayer.db.DuplicateRecordException;
import com.pitchplayer.userprofiling.dao.UserAssociationDao;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserAssociation;
import com.pitchplayer.util.HibernateUtil;

public class UserAssociationServiceImpl implements UserAssociationService {

	UserAssociationDao userAssociationDao;

	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public UserAssociationDao getUserAssociationDao() {
		return userAssociationDao;
	}

	public void setUserAssociationDao(UserAssociationDao userAssociationDao) {
		this.userAssociationDao = userAssociationDao;
	}
	
	private UserAssociation getUserAssociation(User user, User associate, UserAssociationType aType) {
		return userAssociationDao.getUserAssociation(user.getUserId(), associate.getUserId(), aType);
	}

	private UserAssociation createNewAssociation(User user, User associate, UserAssociationType aType, int status) {
		Date now = new Date();
		UserAssociation assoc = new UserAssociation(user, associate, aType.getDbValue(), now);
		assoc.setDateAccepted(now);
		assoc.setStatus(status);
		return assoc;
	}

	/**
	 * Creates a bidirectional, confirmed association between the two users.
	 * @param user
	 * @param associate
	 * @param associationType
	 */
	public void associateUsers(User user, User associate, UserAssociationType aType) throws DbException {
		Set<UserAssociation> associations = user.getUserAssociations();
		boolean found = false;
		for (UserAssociation assoc : associations) {
			if (assoc.getUserByAssociateId().getUsername().equals(associate.getUsername()) &&
					assoc.getAssociationType() == aType.getDbValue()) {
				found = true;
				if (assoc.getStatus() != UserAssociation.STATUS_CONFIRMED) {
					assoc.setStatus(UserAssociation.STATUS_CONFIRMED);
					try {
						userAssociationDao.update(assoc);
						log.debug("user -> associate confirmed");
					} catch (DuplicateRecordException e) {
						// ignore
					}
					log.debug("updated UserAssociation with id " + assoc.getUserAssociationId());
				}
			}
		}
		if (!found) {
			log.debug("user -> associate not found");
			UserAssociation assoc = createNewAssociation(user, associate, aType, UserAssociation.STATUS_CONFIRMED);
			associations.add(assoc);
			// userAssociationDao.create(assoc);
			userAssociationDao.update(assoc);
			log.debug("user -> associate created");				
			log.debug("created new UserAssociation with id " + assoc.getUserAssociationId());
		}
		// since the associate is not associated with this session, we need to use this method
		List<UserAssociation> associateAssociations = userAssociationDao.getUserAssociations(associate, aType, null);
		found = false;
		for (UserAssociation assoc : associateAssociations) {
			if (assoc.getUserByAssociateId().getUsername().equals(user.getUsername()) &&
					assoc.getAssociationType() == aType.getDbValue()) {
				found = true;
				if (assoc.getStatus() != UserAssociation.STATUS_CONFIRMED) {
					assoc.setStatus(UserAssociation.STATUS_CONFIRMED);
					try {
						userAssociationDao.update(assoc);
						log.debug("associate -> user confirmed");
					} catch (DuplicateRecordException e) {
						// ignore
					}
					log.debug("updated UserAssociation with id " + assoc.getUserAssociationId());
				}
			}
		}
		if (!found) {
			log.debug("associate -> user not found");
			UserAssociation assoc = createNewAssociation(associate, user, aType, UserAssociation.STATUS_CONFIRMED);
			associateAssociations.add(assoc);
			// userAssociationDao.create(assoc);
			userAssociationDao.update(assoc);
			log.debug("associate -> user created");
			log.debug("created new UserAssociation with id " + assoc.getUserAssociationId());
		}
	}

	/**
	 * Creates a unidirectional, unconfirmed association between two users.
	 * @param user
	 * @param associate
	 * @param associationType
	 */
	public void createAssociationRequest(User user, User associate,	UserAssociationType aType) throws DbException {
		UserAssociation assoc = createNewAssociation(user, associate, aType, UserAssociation.STATUS_REQUESTED);
		userAssociationDao.update(assoc);
	}

	/**
	 * Completely disassociates the users, breaking the association on both sides.
	 * @param user
	 * @param associate
	 * @param associationType
	 */
	public void disassociateUsers(User user, User associate, UserAssociationType aType) {
		UserAssociation assoc = getUserAssociation(user, associate, aType);
		if (assoc != null) {
			userAssociationDao.delete(assoc);
		}
		assoc = getUserAssociation(associate, user, aType);
		if (assoc != null) {
			userAssociationDao.delete(assoc);
		}		
	}

	/**
	 * Get all of a user associations by type and status.
	 * If status is null, it will not be used when querying.
	 */
	public List<UserAssociation> getUserAssociations(User user,
			UserAssociationType aType, Integer status) {
		return userAssociationDao.getUserAssociations(user.getUserId(), aType);
	}

	public List<UserAssociation> getPendingAssociationRequests(User user,
			UserAssociationType type) {
		return userAssociationDao.getPendingAssociations(user, type);
	}


}
