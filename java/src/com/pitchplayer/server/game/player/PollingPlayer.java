package com.pitchplayer.server.game.player;

import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.pitchplayer.Card;
import com.pitchplayer.Command;
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

/**
 * Server-side representation of a card game player in a Pitch game.
 * 
 * TODO: - this.name is null; must use getPlayerName() to get player's name
 */
public class PollingPlayer extends PitchPlayer implements HumanPlayer {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	private boolean loggedIn = false;

	protected PitchServer server = null; // provides callbacks to server

	private final User user;
	
	// TODO: this needs to be set to true at some point during game creation
	private boolean connected = false;

	Vector commands = null;


	/**
	 * Create a new PollingPlayer object
	 * 
	 * @param socket
	 *            the Socket this connection is connected to
	 * @param server
	 *            the PitchServer the player connected via
	 */
	public PollingPlayer(PitchServer server, User user) {
		this.user = user;
		this.server = server;
	}

	public Integer getUserId() {
		return this.user.getUserId();
	}

	public String getPlayerName() {
		return this.user.getUsername();
	}

	/**
	 * Notify a clientthat their join was successful.
	 * 
	 * @param playerNames
	 *            the names of the other players in the game, which are sent to
	 *            the joiner, in order to build a list of players in the GUI
	 */
	private void confirmJoin(GameType gameType, String[] playerNames) {
		connected = true;
		String[] args = new String[playerNames.length + 1];
		args[0] = gameType.toCommandValue();
		for (int i = 0; i < playerNames.length; i++) {
			args[i + 1] = playerNames[i];
		}
		queueCommand(new Command("joined", args));
	}

	/**
	 * Notify a player that their join was unsuccessful.
	 */
	private void disconfirmJoin() {
		queueCommand(new Command("joined"));
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
		queueCommand(new Command("score", scoreString));
	}

	/**
	 * Notify the client that a new player has been added to the game. <br>
	 * Called from a CardGame.
	 * 
	 * @param playerName
	 *            the name of the player added to a game
	 */
	public void notifyPlayerAdded(String playerName) {
		log.debug("newPlayer command queued");
		Command cmd = new Command("newPlayer", playerName);
		queueCommand(cmd);
	}

	/**
	 * Add a CPU player to the game
	 */
	public void addCPUPlayer() {
		log.debug("add CPU Player command received...");
		CardGame game = getGame();
		if (game != null) {
			((PitchGame) game).addCPUPlayer(server.getPlayerFactory());
		}
		log.debug("add CPU Player command completed.");
	}

	/**
	 * Start the game
	 */
	public void startGame() {
		CardGame game = getGame();
		if (game != null) {
			try {
				game.start(this);
			} catch (SQLException sqle) {
				serverMessage("Error starting game: " + sqle.getMessage());
			}
		}
	}

	/**
	 * Make a bid
	 */
	public void bid(int amount) {
		CardGame game = getGame();
		if (game != null) {
			((PitchGame) game).makeBid(this, amount);
		}
	}

	/**
	 * Quit the game if this player is in one.
	 */
	public void quitGame() {
		CardGame game = getGame();
		if (game != null) {
			getGame().leaveGame(this);
		}
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
		queueCommand(cmd);
	}

	/**
	 * Send a "your turn" notification to client
	 */
	public Card notifyTurn() {
		Command cmd = new Command("turn");
		queueCommand(cmd);
		return null;
	}

	/**
	 * Play a card. Plays the card in the CardGame
	 */
	public void playCard(Card card) {
		CardGame game = getGame();
		if (game != null)
			game.cardPlayed(this, card);
	}

	/**
	 * Send a "your bid" notification to client. Message to the client contains
	 * all previous bidding information. Structure of the 'bid' command's
	 * argument is as follows: TODO: document bid format
	 */
	public int notifyBidTurn(Bid[] bids) {
		String[] bidStrings = new String[bids.length];
		String[] playerNames = getGame().getPlayerNames();
		for (int i = 0, n = bids.length; i < n; i++) {
			String s = playerNames[i] + bids[i].getBid();
			bidStrings[i] = s;
		}
		Command cmd = new Command("bid", bidStrings);
		log.debug("sending bid command with " + bidStrings.length + " args");
		queueCommand(cmd);
		return -1;
	}

	/**
	 * Send "game over" notification to client
	 */
	public void gameWon(String winner) {
		Command cmd = new Command("gameover", winner);
		queueCommand(cmd);
		setGame(null);
	}

