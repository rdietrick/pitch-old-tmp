/**  6/24/99 need to add handlers for three new Commands:
 group_list,
 group_joined,
 group_msg
 which allow chatting in groups.
 */

package com.pitchplayer.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import com.pitchplayer.Command;

/**
 * Client-side representation of a server. <br>
 * Handles incoming communication from the remote server and relays messages to
 * the client.
 */
public class LocalServer extends Thread {

	Socket serverSock;

	ObjectInputStream ois;

	PitchClient client;

	String[] commands = { // list of valid commands
	"say", // 0 - display message from another player
			"info", // 1 - display game list
			"joined", // 2 - confirm join
			"newPlayer", // 3 - new player added to game
			"play", // 4 - display a played card
			"bid", // 5 - bid
			"server", // 6 - display a server message
			"hand", // 7 - show new hand
			"turn", // 8 - turn notification
			"score", // 9 - display scores
			"aborted", // 10 - game was aborted
			"gameover", // 11 - game has ended

			// these are unused now
			"group_list", // 12 - display list of chat groups
			"group_joined", // 13 - group join was successful
			"group_msg", // 14 - show message from a group member

			"trick", // 15 - indicate trick winner & clear cards
			"wbid", // 16 - contains the winning bid info
			
			"userList", // 17 - get the list of logged in players
			"authResp", // 18 - authentication response
			"lobbyChat" // 19 - a lobby chat message

	};

	private long oldTime = new Date().getTime();

	private boolean running = true;

	public static final long DELAY = 1000;

	/**
	 * Construct a new LocalServer object which listens to the remote server and
	 * relays mesages to the client. <br>
	 * Attempts to make a socket connection to the remote server.
	 * 
	 * @param address
	 *            the ip of the remote server
	 * @param port
	 *            the port on which to connect to the remote server
	 * @param client
	 *            the PitchClient to relay remote messages to
	 */
	public LocalServer(String address, int port, PitchClient client) {
		super("LocalServer");
		this.client = client;

		try {
			System.err.println("attempting to connect to " + address + ":"
					+ port);
			serverSock = new Socket(address, port);
			ois = new ObjectInputStream(serverSock.getInputStream());
		} catch (IOException e) {
			System.err.println("Could not connect to: " + address + ":" + port);
			System.exit(1);
		}
	}

	/**
	 * Get a reference to the output stream garnered from the socket connection
	 * to the server.
	 * 
	 * @return an OutputStream connected to the remote server
	 */
	public OutputStream getOutputStream() {
		OutputStream os = null;
		try {
			os = serverSock.getOutputStream();
		} catch (IOException err) {
			System.out.println("Could not get OutputStream!");
		}
		return os;
	}

