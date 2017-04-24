package com.pitchplayer.userprofiling;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.pitchplayer.db.DbException;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserAssociation;

@Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
public interface UserAssociationService {


	/**
	 * Creates a bidirectional association between the two users.
	 * @param user
	 * @param associate
	 * @param associationType
	 */
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Exception.class)
	public void associateUsers(User user, User associate, UserAssociationType aType) throws DbException;
	
	/**
	 * Creates a unidirectional, unconfirmed association between two users.
	 * @param user
	 * @param associate
	 * @param associationType
	 */
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false, rollbackFor=Exception.class)
	public void createAssociationRequest(User user, User associate, UserAssociationType aType) throws DbException;
	
	
	/**
	 * Get pending user association "requests" (those for which the user is the on 
	 * the associate end of the relationship).
	 * 
	 * @param user
	 * @param aType
	 * @return
	 */
	public List<UserAssociation> getPendingAssociationRequests(User user, UserAssociationType aType);
	
	/**
	 * Completely disassociates the users, breaking the association on both sides.
	 * @param user
	 * @param associate
	 * @param associationType
	 */
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void disassociateUsers(User user, User associate, UserAssociationType aType);
	
	/**
	 * Returns the list of associations of the given type and status for the specified user.
	 * If status is null, it will not be used in the query.
	 * @param user
	 * @param associationType
	 * @return
	 */
	public List<UserAssociation> getUserAssociations(User user, UserAssociationType aType, Integer status);
	

	
}
	