	/**
	 * Send a server message to the client Not sure if these should really be
	 * sent, but they're useful for debugging the client for now.
	 */
	public void serverMessage(String message) {
		Command cmd = new Command("server", message);
		queueCommand(cmd);
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
		String[] args = new String[2];
		args[0] = String.valueOf(playerIndex);
		args[1] = card.toString();
		Command cmd = new Command("trick", args);
		queueCommand(cmd);
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
		queueCommand(cmd);
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
	public void gameAborted(String quiter) {
		super.gameAborted(quiter);
		Command cmd = new Command("aborted", quiter);
		queueCommand(cmd);
	}

	/**
	 * Authenticate a user and return a PollingPlayer if authentication
	 * succeeded.
	 * 
	 * @param username
	 *            the username sent at login
	 * @param passwd
	 *            the password sent at login
	 * @param inetAddr
	 *            the IP address the user is logging in from
	 * @return a PollingPlayer object if the username/passwd matched; otherwise
	 *         null
	 */
	public static PollingPlayer loginUser(String username, String passwd,
			String inetAddr, PitchServer pitchServer) {
		PollingPlayer player = null;
		// TODO: fix this!  UserStore.connectUser() needs to be called in some manner
		User user = pitchServer.authenticateUser(username, passwd);
		// user authenticated
		if (user != null) {
			player = new PollingPlayer(pitchServer, user);
			player.log.debug("user " + user.getUsername() + " authenticated");
			pitchServer.logPlayerIn(player, inetAddr);
			player.serverMessage("User " + user.getUsername() + " logged in.");
			player.loggedIn = true;
		} else {
			//	    log.debug("authentication failed");
			// serverMessage("Invalid login. Shift+Reload to try again.");
		}
		return player;
	}

	/**
	 * Join a game.
	 */
	public boolean joinGame(int gameNum) throws Exception {
		if (getGame() == null) {
			server.joinGame(gameNum, this);
			CardGame game = getGame();
			if (game != null) { // join was successfull
				GameType gameType = GameType.SINGLES;
				if (game instanceof DoublesPitchGame) {
					gameType = GameType.DOUBLES;
				}
				confirmJoin(gameType, game.getPlayerNames());
				if (game instanceof AutoStartPitchGame) {
					((AutoStartPitchGame) game).addCPUPlayer(server.getPlayerFactory());
				}
				return true;
			} else {
				return false;
			}
		} else
			throw new Exception("Already in game");
	}

	public boolean createGame(int gameType) throws ServerException {
		// from SocketConnectionPlayer:
		if (getGame() == null) {
			server.createGame(this, new GameOptions(GameFactory.parseGameType(gameType), false));
			return true;
		} 
		throw new ServerException(StatusCode.ILLEGAL_OPERATION, "Already in a game");
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
		// drop chat messages on the floor, too
	}

	/**
	 * Called when the player's connection drops
	 */
	private void end() {
		if (getGame() != null) {
			leaveGame();
			setGame(null);
		}
		server.logPlayerOut(this);
		log.debug("Connection closed for player " + this.getPlayerName());
	}

	/**
	 * Add an incoming Command to the queue
	 */
	protected synchronized void queueCommand(Command cmd) {
		if (commands == null) {
			commands = new Vector();
		}
		commands.addElement(cmd);
	}

	/**
	 * Get any queued commands waiting to be retrieved.
	 * 
	 * @return a Vector of Command objects or null (no commands queued)
	 */
	public synchronized Vector getCommands() {
		if (commands == null) {
			return commands;
		}
		Vector copy = (Vector) commands.clone();
		commands = null;
		return copy;
	}

	/**
	 * Notify this player of the winning bid information Does nothing for now,
	 * cause mobile clients don't yet handle this command.
	 */
	public void notifyBidder(Bid winningBid) {
		/*
		 * Command cmd = new Command("wbid",
		 * String.valueOf(winningBid.getPlayerIndex()) +
		 * String.valueOf(winningBid.getBid())); send(cmd);
		 */
	}

	@Override
	public final User getUser() {
		return this.user;
	}

	@Override
	public final String getUsername() {
		return this.user.getUsername();
	}

	public boolean isConnected() {
		return connected;
	}

//	/**
//	 * Update's the UserStore to reflect this newly authenticated User and
//	 * update's the user's last logged in date in the DB.
//	 */
//	public void valueBound(HttpSessionBindingEvent event) {
//		String sessionId = event.getSession().getId();
//		DbBackedUserStore.getInstance().addUser(sessionId, this.user);
//		setSessionId(sessionId);
//	}
//
//	/**
//	 * Removes the user from the UserStore.
//	 */
//	public void valueUnbound(HttpSessionBindingEvent event) {
//		end();
//		DbBackedUserStore.getInstance().removeUser(getUser());
//	}
//

}