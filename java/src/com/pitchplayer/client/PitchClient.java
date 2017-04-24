package com.pitchplayer.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.pitchplayer.Card;
import com.pitchplayer.Command;
import com.pitchplayer.client.ui.PitchUI;

/**
 * PitchClient.
 * <P>
 * Appletized client for connecting to a PitchServer. <br>
 * The applet must connect to connect to the server from which it was downloaded
 * (security contraint imposed by the Sandbox).
 * </P>
 * <P>
 * May only be run in a Netscape browser, since LiveConnect (the
 * netscape.javascript package) is necessary for java-to-javascript (and
 * vice-versa) communication. <br>
 * When compiling, be sure that the netscape package is in the CLASSPATH!
 * </P>
 */
public class PitchClient {

	protected String username;

	protected LocalServer server;

	private ObjectOutputStream oos;

	protected boolean myTurn = false;

	protected boolean playing = false;

	protected ClientCard[] hand = null;

	protected boolean initialized = false;

	protected int playIndex = 0; // how many cards have been played this trick

	protected int trickCount = 0; // which trick is it?

	protected int trumpSuit = -1;

	protected int leadSuit = -1;

	protected int bidder = -1; // index of bidding player

	protected int bidAmt = -1; // # of points the bidder bid

	public static final int NO_GAME = 0;

	public static final int GAME_TYPE_SINGLES = 1;

	public static final int GAME_TYPE_DOUBLES = 2;
	
	private PitchUI ui;

	private ArrayList playerNames;

	private int gameType;

	private int numPlayers;

	private boolean inGame;

	private boolean gatherer;

	private String sessionId;

	private boolean gettingGameList = false;
	
	// private ServerInfoUpdater gameListUpdater = null;

	private boolean gettingPlayerList = false;
	
