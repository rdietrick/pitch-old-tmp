package com.pitchplayer.server.game.player;

import com.pitchplayer.*;
import com.pitchplayer.chat.*;
import com.pitchplayer.server.PitchServer;
import com.pitchplayer.server.ServerException;
import com.pitchplayer.server.ServerException.StatusCode;
import com.pitchplayer.server.game.AutoStartPitchGame;
import com.pitchplayer.server.game.Bid;
import com.pitchplayer.server.game.CardGame;
import com.pitchplayer.server.game.DoublesPitchGame;
import com.pitchplayer.server.game.GameFactory;
import com.pitchplayer.server.game.GameOptions;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.server.game.PitchGame;
import com.pitchplayer.userprofiling.om.User;
import java.net.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Server-side representation of a card game player in a Pitch game. Runs in its
 * own thread and listens for commands from the client.
 */
public class SocketConnectionPlayer extends PitchPlayer implements Runnable, HumanPlayer {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	ChatServer chatServer;

	Hashtable chatGroups;

	private boolean loggedIn = false;

	protected PitchServer server = null; // provides callbacks to server

	// list of command client can pass to server
	String[] commands = { "say", // 0 - pass an in-game text message to
								 // (Pitch)Server
			"play", // 1 - play game
			"join", // 2 - join game
			"info", // 3 - retrieve current game info
			"create", // 4 - create your own game
			"start", // 5 - start a game
			"quit", // 6 - quit game
			"exit", // 7 - close connection
			"bid", // 8 - make a bid
			"login", // 9 - login to server
			"namelist", // 10 - get list of players in game
			"listChatGroups", // 11 - list all chat groups
			"joinChatGroup", // 12 - join a chat group
			"exitChatGroup", // 13 - exit this chat group
			"sayGroup", // 14 - send a message to a chat group
			"addCPUPlayer", // 15 - add a CPU player to the game
			"auth", // 16 - do session based authentication
			"users", // 17 - get the list of users on the server
			"lobbyChat" // 18 - send a message to the lobby
	};

	private boolean running;

	private ClientConnector clientConnector;

	private final User user;

	
	public SocketConnectionPlayer(PitchServer server, User user) {
		this.user = user;
		this.server = server;
	}

	/**
	 * Reads a command from the input stream this player is connected on.
	 */
	protected Command readCommand() throws IOException {
		return clientConnector.readCommand();
	}

	/**
	 * Notify a client that their join was successful.
	 * 
	 * @param gameType
	 *            the type of game the player joined 
	 * @param playerNames
	 *            the names of the other players in the game, which are sent to
	 *            the joiner, in order to build a list of players in the GUI
	 */
	public void confirmJoin() {
		String[] playerNames = getGame().getPlayerNames();
		GameType gameType = ((PitchGame)getGame()).getGameOptions().getGameType();
		String[] args = new String[playerNames.length + 1];
		args[0] = gameType.toCommandValue();
		for (int i = 0; i < playerNames.length; i++) {
			args[i + 1] = playerNames[i];
		}
		send(new Command("joined", args));
	}

	/**
	 * Notify a player that their join was unsuccessful.
	 */
	private void disconfirmJoin() {
		send(new Command("joined"));
	}

	/**
	 * Find out if a player is logged in.
	 * 
	 * @return true if the player is logged in.
	 */
	private boolean isLoggedIn() {
		return this.loggedIn;
	}

	
	/**
	 * Send all players' scores to the client.
	 * 
	 * @param scoreString
	 *            a string containing all scores
	 */
	public void notifyScores(String scoreString) {
		send(new Command("score", scoreString));
	}

	/**
	 * Get information about all current games from the GameServer and sends it
	 * to the client.
	 */
	private void getGameList() {
		Command cmd = new Command("info", server.listGames());
		send(cmd);
	}
	
	private void getUserList() {
		String[] users = server.getUserList();
		Command cmd = new Command("userList", users);
		send(cmd);
	}

	/**
	 * Send a command to the client. <br>
	 * Does actual writing to and flushing of output stream.
	 * 
	 * @param cmd
	 *            the Command to be sent to the client
	 */
	protected void send(Command cmd) {
		if (clientConnector != null && clientConnector.isConnected()) {
			try {
				clientConnector.send(cmd);
			} catch (IOException ioe) {
				log.debug("Error sending command", ioe);
				running = false;
			}
		}
	}

	/**
	 * Notify the client that a new player has been added to the game. <br>
	 * Called from a CardGame.
	 * 
	 * @param playerName
	 *            the name of the player added to a game
	 */
	public void notifyPlayerAdded(String playerName) {
		Command cmd = new Command("newPlayer", playerName);
		send(cmd);
	}

