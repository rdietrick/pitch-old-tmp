package com.pitchplayer.server;

import java.net.InetAddress;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.pitchplayer.chat.ChatServer;
import com.pitchplayer.server.game.CardGame;
import com.pitchplayer.server.game.GameFactory;
import com.pitchplayer.server.game.GameOptions;
import com.pitchplayer.server.game.GameType;
import com.pitchplayer.server.game.player.CPUPlayerFactory;
import com.pitchplayer.server.game.player.ClientConnector;
import com.pitchplayer.server.game.player.GamePlayer;
import com.pitchplayer.server.game.player.SocketConnectionPlayer;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.UserStore;
import com.pitchplayer.userprofiling.om.User;

/**
 * The main server class. Listens for socket connections on a TCP port from
 * clients. Wnen a new client connects, a new Thread is created for the
 * SocketConnectionPlayer object that will handle communication with the client.
 * 
 * This class is being phased out in favor of a decentralized component-based "server,"
 * where independent components (GameFactory, CPUPlayerFactory, etc.) function together
 * as a "server".
 */
public class PitchServer {

	protected Logger log = null;

	private final static double version = 0.2; // keep this consistent with a version

	// number

	private static final String USAGE = "Usage: java PitchServer -p <port> -l <log> -d <db_prop_file> --help -v";

	private GameFactory gameFactory;

	private ChatServer chatServer;

	private Vector<GamePlayer> players;

	private ThreadGroup listenerThreadGroup;

	Thread[] listenerThreads;
	Thread challengeThread;

	private boolean running = false;

	private int objPort, rawPort;
	private UserStore userStore = null;
	private UserService userService = null;
	private GameChallengeService gameChallengeService = null;
	private CPUPlayerFactory playerFactory = null;

	private boolean acceptSocketConnections = false;
	
	public PitchServer() {
		this.log = Logger.getLogger(this.getClass().getName());
	}
	
	/**
	 * Create a new PitchServer that listens on the specified ports
	 * 
	 * @param port
	 *            the port number this server listens on
	 */
	public PitchServer(int objPort, int rawPort) {
		this.log = Logger.getLogger(this.getClass().getName());
		this.objPort = objPort;
		this.rawPort = rawPort;
	}

	/**
	 * Initialize the server
	 */
	public void init() {
		//	chatServer = new ChatServer();
		players = new Vector<GamePlayer>();
		
		try {
			userService.logAllUsersOut();
			log.info("All users logged out");
		} catch (Exception e) {
			log.error("Error initializing all users as logged out: "
					+ e.getMessage(), e);
		}
		
		challengeThread = new Thread(gameChallengeService);
		challengeThread.setDaemon(true);
		challengeThread.start();
		log.debug("challengeThread started");

		if (acceptSocketConnections) {
			log.info("Starting socket connection listener threads");
			listenerThreadGroup = new ThreadGroup("listenerThreads");
			ConnectionListener[] listeners = new ConnectionListener[2];
			listenerThreads = new Thread[2];
			listeners[0] = new ObjConnectionListener(objPort, this);
			listenerThreads[0] = new Thread(listenerThreadGroup, listeners[0]);
			listenerThreads[0].setDaemon(true);
			listeners[1] = new RawConnectionListener(rawPort, this);
			listenerThreads[1] = new Thread(listenerThreadGroup, listeners[1]);
			listenerThreads[1].setDaemon(true);
			running = true;
			listenerThreads[0].start();
			listenerThreads[1].start();
		}
		else {
			log.info("NOT starting socket connection listener threads");
		}
	}

	
	
	/**
	 * Get the GameFactory
	 */
	public GameFactory getGameFactory() {
		return this.gameFactory;
	}

	/**
	 * Drops all connected players
	 * 
	 * @param msg
	 *            the message sent to all players
	 */
	private void dropAllPlayers(String msg) {
		for (int i = 0; i < players.size(); i++) {
			GamePlayer player = players.elementAt(i);
			player.serverMessage(msg);
			player = null;
		}
		players = null;
	}

	/**
	 * Restart the server
	 */
	public void restart() {
		log.info("Server restarting...");
		running = false;
		dropAllPlayers("Server is restarting.");
		listenerThreadGroup.destroy();
		init();
		log.info("Server restarted.");
	}

