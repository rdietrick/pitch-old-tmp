package com.pitchplayer.chat;

import java.util.Enumeration;
import java.util.Hashtable;

import com.pitchplayer.server.game.player.SocketConnectionPlayer;

public class ChatGroup {

	private String name;

	private Hashtable members;

	public String getName() {
		return this.name;
	}

	public ChatGroup(String name, SocketConnectionPlayer creator) {
		this.name = name;
		this.members.put(creator.getUsername(), creator);
	}

	public ChatGroup addMember(SocketConnectionPlayer newMember)
			throws AlreadyJoinedException {
		if (members.containsKey(newMember.getUsername()))
			throw new AlreadyJoinedException(this.name);
		members.put(newMember.getUsername(), newMember);
		return this;
	}

	public Enumeration getMembers() {
		return members.keys();
	}

	public void removeMember(SocketConnectionPlayer player) {
		members.remove(player);
	}

	public void sendToMembers(SocketConnectionPlayer fromPlayer, String msg) {
		Enumeration names = members.keys();
		while (names.hasMoreElements()) {
			String playerName = (String) names.nextElement();
			((SocketConnectionPlayer) members.get(playerName)).sendChatGroupMessage(
					this.name, fromPlayer.getUsername(), msg);
		}
	}

}