	/**
	 * Initialize the applet. <br>
	 * Reads the following parameters from the applet tag in the html page:
	 * <ul>
	 * <li>port - the server port to connect to
	 * <li>server - the ip/hostname of the server
	 * </ul>
	 * Then makes a connection to the server on the specified port. <br>
	 * Also instantiates a LocalServer to listen for messages from the remote
	 * server.
	 */
	public PitchClient(PitchUI ui, String serverHostname, 
			int port, String sessionId, String username) {

		this.ui = ui;
		this.username = username;
		this.sessionId = sessionId;
		// LocalServer handles incoming data from server
		LocalServer server = new LocalServer(serverHostname, port, this);
		server.start();
		System.err.println("Started localserver");

		// objectOutputStream for writing Commands to socket
		try {
			oos = new ObjectOutputStream(server.getOutputStream());
			System.err.println("got output stream.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// gameListUpdater = new ServerInfoUpdater(this);
		initialized = true;
		System.err.println(this.getClass().getName() + " initialized!");
	}

	public void authenticate() {
		// sessionId should have been passes as a parameter
		// need to send this to the server to authenticate
		if (sessionId == null || username == null) {
			// TODO:
			// need to display an error screen in this case
			System.err.println("not authenticated.");
		} else {
			System.err.println("authenticating via session ID");
			// send login command with sessionId
			String[] args = { sessionId, username };
			System.err.println("authing user " + username);
			Command cmd = new Command("auth", args);
			send(cmd);
		}
	}
	
	public void authenticate(String gameId) {
		// sessionId should have been passes as a parameter
		// need to send this to the server to authenticate
		if (sessionId == null || username == null) {
			// TODO:
			// need to display an error screen in this case
			System.err.println("not authenticated.");
		} else {
			System.err.println("authenticating via session ID");
			// send login command with sessionId
			String[] args = { sessionId, username, gameId };
			System.err.println("authing user " + username);
			Command cmd = new Command("auth", args);
			send(cmd);
			System.err.println("Sent command 'auth'");
		}
	}

	/**
	 * Start or resume the background thread which auto-updates the game list.
	 *
	 */
	public void startGameListUpdates() {
		// gameListUpdater.unpause();
	}
	
	/**
	 * Temporarily pause game list updates.
	 *
	 */
	public void pauseGameListUpdates() {
		// gameListUpdater.pause();
	}
	
	/**
	 * Find out if this applet is initialized
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Reinitialize all variables used to keep track of hand information.
	 */
	public void reInitHand() {
		playIndex = 0;
		trickCount = 0;
		trumpSuit = -1;
		leadSuit = -1;
	}

	/**
	 * Get this player's hand of cards
	 */
	public ClientCard[] getHand() {
		return hand;
	}

	/**
	 * Called when a server message is received with the list of current games.
	 * 
	 * @param gameList
	 *            the list of games received from the server
	 */
	public void notifyDisplayGameList(String[] gameList) {
		gettingGameList = false;
		// parse the game list into a 2-dimensional array 
		// format of each row is { gameId, gameType, gameStatus, p1, p2, p3, p4}
		String[][] gameInfo = new String[gameList.length][7];
		for (int i = 0; i < gameList.length; i++) {
			StringTokenizer st = new StringTokenizer(gameList[i], ",");
			
			gameInfo[i][0] = st.nextToken(); // game ID
			
			gameInfo[i][1] = st.nextToken(); // game type ("s" | "d")
			
			gameInfo[i][2] = st.nextToken(); // status ("gathering" | "running")
						
			StringBuffer playerNames = new StringBuffer();
			int numPlayers = st.countTokens();
			for (int j=0;j<4;j++) {
				String playerName = null;
				if (st.hasMoreTokens()) {
					playerName = st.nextToken();
					// trim trailing ";" from player name if there is one (why would this happen??)
					if (playerName.substring(playerName.length() - 1).equals(";")) {
						playerName = playerName.substring(0, playerName.length() - 1);
					}
				}
				gameInfo[i][3+j] = playerName;
			}
		}
		ui.notifyDisplayGameList(gameInfo);
	}
	
	/**
	 * Called to notify the client of an update to the list of logged-in users.
	 * @param args
	 */
	public void notifyDisplayUserList(String[] args) {
		gettingPlayerList = false;
		ui.notifyDisplayUserList(args);
	}


	/**
	 * Called when a server message is received indicating that a game join
	 * attempt was successful. Just reinitializes the client for a new hand of
	 * cards.
	 *  
	 */
	public void notifyJoinOk(int gameType, String[] playerNames) {
		inGame = true;
		numPlayers = playerNames.length;
		this.playerNames = new ArrayList(playerNames.length);
		if (playerNames.length == 1) {
			System.err.println("setting isGatherer = true");
			this.gatherer = true;
		}
		for (int i=0;i<playerNames.length;i++) {
			this.playerNames.add(i, playerNames[i]);
		}
		this.gameType = gameType;
		reInitHand();
		ui.notifyJoinOk(gameType, playerNames);
	}

	/**
	 * Called when a server message is received indicating that an attempt to
	 * join a game failed.
	 */
	public void notifyJoinFailed() {
		ui.notifyJoinFailed();
	}

	/**
	 * Called when a server message is received that indicates that a player was
	 * added to the game this client is in.
	 */
	public void notifyPlayerAdded(String playerName) {
		this.playerNames.add(playerName);
		numPlayers++;
		ui.notifyPlayerAdded(playerName);
	}

	/**
	 * Called when a 'card played' message was received from the server. Updates
	 * the trick count, determines if the played card is the trump or lead suit
	 * based on the playing order.
	 */
	public void notifyCardPlayed(int playerIndex, ClientCard card) {
		if (!playing) {
			return;
		}
		// update the trick and played card counts
		playIndex++;
		if (playIndex == 1) {
			leadSuit = card.getSuit();
			if (trickCount == 0) {
				trumpSuit = leadSuit;
			}
		}
		ui.notifyCardPlayed(playerIndex, card);
	}

	/**
	 * Called when an informational server message is received
	 */
	public void notifyServerMessage(String msg) {
		ui.notifyServerMessage(msg);
	}

	/**
	 * Called when a chat message is received
	 */
	public void notifyChatMessage(String msg) {
		ui.notifyChatMessage(msg);
	}

	/**
	 * Tell this client which player won the bid and the amount the player bid.
	 */
	public void notifyWinningBid(int playerIndex, int bidAmt) {
		this.bidder = playerIndex;
		this.bidAmt = bidAmt;
		ui.notifyWinningBid(playerIndex, bidAmt);
	}

	/**
	 * Notify this client that a trick was won by a player.
	 * 
	 * @param winnerString
	 *            is of the format playerIndex|Card.toString()
	 */
	public void notifyTrickWon(String winnerString) {
		// update the trick and play counters
		int playerIndex = Integer.parseInt(winnerString.substring(0, 1));
		Card card = new ClientCard(winnerString.substring(2));
		trickCount++;
		playIndex = 0;
		ui.notifyTrickWon(playerIndex, card);
	}

	/**
	 * Notify the client that a score update has occured. Subclasses should
	 * display the scores in the user interface. FIX: add format of score string
	 * to javadoc here
	 */
	public void notifyDisplayScores(String scoreString) {
		ui.notifyDisplayScores(scoreString);
	}

	protected void gameEnded() {
		inGame = false;
		gameType = NO_GAME;
		gatherer = false;
		playerNames = null;
		numPlayers = 0;
		hand = null;
		playing = false;
		myTurn = false;
	}
	
	/**
	 * Notify this client that the game was aborted by a user. Sets the hand
	 * variable to null and fakes a server message which looks like 'quitterName
	 * aborted game.' Subclasses should update the UI accordingly after calling
	 * super.notifyGameAborted()
	 * 
	 * @param quitterName
	 *            the name of the player who quit the game
	 */
	public void notifyGameAborted(String quitterName) {
		gameEnded();
		reInitHand();
		notifyServerMessage(quitterName + " aborted game.");
		ui.notifyGameAborted(quitterName);
	}

	/**
	 * Notify this client that the game has been won. Sets the hand to null and
	 * fakes a server message which looks like 'And the winner
	 * is...winnerName!'. Subclasses should update the UI accordingly after
	 * calling super.notifyGameOver();
	 * 
	 * @param winnerName
	 *            the name of the winning player.
	 */
	public void notifyGameOver(String winnerName) {
		gameEnded();
		reInitHand();
		notifyServerMessage("And the winner is..." + winnerName + "!");
		ui.notifyGameOver(winnerName);
	}

	/**
	 * Notify this client that it is the user's turn to bid.
	 */
	public void notifyMyBid(String bidStr) {
		ui.notifyMyBid(bidStr);
	}

	/**
	 * Notify this client that a new hand of cards has been received. Parses the
	 * hand string into an array of cards, which is stored as the protected
	 * variable 'hand'. Also reinitializes all variables which keep track of
	 * trump, lead suits, etc. Subclasses should override this method to update
	 * the UI with the new hand of cards, but be sure to invoke super.takeHand()
	 * first.
	 * 
	 * @param strHand
	 *            the new hand of cards, as a comma delimeted list of cards as
	 *            String representations (equivalent to:
	 *            Card.toString(),Card.toString(),...
	 */
	public void notifyTakeHand(String strHand) {
		try {
			StringTokenizer st = new StringTokenizer(strHand, ",");
			hand = new ClientCard[st.countTokens()];
			int i = 0;
			while (st.hasMoreTokens()) {
				hand[i++] = new ClientCard(st.nextToken());
			}
		} catch (Exception e) {
			e.printStackTrace();
			debug(e);
			throw new RuntimeException("Error parsing hand information");
		}
		if (!playing) {
			playing = true;
		}
		reInitHand();
		ui.notifyTakeHand(strHand);
	}

	/**
	 * Notify the browser that it's this player's turn Sets the myTurn attribute
	 * to true.
	 */
	public void notifyMyTurn(boolean isMyTurn) {
		myTurn = isMyTurn;
		ui.notifyMyTurn(isMyTurn);
	}


	/**
	 * Send Command through output stream to server. <br>
	 * All (outgoing) communication uses this method.
	 */
	public void send(Command cmd) {
		try {
			oos.writeObject(cmd);
			oos.flush();
		} catch (IOException e) {
			System.err.println("Error sending command: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Send a login command to the server
	 * 
	 * @param name
	 *            the user's username
	 * @param passwd
	 *            the user's password
	 */
	public void login(String name, String passwd) {
		String[] args = { name, passwd };
		Command cmd = new Command("login", args);
		send(cmd);
	}

	/**
	 * Send a login command with an authentication token (sessionId) to the
	 * server. Used when a user has already authenticated through a servlet.
	 */
	public void loginToken(String token) {
		System.err.println("loginToken(" + token + ") called");
		String[] args = { token };
		Command cmd = new Command("login", args);
		send(cmd);
	}

	/**
	 * Send a join game command to the server
	 */
	public void joinGame(String gameId) {
		System.err.println("attempting to join game ID " + gameId);
		send(new Command("join", gameId));
	}

	/**
	 * Send the named command with no arguments to the server.
	 * 
	 * @param strCommand
	 *            the name of the command to send to the server.
	 */
	public void sendCommand(String strCommand) {
		Command cmd = new Command(strCommand);
		send(cmd);
	}

	/**
	 * Send a chat message to the server to be relayed to all players
	 */
	public void sendChat(String message) {
		send(new Command("say", message));
	}

	
	/**
	 * Send a command to the server with this player's bid
	 */
	public void sendBid(long bid) {
		send(new Command("bid", String.valueOf(bid)));
	}

	/**
	 * Create a new game
	 * 
	 * @param gameType
	 *            the type of game (singles, doubles) to create
	 */
	public void sendCreateGame(int gameType) {
		inGame = true;
		this.gameType = gameType;
		playerNames = new ArrayList(1);
		playerNames.add(0, getName());
		numPlayers = 1;
		gatherer = true;
		send(new Command("create", String.valueOf(gameType)));
	}

	/**
	 * Send played card to server
	 * 
	 * @param cardStr
	 *            a string representation of the card being played (e.g. 'AS',
	 *            'TH', '7D', etc).
	 */
	public void sendPlayCard(int cardIndex) {
		myTurn = false;
		hand[cardIndex].setPlayed();
		send(new Command("play", hand[cardIndex].toString()));
	}

	/**
	 * Send a join game command to the server
	 * 
	 * @param gameId
	 *            the ID of the game to join
	 */
	public void sendJoinGame(String gameId) {
		System.err.println("attempting to join game ID " + gameId);
		send(new Command("join", gameId));
	}

	/**
	 * Send a join game command to the server
	 * 
	 * @param gameId
	 *            the ID of the game to join
	 */
	public void sendJoinGame(long gameId) {
		send(new Command("join", String.valueOf(gameId)));
	}

	/**
	 * Send played card to server
	 * 
	 * @param cardIndex
	 *            the index of the card being played in the user's hand TODO:
	 *            Delete this method, if nothing calls it
	 */
	public void sendPlayCard(String cardStr) {
		myTurn = false;
		send(new Command("play", cardStr));
	}

	/**
	 * Request the list of current games from the server
	 */
	public synchronized void sendGetGameList() {
		if (!gettingGameList ) {
			gettingGameList = true;
			sendCommand("info");
		}
	}
	

	/**
	 * Request the list of currently logged in players from the server.
	 *
	 */public void sendGetPlayerList() {
		if (!gettingPlayerList ) {
			gettingPlayerList = true;
			sendCommand("users");
			System.err.println("sent command users");
		}
		else {
			System.err.println("userList still pending; not sending command");
		}
	}

	 
	 /**
	  * Send a quit game command.
	  *
	  */
	 public void sendQuitGame() {
			sendCommand("quit");		 
	 }

	/**
	 * Get the player's name
	 */
	public String getName() {
		return username;
	}

	/**
	 * Find out whether it is this player's turn
	 */
	public boolean getIsMyTurn() {
		return myTurn;
	}


	/**
	 * Stop the client from running.
	 */
	public void stop() {
		if (this.getInGame()) {
			sendQuitGame();
		}
		sendCommand("exit");
		try {
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (server != null) {
			server.destroy();
			server = null;
		}
	}


	/**
	 * Request the server to add a CPU player to this game
	 */
	public void addCPUPlayer() {
		send(new Command("addCPUPlayer"));
	}

	/**
	 * Determine whether a card is playable
	 */
	public boolean isCardPlayable(int cardIndex) {
		boolean playable = false;
		// if it's trump or the lead suit, return true
		if (playIndex == 0 || (hand[cardIndex].getSuit() == trumpSuit)
				|| (hand[cardIndex].getSuit() == leadSuit)) {
			playable = true;
		} else {
			// if i have the lead suit left, return false
			boolean hasLeadSuit = false;
			for (int i = 0; i < hand.length; i++) {
				if (hand[i].wasPlayed()) {
					continue;
				}
				if (hand[i].getSuit() == leadSuit) {
					hasLeadSuit = true;
					break;
				}
			}
			// none of lead suit left, return true
			playable = !hasLeadSuit;
		}
		return playable;
	}

	/**
	 * Useful for debugging from in applet or from JSP page.
	 */
	public void debug(Exception e) {
		System.err.println(e.getMessage());
		e.printStackTrace();
	}

	/**
	 * Useful for debugging from in applet or from JSP page.
	 */
	public void debug(String message) {
		System.err.println(message);
	}

	/**
	 * Find out whether a game is currently underway
	 * @return
	 */
	public boolean getGameStarted() {
		return playing;
	}

	/**
	 * Find out what the trump suit of the current hand is.
	 * @return
	 */
	public int getTrumpSuit() {
		return trumpSuit;
	}

	/**
	 * Get the index (seat) of the current bidder.
	 * @return
	 */
	public int getBidder() {
		return bidder;
	}

	/**
	 * Get the amount the bidder bid.
	 * @return
	 */
	public int getBidAmt() {
		return bidAmt;
	}

	/**
	 * Find out whether this player is the creator of the current game.
	 */
	public boolean getIsGatherer() {
		return gatherer;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public ArrayList getPlayerNames() {
		// TODO Auto-generated method stub
		return playerNames;
	}

	/**
	 * Find out wiehter this client is currently playing an active game.
	 * @return
	 */
	public boolean getPlaying() {
		return playing;
	}

	/**
	 * Find out whether this client is currently in a game (seated at a table).
	 * @return
	 */
	public boolean getInGame() {
		return inGame;
	}

	/**
	 * Get the type of game this player is in (singles or doubles).
	 * @return
	 */
	public int getGameType() {
		return gameType;
	}
	
	public void finalize() {
		// gameListUpdater.kill();
		// gameListUpdater = null;
		server.kill();
		server = null;
		try {
			oos.close();
		} catch (IOException ignore) {
			
		}
	}

	/**
	 * 
	 * @param username
	 */
	public void notifyAuthResponse(String username) {
		if (username == null) {
			this.username = null;
			ui.notifyAuthFailed();
		}
		else {
			// gameListUpdater.start();
			ui.notifyAuthSucceeded();
		}
	}

	/**
	 * Send a lobby chat message
	 * @param text
	 */
	public void sendLobbyChat(String text) {
		send(new Command("lobbyChat", text));
	}

	/**
	 * Notify the UI that a lobby chat message was received
	 * @param username
	 * @param message
	 */
	public void notifyLobbyChat(String username, String message) {
		ui.notifyLobbyChat(username, message);
	}

	public void setIsGatherer(boolean b) {
		this.gatherer = true;
	}

}