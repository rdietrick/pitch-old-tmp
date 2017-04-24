package com.pitchplayer.server.game.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.pitchplayer.Command;
import com.pitchplayer.NetUtils;

public class RawDataClientConnector implements ClientConnector {

	DataOutputStream out;

	DataInputStream in;

	private Socket socket;
	Logger log = Logger.getLogger(this.getClass().getName());
	
	/**
	 *  
	 */
	public RawDataClientConnector(Socket socket)
			throws IOException {
		this.socket = socket;
		this.out = new DataOutputStream(socket.getOutputStream());
		this.in = new DataInputStream(socket.getInputStream());
	}
	
	/**
	 * Create a new RawDataHumanPlayer object
	 * 
	 * @param socket
	 *            the Socket this connection is connected to
	 * @param server
	 *            the PitchServer the player connected via
	 */
	public RawDataClientConnector(Socket socket, InputStream in, OutputStream out) throws IOException {
		this.socket = socket;
		this.out = new DataOutputStream(out);
		this.in = new DataInputStream(in);
	}
	
	public void disconnect() {
		if (out != null) {
			try {
				out.close();
				out = null;
			} catch (IOException ioe) {
			}
		}
		if (in != null) {
			try {
				in.close();
				in = null;
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
	 * Reads a command from the input stream this player is connected on.
	 */
	public Command readCommand() throws IOException {
		NetUtils nu = new NetUtils();
		int nameLen = in.readInt();
		String cmdName = nu.readString(in, nameLen);
		int numArgs = in.readInt();
		String args[] = new String[numArgs];
		for (int i = 0; i < args.length; i++) {
			args[i] = nu.readString(in, in.readInt());
		}
		return new Command(cmdName, args);
	}

	/**
	 * Send a command to the client. <br>
	 * Does actual writing to and flushing of output stream.
	 * 
	 * @param cmd
	 *            the Command to be sent to the client
	 */
	public void send(Command cmd) {
		if (out == null) {
			log.error("output stream is null");
			return;
		}
		try {
			out.writeInt(cmd.getCommand().length());
			out.write(cmd.getCommand().getBytes());
			String[] args = cmd.getArgs();
			out.writeInt(args.length);
			for (int i = 0; i < args.length; i++) {
				out.writeInt(args[i].length());
				out.write(args[i].getBytes());
			}
			out.flush();
		} catch (IOException e) {
			log.error("Error writing to socket: " + e.getMessage(), e);
		}
	}

}
