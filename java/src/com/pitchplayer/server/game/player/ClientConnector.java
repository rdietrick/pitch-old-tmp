package com.pitchplayer.server.game.player;

import java.io.IOException;
import java.net.InetAddress;

import com.pitchplayer.Command;

public interface ClientConnector {
	
	/**
	 * Send a command to the client. <br>
	 * Does actual writing to and flushing of output stream.
	 * 
	 * @param cmd
	 *            the Command to be sent to the client
	 * @throws IOException 
	 */
	public void send(Command cmd) throws IOException;

	/**
	 * Read a Command from the input stream this player is connected on
	 */
	public Command readCommand() throws IOException;

	public void disconnect();
	
	public InetAddress getInetAddress();
	
	public boolean isConnected();

}
