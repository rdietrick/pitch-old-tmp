package com.pitchplayer.userprofiling.dao;

import java.util.List;
import java.util.Set;

import com.pitchplayer.db.DuplicateRecordException;
import com.pitchplayer.userprofiling.UserAssociationType;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserAssociation;

public interface UserAssociationDao {

	public void update(UserAssociation assoc) throws DuplicateRecordException;
	
	public List<UserAssociation> getUserAssociations(Integer userId, UserAssociationType associationType);
	
	public UserAssociation getUserAssociation(Integer userId, Integer associateId, UserAssociationType associationType);
	
	public void delete(UserAssociation assoc);
	
	public List<UserAssociation> getUserAssociations(User user, UserAssociationType associationType, Integer status);

	public List<UserAssociation> getPendingAssociations(User user,
			UserAssociationType type);
	
}