	/**
	 * Notify the client that another player played a card. <br>
	 * Called from a CardGame.
	 * 
	 * @param playerIndex
	 *            the index of the player playing the card
	 * @param card
	 *            info about the card being played
	 */
	public void notifyPlay(int playerIndex, Card card) {
		String[] args = new String[2];
		args[0] = String.valueOf(playerIndex);
		args[1] = card.toString();
		Command cmd = new Command("play", args);
		send(cmd);
	}

	/**
	 * Send a "your turn" notification to client
	 */
	public Card notifyTurn() {
		Command cmd = new Command("turn");
		send(cmd);
		return null;
	}

	/**
	 * Send a "your bid" notification to client.
	 * Contents of command will look like the following:
	 * 2;0;3
	 */
	public int notifyBidTurn(Bid[] bids) {
		StringBuffer sb = new StringBuffer();
		String[] playerNames = getGame().getPlayerNames();
		for (int i = 0, n = bids.length; i < n; i++) {
			String s = playerNames[i] + bids[i].getBid();
			sb.append(s + (i < bids.length - 1 ? ";" : ""));
		}
		Command cmd = new Command("bid", sb.toString());
		send(cmd);
		return -1;
	}

	/**
	 * Notify this player of the winning bid information
	 */
	public void notifyBidder(Bid winningBid) {
		String[] args = { String.valueOf(winningBid.getPlayerIndex()),
				String.valueOf(winningBid.getBid()) };
		Command cmd = new Command("wbid", args);
		send(cmd);
	}

	/**
	 * Send "game over" notification to client
	 */
	public void gameWon(String winner) {
		Command cmd = new Command("gameover", winner);
		send(cmd);
		setGame(null);
	}

	/**
	 * Send a server message to the client
	 */
	public void serverMessage(String message) {
		Command cmd = new Command("server", message);
		send(cmd);
	}

	/**
	 * Get a list of all chat groups
	 */
	private void listChatGroups() {
		if (chatServer == null)
			chatServer = server.getChatServer();
		StringBuffer sb = new StringBuffer();
		String[] groups;
		// i don't think this needs a synchronized block around it
		// if anything, ChatServer.getGroupList() should return a copy
		// of it's internal data structure
		synchronized (chatServer) {
			groups = new String[chatServer.getGroupListSize()];
			Enumeration groupList = chatServer.getGroupList();
			int i = 0;
			while (groupList.hasMoreElements()) {
				groups[i++] = (String) groupList.nextElement();
			}
		}
		Command cmd = new Command("group_list", groups);
		send(cmd);
	}

	/**
	 * Attempt to join a chat group
	 * 
	 * @param groupName
	 *            the name of the chat group to join
	 */
	private void joinChatGroup(String groupName) {
		if (chatServer == null)
			chatServer = server.getChatServer();

		try {
			ChatGroup newGroup = chatServer.joinGroup(this, groupName);
			if (chatGroups == null)
				chatGroups = new Hashtable();
			chatGroups.put(groupName, newGroup);
			newGroup.sendToMembers(this, " joined " + newGroup.getName());
			send(new Command("group_joined", groupName));
		} catch (NoSuchGroupException nsge) {
			serverMessage("No such group: " + groupName);
		} catch (AlreadyJoinedException aje) {
			serverMessage("Already in group: " + groupName);
		}

	}

	/**
	 * Exit a chat group. <br>
	 * Notifies the chat server to remove this member from the chat group.
	 * 
	 * @param groupName
	 *            the name of the group to remove this player from
	 */
	private void exitChatGroup(String groupName) {
		chatServer.removeFromGroup(groupName, this);
		if (chatGroups.containsKey(groupName))
			chatGroups.remove(groupName);
	}

	/**
	 * Send a message to a chat group
	 * 
	 * @param groupName
	 *            the name of the chat group to speak to
	 * @param msg
	 *            the message to send
	 */
	private void sayToGroup(String groupName, String msg) {
		if (chatGroups.containsKey(groupName)) {
			((ChatGroup) chatGroups.get(groupName)).sendToMembers(this, msg);
		} else
			serverMessage("Not in group: " + groupName);
	}

	/**
	 * Send a message from someone in a chat group to this player's client
	 * 
	 * @param groupName
	 *            the name of the group to send the message to
	 * @param fromPlayerName
	 *            the name of the player sending the message
	 * @param msg
	 *            the message to send
	 */
	public void sendChatGroupMessage(String groupName, String fromPlayerName,
			String msg) {
		String[] args = { groupName, fromPlayerName, msg };
		Command cmd = new Command("group_msg", args);
		send(cmd);
	}

