package com.pitchplayer.server;

import java.io.IOException;
import java.net.Socket;

import com.pitchplayer.Command;
import com.pitchplayer.server.game.player.ObjClientConnector;

/**
 * Listens for connections from clients who send Commands as serialized objects
 */
public class ObjConnectionListener extends ConnectionListener {

	public ObjConnectionListener(int port, PitchServer server) {
		setPortNum(port);
		setPitchServer(server);
	}


	@Override
	protected void connectClient(Socket clientSocket, PitchServer server) throws IOException, ServerException {
		ObjClientConnector connector = new ObjClientConnector(clientSocket);
		// expect an 'auth' command upon connection
		Command cmd = connector.readCommand();
		if (cmd.getCommand().equals("auth")) {
			log.debug("received command 'auth'");
			String sessionId = cmd.getArgs()[0];
			String username = cmd.getArgs()[1];
			int gameId = Integer.parseInt(cmd.getArgs()[2]);
			log.debug("connecting to human player via gameId " + gameId);
			server.connectHumanPlayer(connector, sessionId, username, gameId);
			log.debug("connected");
		}
		else {
			throw new IOException("Expected 'auth' command but received '" + cmd.getCommand() + "'");
		}
	}

}