	/**
	 * Ensure that there's a free game for a newly logged-in user.
	 * @throws ServerException 
	 */
	protected void loginGameCheck() throws ServerException {
		if (gameFactory.getGatheringGameCount() == 0) {
			GamePlayer cpuPlayer = playerFactory.getRandomCPUPlayer(GameType.AUTO_START);
			GameOptions opts = new GameOptions(GameType.AUTO_START, false);
			cpuPlayer.setGame(gameFactory.createGame(cpuPlayer,	opts));
		}
	}

	
	/**
	 * Authenticate a user by session ID Checks for an already logged in user
	 * with the specified sessionId
	 */
	public User authenticateUserWithSession(String sessionId, String username) {
		User user = userStore.connectUser(sessionId);
		// ensure that there's an available game to join
		try {
			if (user != null && user.getUsername().equals(username)) {
				loginGameCheck();
			}
			else {
				return null;
			}
		} catch (ServerException sqle) {
			log.error("DB Error authenticating user: " + sqle.getMessage(),
					sqle);
			return null;
		}
		return user;
	}
	
	/**
	 * Authenticate a user by session ID Checks for an already logged in user
	 * with the specified sessionId
	 * @throws ServerException 
	 */
	public void connectHumanPlayer(ClientConnector connector, String sessionId,
			String username, int gameId) throws ServerException {
		User user = userStore.connectUser(sessionId);
		if (user == null) {
			// TODO: should throw some typed exception
			throw new RuntimeException("No such user");
		}
		// send back auth response
		CardGame game = gameFactory.getGameById(gameId);
		if (game == null) {
			// TODO: throw some exception
			throw new RuntimeException("Game could not be located");
		}
		else {
			SocketConnectionPlayer player = game.connectHumanPlayer(user, connector);
			logPlayerIn(player, connector.getInetAddress());
			// start the thread for the player
			Thread t = new Thread(player);
			t.start();
		}
	}

	/**
	 * Authenticates users against a database of user information
	 * 
	 * @param username
	 *            the username to be authenticated
	 * @param password
	 *            the password to be authenticated
	 * @return a valid User object if successful; null otherwise
	 */
	public User authenticateUser(String username, String password) {
		try {
			User user = userService.authenticateUser(username, password);
			log.info("Authentication "
					+ (user != null ? "successful" : "failed") + " for user "
					+ username);

			// ensure that there's an available game to join
			if (user != null) {
				loginGameCheck();
			}

			return user;
		} catch (ServerException e) {
			return null;
		} finally {

		}
	}