	/**
	 * Kick off this thread. <br>
	 * Listens infinitely for messages from the remote server. Upon receipt of a
	 * valid message, this method parses the received message/data and relays
	 * the call to the client.
	 * <P>
	 * Messages are received in serialized com.pitchplayer.Command objects,
	 * which are read through an ObjectInputStream. These messages contain two
	 * parts: <br>
	 * <ul>
	 * <li>a "command" part -- a string containing the command type
	 * <li>an "argument" part -- an array of string arguments to the command
	 * </ul>
	 */
	public void run() {
		System.out.println("LocalServer Running!");

		Command cmd;
		String arg;
		String[] args;
		try {
			System.out.println("Listening for input...");
			while (running  && (cmd = (Command) (ois.readObject())) != null) {
				int cmdIndex = -1;
				// find out which command was passed
				for (int i = 0; i < commands.length; i++) {
					if (cmd.getCommand().equals(commands[i])) {
						cmdIndex = i;
						System.err.println("Recognized command " + commands[i]);
						break;
					}
				}

				switch (cmdIndex) {
				case 0: // say
					client.notifyChatMessage(cmd.getArgs()[0]);
					break;
				case 1: // info
					client.notifyDisplayGameList(cmd.getArgs());
					break;
				case 2: // joined
					args = cmd.getArgs();
					if (args != null) {
						int gameType = Integer.parseInt(args[0]);
						String[] playerNames = new String[args.length - 1];
						for (int i = 0; i < playerNames.length; i++) {
							playerNames[i] = args[i + 1];
						}
						client.notifyJoinOk(gameType, playerNames);
						System.err.println("'joined' command processed");
					} else {
						client.notifyJoinFailed();
					}
					break;
				case 3: // newPlayer
					arg = cmd.getArgs()[0];
					client.notifyPlayerAdded(arg);
					break;
				case 4: // play
					synchronized (this) {
						long newTime = new Date().getTime();
						long pause = 750 - (newTime - oldTime);
						if (pause > 0) {
							try {
								// sleeping should occur in the UI between display of 
								// played cards, not here
								this.sleep(pause);
							} catch (InterruptedException ignore) {
								// maybe should do something here in case page
								// was unloaded or something
							}
							oldTime = newTime + pause;
						} else {
							oldTime = newTime;
						}
						client.notifyCardPlayed(Integer
								.parseInt(cmd.getArgs()[0]), new ClientCard(cmd
								.getArgs()[1]));
					}
					break;
				case 5: // bid
					client.notifyMyBid(cmd.getArgs()[0]);
					break;
				case 6: // server
					client.notifyServerMessage(cmd.getArgs()[0]);
					break;
				case 7: // hand
					client.notifyTakeHand(cmd.getArgs()[0]);
					break;
				case 8: // turn
					client.notifyMyTurn(true);
					break;
				case 9: // score
					client.notifyDisplayScores(cmd.getArgs()[0]);
					break;
				case 10: // aborted
					//		    client.serverMessage(cmd.getArgs()[0] + " quit game.");
					client.notifyGameAborted(cmd.getArgs()[0]);
					break;
				case 11: // gameover
					client.notifyGameOver(cmd.getArgs()[0]);
					break;
				/*
				 * case 12: // group_list String[] args1 = cmd.getArgs();
				 * StringBuffer sb = new StringBuffer(); for (int i=0;i
				 * <args1.length;i++) sb.append(args1[i] + "|");
				 * client.showGroups(sb.toString()); break; case 13: //
				 * group_joined client.joinedGroup(cmd.getArgs()[0]); break;
				 * case 14: // group_msg String args2[] = cmd.getArgs(); // args
				 * are : groupName, playerName, msg
				 * client.showGroupMessage(args2[0], args2[1], args2[2]); break;
				 */
				case 15: // trick over
					client.notifyTrickWon(cmd.getArgs()[0]);
					// pause for 1.5 seconds
					try {
						// i dont think this is the right place to sleep
						// this should be handled in the user interface
						this.sleep(1500);
					} catch (InterruptedException ie) {
						System.err.println("interrupted!");
					}
					break;
				case 16: // wbid
					client.notifyWinningBid(Integer.parseInt(cmd.getArgs()[0]),
							Integer.parseInt(cmd.getArgs()[1]));
					break;
				case 17: // user list received
					client.notifyDisplayUserList(cmd.getArgs());
					break;
				case 18: // authentication response
					if (cmd.getArgs() == null) {
						client.notifyAuthResponse(null);
					}
					else {
						client.notifyAuthResponse(cmd.getArgs()[0]);
					}
					break;
				case 19: // lobby chat
					client.notifyLobbyChat(cmd.getArgs()[0], cmd.getArgs()[1]);
					break;
				default:
					System.out.println("Invalid command: " + cmd.getCommand());
				}
			}
			ois.close();
			serverSock.close();
		} catch (IOException e) {
			System.err.println("Connection closed");
		} catch (ClassNotFoundException e) {
			System.err.println("Error deserializing command");
			e.printStackTrace();
		} catch (Throwable t) {
			System.out.println("Unexpected error reading in LocalServer: "
					+ t.getMessage());
			t.printStackTrace();
		}
	}
	
	public void kill() {
		running = false;
	}
	
	public void finalize() {
		try {
			ois.close();
		} catch (IOException ignore) {
		}
		try {
			serverSock.close();
		} catch (IOException ignore) {
		}
		running = false;
	}
}

