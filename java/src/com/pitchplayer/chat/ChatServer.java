package com.pitchplayer.chat;

import java.util.Enumeration;
import java.util.Hashtable;

import com.pitchplayer.server.game.player.SocketConnectionPlayer;

public class ChatServer {

	private Hashtable groups;

	public int getGroupListSize() {
		return groups.size();
	}

	public Enumeration getGroupList() {
		return groups.keys();
	}

	public ChatGroup joinGroup(SocketConnectionPlayer player, String groupName)
			throws AlreadyJoinedException, NoSuchGroupException {
		if (!groups.containsKey(groupName))
			throw new NoSuchGroupException(groupName);
		ChatGroup group = (ChatGroup) groups.get(groupName);
		return group.addMember(player);
	}

	public void removeFromGroup(String groupName, SocketConnectionPlayer player) {
		ChatGroup group = (ChatGroup) groups.get(groupName);
		group.removeMember(player);
	}

	public ChatGroup addGroup(String groupName, SocketConnectionPlayer creator)
			throws DuplicateGroupException {
		if (groups.containsKey(groupName))
			throw new DuplicateGroupException(groupName);
		return (ChatGroup) groups.put(creator.getUsername(), new ChatGroup(
				groupName, creator));
	}

	public void removeGroup(String groupName) {
		groups.remove(groupName);
	}

}