	/**
	 * Log a player's departure.
	 * 
	 * @param playerName
	 *            the name of the player departing
	 */
	public void logPlayerOut(GamePlayer player) {
		players.remove(player);
		User user = player.getUser();
		if (user != null) {
			try {
				userStore.disconnectUser(player.getUser());
			} catch (Exception e) {
				log.error("Error logging player out: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Create a log entry for a player logging in, and update his status as
	 * logged-in in the database.
	 * 
	 * @param player
	 *            the player logging in
	 * @param addr
	 *            the client's InetAddress
	 */
	public void logPlayerIn(GamePlayer player, InetAddress addr) {
		logPlayerIn(player, addr.toString());
	}

	/**
	 * Create a log entry for a player logging in, and update his status as
	 * logged-in in the database.
	 * 
	 * @param player
	 *            the player logging in
	 * @param addr
	 *            the client's InetAddress
	 */
	public void logPlayerIn(GamePlayer player, String addr) {
		log.info(player.getUsername() + " logged in from " + addr);
		players.addElement(player);
	}

	/**
	 * Add a player to an existing game
	 * 
	 * @param gameId
	 *            the numeric id of the game to join
	 * @param player
	 *            the GamePlayer joining the game
	 * @return the PitchGame the player joined or null if the join was
	 *         unsuccessful
	 */
	public synchronized void joinGame(int gameId, GamePlayer player) {
		CardGame game = null;
		game = gameFactory.getGameById(gameId);
		if (game != null && game.isJoinable()) {
			game.addPlayer(player);
		}
	}

	/**
	 * Create a new Game object with one initial player
	 * 
	 * @param player
	 *            the GamePlayer initially in the game
	 */
	public void createGame(GamePlayer player, GameOptions opts)
			throws ServerException {
		log.info("creating game with initial player");
		gameFactory.createGame(player, opts);
	}


	/**
	 * Get a list of all existing game information.
	 * 
	 * @return an array containing all info (players and status) of each game.
	 */
	public String[] listGames() {
		return gameFactory.listGames();
	}

	/**
	 * Get a list of all existing game information.
	 * 
	 * @return an array containing all info (players and status) of each game.
	 */
	public byte[] listGamesAsBytes() {
		return gameFactory.listGamesAsBytes();
	}

	/**
	 * Print a help message for invoking the server on the command line.
	 */
	private static void printHelp() {
		System.out.print("\n\t\tJava PitchServer v" + version + "\n\n");
		System.out.println(USAGE);
		System.out.print("\n<port> - the port that the server "
				+ "will listen for \n\t connections on. (default 7000)\n");
		System.out.print("<log>  - logfile (default com.pitchplayer.log)\n");
		System.out.print("-v     - Prints current server version and exits.\n");
		System.out.print("--help - prints this message.\n\n");

	}

	/**
	 * Get the chat server
	 * 
	 * @return a reference to this server's chat server
	 */
	public ChatServer getChatServer() {
		return this.chatServer;
	}

	/**
	 * Find out if this server is running or has been stopped.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * The main method. <br>
	 * This is where the whole shebang begins. <br>
	 * Processes the command line args, looking for a port number and file to do
	 * logging. If none are passed it uses the defults of port = 7000 and the
	 * log filename "com.pitchplayer.log" Then kicks off the server and begins
	 * listening for connections.
	 * 
	 * @param args
	 *            command line arguments
	 * @deprecated this class is being phased out, and this method is currently NOT USED
	 */
	public static void main(String[] args) {
		int port = 7000; // deafult port number
		// process command line args
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') {
				switch (args[i].charAt(1)) {
				case 'p':
					try {
						port = Integer.parseInt(args[++i]);
					} catch (NumberFormatException e) {
						System.out.println("Invalid port number: " + args[i]);
						System.out.println("Using default port number 7000.\n");
					}
					break;

				case 'l':
					break;

				case 'v':
					System.out.println("PitchServer v0.1a");
					System.exit(0);

				case 'd':
					break;

				case '-': // this is for args that have double "--" instead of
					// single
					if (args[i].substring(2).equals("help")) {
						printHelp();
						System.exit(0); // normal program termination
						break;
					}

				default:
					System.out.println("Invalid option: \"" + args[i] + "\"");
					System.out.println(USAGE);
					System.exit(1); // exit with status 1 to indicate error
				}
			}
		}
		// end command line arg parsing

		System.out.println("Starting Pitch Server...");
		PitchServer theServer = null;
		try {
			theServer = new PitchServer(port, port + 1);
			theServer.init();
		} catch (Exception e) {
			System.out.println("Exception thrown while starting server: "
					+ e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Pitch Server started on port " + port);

	}

	/** ************ end main *************** */


	public void finalize() {
		if (listenerThreadGroup != null) {
			listenerThreadGroup.destroy();
			listenerThreadGroup = null;
		}
		gameChallengeService.stop();
		if (challengeThread.isAlive()) {
			try {
				challengeThread.join(2000);
				log.debug("Challenge thread stopped");
			} catch (InterruptedException ie) {
				log.warn("Challenge thread could not be stopped");
			} finally {
				challengeThread = null;
			}
		}
		else {
			log.debug("challenge thread already stopped");
		}
	}

	public String[] getUserList() {
		String[] usernames = new String[players.size()];
		for (int i=0;i<players.size();i++) {
			usernames[i] = (players.elementAt(i)).getUsername();
		}
		return usernames;
	}

	/**
	 * Send a lobby chat message to all players.
	 * @param username
	 * @param text
	 */
	public void sendLobbyChat(String username, String text) {
		for (int i=0;i<players.size();i++) {
			GamePlayer p = players.elementAt(i);
			p.notifyLobbyChat(username, text);
		}
	}

	public int getObjPort() {
		return objPort;
	}

	public void setObjPort(int objPort) {
		this.objPort = objPort;
	}

	public int getRawPort() {
		return rawPort;
	}

	public void setRawPort(int rawPort) {
		this.rawPort = rawPort;
	}

	public UserStore getUserStore() {
		return userStore;
	}

	public void setUserStore(UserStore userStore) {
		this.userStore = userStore;
	}

	public void setGameFactory(GameFactory gameFactory) {
		this.gameFactory = gameFactory;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public CPUPlayerFactory getPlayerFactory() {
		return playerFactory;
	}

	public void setPlayerFactory(CPUPlayerFactory playerFactory) {
		this.playerFactory = playerFactory;
	}

	public GameChallengeService getGameChallengeService() {
		return gameChallengeService;
	}

	public void setGameChallengeService(GameChallengeService gameChallengeService) {
		this.gameChallengeService = gameChallengeService;
	}

	public boolean isAcceptSocketConnections() {
		return acceptSocketConnections;
	}

	public void setAcceptSocketConnections(boolean acceptSocketConnections) {
		this.acceptSocketConnections = acceptSocketConnections;
	}

}

