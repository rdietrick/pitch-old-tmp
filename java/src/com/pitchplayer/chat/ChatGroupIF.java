package com.pitchplayer.chat;

import java.util.Enumeration;

import com.pitchplayer.server.game.player.GamePlayer;

public interface ChatGroupIF {

	public ChatGroupIF addMember(GamePlayer newMember)
			throws AlreadyJoinedException;

	public Enumeration getMembers();

	public void sendToMembers(GamePlayer fromPlayer, String msg);

	public void removeMember(GamePlayer player);

}