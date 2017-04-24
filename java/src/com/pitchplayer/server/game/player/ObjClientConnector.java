package com.pitchplayer.server.game.player;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.pitchplayer.Command;

public class ObjClientConnector implements ClientConnector {
	ObjectOutputStream oos;

	ObjectInputStream ois;

	private Socket socket;

	Logger log = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Create a new ObjClientConnector which uses the passed-in socket.
	 * @param socket the socket through which the connector will communicate with the client.  
	 */
	public ObjClientConnector(Socket socket) throws IOException {
		this.socket = socket;
		this.oos = new ObjectOutputStream(socket.getOutputStream());
		this.ois = new ObjectInputStream(socket.getInputStream());
	}
	
	/**
	 * Create a new ObjClientConnector
	 * 
	 * @param socket
	 *            the Socket this connection is connected to
	 * @param in the input stream to read from the client through
	 * @param out the output stream to write to the client through
	 */
	public ObjClientConnector(Socket socket, InputStream in, OutputStream out) throws IOException {
		this.socket = socket;
		this.oos = new ObjectOutputStream(out);
		this.ois = new ObjectInputStream(in);
	}

	public void disconnect() {
		if (oos != null) {
			try {
				oos.close();
				oos = null;
			} catch (IOException ioe) {
			}
		}
		if (ois != null) {
			try {
				ois.close();
				ois = null;
			} catch (IOException ioe) {
			}
		}
	}

	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	public boolean isConnected() {
		return !socket.isClosed();
	}

	/**
	 * Read a Command from the input stream this player is connected on
	 */
	public Command readCommand() throws IOException {
		try {
			return (Command) (ois.readObject());
		} catch (ClassNotFoundException e) {
			log.error("Error casting to Command: " + e.getMessage(), e);
			throw new IOException("Could not parse command");
		}
	}

	/**
	 * Send a command to the client. <br>
	 * Does actual writing to and flushing of output stream.
	 * 
	 * @param cmd
	 *            the Command to be sent to the client
	 */
	public void send(Command cmd) throws IOException {
		if (oos == null) {
			log.error("output stream is null");
			return;
		}
		oos.writeObject(cmd);
		oos.flush();
	}

}