	/**
	 * Send a message from someone in the current game to this player's client
	 * 
	 * @param player
	 *            the name of the player sending the message
	 * @param quote
	 *            the message
	 */
	public void sendQuote(String player, String quote) {
		Command cmd = new Command("say", player + ">" + quote);
		send(cmd);
	}

	/**
	 * Notify this player that a player won the last trick. <br>
	 * Sends a message to the client informing them of the winner.
	 * 
	 * @param playerIndex
	 *            the index of the player who won the trick
	 * @param card
	 *            the card which one the last trick
	 */
	public void notifyTrickWon(int playerIndex, Card card) {
		Command cmd = new Command("trick", playerIndex + "|" + card.toString());
		send(cmd);
	}

	/**
	 * Send a hand to this player's client
	 * 
	 * @param hand
	 *            an array of cards to be sent to the client
	 */
	public void takeHand(Card[] hand) {
		sortCards(hand);
		StringBuffer cards = new StringBuffer(hand[0].toString());
		for (int i = 1, n = hand.length; i < n; i++) {
			cards.append("," + hand[i].toString());
		}
		Command cmd = new Command("hand", cards.toString());
		send(cmd);
	}

	/**
	 * Method to swap to elements in an array
	 */
	private void swapElements(Object[] arr, int pos1, int pos2) {
		Object temp = arr[pos1];
		arr[pos1] = arr[pos2];
		arr[pos2] = temp;
	}

	/**
	 * Log an invalid command to std out
	 * 
	 * @param word
	 *            the invalid command
	 */
	private void printError(String word) {
		log.debug(word + " is not a valid command");
	}

	/**
	 * Send game abortion notification to client and nullify the game
	 * 
	 * @param quiter
	 *            the name of the quiter
	 */
	public void gameAborted(String quitter) {
		super.gameAborted(quitter);
		Command cmd = new Command("aborted", quitter);
		send(cmd);
	}

	/**
	 * Authenticate a user with an already established session.
	 * If a gameId was passed as an additional argument, the game will be joined
	 * automatically.
	 * @param args an array of string arguments containing: {sessionId, username, gameId (optional)}
	 */
	protected void authUser(String[] args) {
		// the following lines need to be invoked one way or another
/*			setSessionId(sessionId);
			this.name = user.getUsername();
			server.logPlayerIn(this, getInetAddress());
			this.loggedIn = true;
			sendAuthResponse(this.name); */
	}
	
	private CardGame joinGame(int gameId) {
		server.joinGame(gameId, this);
		CardGame game = getGame();
		if (game != null) { // join was successful
			GameType gameType = GameType.SINGLES;
			if (game instanceof DoublesPitchGame) {
				gameType = GameType.DOUBLES;
			}
			confirmJoin();
			if (game instanceof AutoStartPitchGame) {
				AutoStartPitchGame aGame = (AutoStartPitchGame) game;
				aGame.addCPUPlayer(server.getPlayerFactory());
			}
		} else {
			disconfirmJoin();
		}
		return game;
	}

	/**
	 * Send ant authentication response to the user with their username.
	 * If authentication failed, the username argument should be null
	 * @param username
	 */
	private void sendAuthResponse(String username) {
		Command cmd = null;
		if (username == null) {
			cmd = new Command("authResp");
		}
		else {
			cmd = new Command("authResp", username);
		}
		send(cmd);
	}


