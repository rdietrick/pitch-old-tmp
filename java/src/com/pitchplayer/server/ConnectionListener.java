package com.pitchplayer.server;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.pitchplayer.server.game.player.SocketConnectionPlayer;

/**
 * Listens for connections from pitch clients on a specified port.
 */
public abstract class ConnectionListener implements Runnable {

	protected Logger log = Logger.getLogger(getClass().getName());

	private int portNum;

	private PitchServer server = null;

	private ThreadGroup myThreads;

	private ServerSocket serverSocket;

	public void setPortNum(int n) {
		this.portNum = n;
	}

	public int getPortNum() {
		return this.portNum;
	}

	/**
	 * Set the PitchServer this listener is listening for.
	 */
	public void setPitchServer(PitchServer server) {
		this.server = server;
	}

	/**
	 * Get a reference to the PitchServer which started this listener
	 */
	public PitchServer getPitchServer() {
		return this.server;
	}

	/**
	 * Start the thread. Listens for incoming connections.
	 */
	public void run() {
		myThreads = new ThreadGroup("clientThreads");
		try {
			this.serverSocket = new ServerSocket(portNum);
			log.info("Server started on port " + portNum);
		} catch (IOException e) {
			log.error("Could not listen on port: " + portNum + ", "
					+ e.getMessage());
			System.exit(1);
		}
		listen();
	}

	/**
	 * Listen on the server socket. <br>
	 * Sits in an infinite loop accepting connections to a server socket and
	 * instantiates a new GamePlayer object for each new connection.
	 */
	protected void listen() {
		while (server.isRunning()) {
			Socket clientSocket = null;
			try {
				clientSocket = serverSocket.accept();
				String clientIP = clientSocket.getInetAddress().toString();
				log.info("Connection accepted from " + clientIP);
				connectClient(clientSocket, server);				
			} catch (IOException e) {
				if (server.isRunning()) {
					log.error("Accept failed: " + e.getMessage(), e);
					continue;
				} else {
					break;
				}
			} catch (ServerException se) {
				if (server.isRunning()) {
					log.error("Accept failed: " + se.getMessage(), se);
					continue;
				} else {
					break;
				}				
			}
		}
		log.info("Listener stopped");
	}

	/**
	 * Connect a SocketConnectionPlayer to a remote client via the socket.
	 * @param clientSocket the socket through which the remote client is attaced.
	 * @param server the PitchServer
	 * @throws IOException
	 * @throws ServerException 
	 */
	protected abstract void connectClient(Socket clientSocket, PitchServer server) throws IOException, ServerException;
	
	/**
	 * Stop the listener
	 */
	public void finalize() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException ioe) {
				log.error("Error closing socket", ioe);
			}
			serverSocket = null;
		}
	}

}