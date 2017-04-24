package com.pitchplayer.server;

import java.io.IOException;
import java.net.Socket;

import com.pitchplayer.Command;
import com.pitchplayer.server.game.player.RawDataClientConnector;

/**
 * Listens for connections from clients who send Commands as binary streams of
 * data.
 */
public class RawConnectionListener extends ConnectionListener {

	public RawConnectionListener(int port, PitchServer server) {
		setPortNum(port);
		setPitchServer(server);
	}

	@Override
	protected void connectClient(Socket clientSocket, PitchServer server)
			throws IOException, ServerException {
		RawDataClientConnector connector = new RawDataClientConnector(clientSocket);
		// expect an 'auth' command upon connection
		Command cmd = connector.readCommand();
		if (cmd.getCommand().equals("auth")) {
			String sessionId = cmd.getArgs()[0];
			String username = cmd.getArgs()[1];
			int gameId = Integer.parseInt(cmd.getArgs()[2]);
			server.connectHumanPlayer(connector, sessionId, username, gameId);
		}
		else {
			throw new IOException("Expected 'auth' command but received '" + cmd.getCommand() + "'");
		}		
	}


}