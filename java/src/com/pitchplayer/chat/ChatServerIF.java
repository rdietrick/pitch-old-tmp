package com.pitchplayer.chat;

import java.util.Enumeration;

import com.pitchplayer.server.game.player.GamePlayer;

public interface ChatServerIF {

	/*
	 * private Vector groups; private Vector players;
	 */

	public Enumeration getGroupList();

	public ChatGroupIF joinGroup(GamePlayer player, String groupName)
			throws AlreadyJoinedException, NoSuchGroupException;

	public void removeFromGroup(String groupName, GamePlayer player);

	public ChatGroup addGroup(String groupName, GamePlayer creator)
			throws DuplicateGroupException;

	public void removeGroup(String groupName);

}