	/**
	 * Main method of this thread. <BR>
	 * Sits in a loop listening for commands
	 */
	public void run() {
		running = true;
		try {

			Command command;

			// listen in an infinite loop and check for valid commands
			while (running && (command = readCommand()) != null) {
				log.debug("read command '" + command.getCommand() + "'");
				int commandNumber = -1;
				for (int i = 0, n = commands.length; i < n; i++) { // check for
																   // command
					if (command.getCommand().equals(commands[i])) {
						commandNumber = i;
						break;
					}
				}

				// login:
				if (!isLoggedIn()) {
					// TODO: should probably close the connection here 
					log.warn("unauthenticated client sent command " + command.getCommand());
				} else {
					CardGame game = getGame();
					switch (commandNumber) {
					case 0: // <say>
						if (game != null)
							game.say(getUsername(), command.getArgs()[0]);
						break;
					case 1: // <play>
						if (game != null)
							game.cardPlayed(this,
									new Card(command.getArgs()[0]));
						break;
					case 2: // <join>
						if (game == null) {
							try {
								int gameId = Integer
										.parseInt(command.getArgs()[0]);
								game = joinGame(gameId);
							} catch (NumberFormatException e) {
								serverMessage("Incorrect game number.");
							}
						} else {
							serverMessage("Already in a game!");
						}
						break;
					case 3: // <info>
						getGameList();
						break;
					case 4: // <create>
						if (game == null) {
							createGame(command);
						} else
							serverMessage("Already in a game!");
						break;
					case 5: // <start>
						try {
							game.start(this);
						} catch (SQLException sqle) {
							serverMessage("Error starting game: "
									+ sqle.getMessage());
						}
						break;
					case 6: // <quit>
						if (game != null) {
							leaveGame();
							setGame(null);
						} else {
							serverMessage("Not currently in a game!");
						}
						break;
					case 7: // exit
						if (game != null) {
							serverMessage("You must quit current game befor exiting.");
						} else {
							server.logPlayerOut(this);
							running = false;
						}
						break;
					case 8: // bid
						if (game != null) {
							int b = 0;
							try {
								b = Integer.parseInt(command.getArgs()[0]);
								((PitchGame) game).makeBid(this, b);
							} catch (NumberFormatException nfe) {
								log.warn("Error parsing bid");
							}
						}
						break;
					case 9: // login
						serverMessage("Already logged in!");
						break;
					case 10: // namelist
						if (game != null) {
							StringBuffer sb = new StringBuffer();
							String[] pn = game.getPlayerNames();
							for (int i = 0, n = pn.length; i < n; i++)
								sb.append(pn[i]);
							serverMessage(sb.toString());
						}
					case 11: // listChatGroups
						listChatGroups();
						break;
					case 12: // joinChatGroup
						joinChatGroup(command.getArgs()[0]);
						break;
					case 13: // exitChatGroup
						exitChatGroup(command.getArgs()[0]);
						break;
					case 14: // sayGroup
						String[] args = command.getArgs();
						sayToGroup(args[0], args[1]);
						break;
					case 15: // addCPUPlayer
						if (game != null && game.isJoinable()) {
							game.addCPUPlayer(server.getPlayerFactory());
						}
						break;
					case 17: // users
						getUserList();
						break;
					case 18:
						sendLobbyChat(command.getArgs()[0]);
						break;
					default:
						printError(command.getCommand());
						log.warn("Invalid command '" + command.getCommand()
								+ "' from player " + this.getUsername());
					}
				}
			}
		} catch (IOException ioe) {
			log.debug("Error reading command", ioe);
			// break out of the loop
			running = false;
		} finally {
			end();
		}
	}

	/**
	 * Handle a create command from a remote client.
	 * @param command the Command sent
	 */
	private void createGame(Command command) {
		GameType gameType = null;
		try {
			gameType = GameFactory.parseGameType(Integer.parseInt(command.getArgs()[0]));
			server.createGame(this, new GameOptions(gameType, false));
		} catch (NumberFormatException e) {
			log.warn("Error parsing game type");
		} catch (ServerException e) {
			log.error("Error creating game", e.getCause());
		}
		CardGame game = getGame();
		if (game != null) {
			confirmJoin();
		}
		else {
			disconfirmJoin();
		}
	}

	/**
	 * Disconnect from the client host, if necessary.
	 * Subclasses should implement this method to disconnect 
	 * from the remote client.
	 *
	 */
	protected void disconnect() {
		clientConnector.disconnect();
	}

	/**
	 * Get the IP address of this remote client
	 * @return
	 */
	protected InetAddress getInetAddress() {
		return clientConnector.getInetAddress();
	}
	
	/**
	 * Find out if this client is still connected.
	 * @return
	 */
	public boolean isConnected() {
		return clientConnector.isConnected();
	}
	
	/**
	 * Send a lobby chat message
	 * @param text
	 */
	private void sendLobbyChat(String text) {
		server.sendLobbyChat(getUsername(), text);
	}

	/**
	 * Notify the client that a lobby message was received
	 */
	public void notifyLobbyChat(String username, String text) {
		send(new Command("lobbyChat", new String[] {username, text}));
	}
	
	/**
	 * Called when the player's connection drops
	 */
	private void end() {
		disconnect();
		if (getGame() != null) {
			leaveGame();
			setGame(null);
		}
		server.logPlayerOut(this);
		log.debug("Connection closed for player " + getUsername());
	}
	

	public void attachClientConnector(ClientConnector clientConnector) throws ServerException {
		if (this.clientConnector != null) {
			throw new ServerException(StatusCode.ILLEGAL_OPERATION, "Already connected");
		}
		this.clientConnector = clientConnector;
		// this can probably happen sooner
		setSessionId(getUser().getSessionId());
		
		server.logPlayerIn(this, getInetAddress());
		this.loggedIn = true;
		sendAuthResponse(getUsername()); 
	}

	@Override
	public final User getUser() {
		return this.user;
	}

	@Override
	public final String getUsername() {
		return this.user.getUsername();
	}

}