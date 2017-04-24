package com.pitchplayer.server;

import java.util.Collection;
import java.util.List;

import com.pitchplayer.server.game.GameInfo;

/**
 * Defines public interface (exposed through DWR) for a remote Lobby.
 * @author robd
 *
 */
public interface RemoteLobby {

	public List<GameInfo> getGameList() throws ServerException;
		
	public String[] getOnlineUsers();
	
	public Collection<Challenge> getAllChallenges() throws ServerException;

//	public Collection<UserFriend> getFriendUpdates();
	
}
