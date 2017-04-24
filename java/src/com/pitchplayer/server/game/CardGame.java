package com.pitchplayer.server.game;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.pitchplayer.Card;
import com.pitchplayer.server.Challenge;
import com.pitchplayer.server.ServerException;
import com.pitchplayer.server.ServerException.StatusCode;
import com.pitchplayer.server.game.player.CPUPlayerFactory;
import com.pitchplayer.server.game.player.ClientConnector;
import com.pitchplayer.server.game.player.GamePlayer;
import com.pitchplayer.server.game.player.HumanPlayer;
import com.pitchplayer.server.game.player.SocketConnectionPlayer;
import com.pitchplayer.stats.om.GamePlayerRecord;
import com.pitchplayer.stats.om.GameRecord;
import com.pitchplayer.userprofiling.om.User;

/**
 * Abstract superclass of all card games.
 */

public abstract class CardGame {

	private Lock lock = new ReentrantLock();
	private boolean rematchCreated = false;
	
	protected Logger log = Logger.getLogger(this.getClass().getName());

	protected long gameNum; // numerical identifier of this game (for joining
							// purposes)

	protected Vector<GamePlayer> players; // vector of players in this game

	protected GameRecord gameRecord;
	
	protected GameStatus status = GameStatus.GATHERING;
	// protected GameStatus status = GameStatus.NEW;
	
	protected Rotation dealerRotation; // Rotation object to keep track of who's deal it
							   // is

	protected Rotation playRotation; // Rotation object which keeps track of whose turn
							  // it is

	protected Deck deck; // a Deck of cards

	protected final int maxPlayers; // maximum number of players allowed in game

	protected final int minPlayers; // minimum number of players allowed in game

	protected Hand hand; // all of the currently played cards

	protected int trick; // counter for the current trick?

	protected int winner; // index of winner in players vector

	protected int handSize; // number of cards in a hand

	protected int initialPlayerScore; // score at which players begin the game

	protected GameFactory factory = null;

	public static final int NUM_STATUS_GATHERING = 0;

	public static final int NUM_STATUS_RUNNING = 1;

	public static final int NUM_STATUS_OVER = 2;

	public static final int NUM_STATUS_ABORTED = 3;

	private Date lastActivityDate = new Date();

	protected Challenge challenge;

	int humanPlayers = 0;
	

	/**
	 * Create a new game with no players.
	 * 
	 * @param gameRecord2
	 *            the numeric id of this game.
	 */
	protected CardGame(GameRecord gameRecord, GameFactory gf, int initPlayerScore, int minPlayers, int maxPlayers) {
		this(gameRecord, gf, initPlayerScore, minPlayers, maxPlayers, null);
	}

	/**
	 * Create a new game with one player.
	 * 
	 * @param gameRecord
	 *            the numeric id of this game
	 * @param creator
	 *            the player who created this game
	 */
	protected CardGame(GameRecord gameRecord, GameFactory gf, int initPlayerScore,
			int minPlayers, int maxPlayers, GamePlayer creator) {
		this.gameRecord = gameRecord;
		this.factory = gf;
		this.status = GameStatus.GATHERING;
		this.winner = -1;
		this.maxPlayers = maxPlayers;
		this.minPlayers = minPlayers;
		this.players = new Vector<GamePlayer>(maxPlayers);
		this.handSize = 0; // needs to be set in sublclass
		this.trick = 0;
		initialPlayerScore = initPlayerScore;
		if (creator != null) {
			addPlayer(creator);
		}
	}

	/**
	 * Update lastActivityDate
	 */
	protected void touch() {
		lastActivityDate = new Date();
	}

	/**
	 * Get the number of human players in this game
	 */
	public int getNumHumanPlayers() {
		return this.humanPlayers;
	}
	
	/**
	 * Find out the number of human players that are actually connected to the game.
	 * @return
	 */
	public int getNumHumanPlayersConnected() {
		if (players == null || players.isEmpty() || getNumHumanPlayers() == 0) {
			return 0;
		}
		else {
			int connectedHumanPlayers = 0;
			for (GamePlayer p : players) {
				if (HumanPlayer.class.isAssignableFrom(p.getClass())) {
					if (((HumanPlayer)p).isConnected()) {
						connectedHumanPlayers++;
					}
				}
			}
			return connectedHumanPlayers;
		}
	}


	/**
	 * Find out when the last activity took place in this game.
	 */
	public Date getLastActivityDate() {
		return lastActivityDate;
	}

	/**
	 * Get the score at which players begin the game.
	 */
	public int getInitialPlayerScore() {
		return this.initialPlayerScore;
	}

	/**
	 * Set the score at which players begin the game.
	 */
	public void setInitialPlayerScore(int n) {
		this.initialPlayerScore = n;
	}

	/**
	 * Get the maximum allowed number of players in a game.
	 * 
	 * @return the max number of players allowed in a game.
	 */
	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	/**
	 * Get the minimum allowed number of players in a game.
	 * 
	 * @return the minimum number of players allowed in a game.
	 */
	public int getMinPlayers() {
		return this.minPlayers;
	}

	/**
	 * Set the hand size (number of cards to be dealt). <br>
	 * 
	 * @param handSize
	 *            the number of cards to be dealt each hand.
	 */
	public void setHandSize(int handSize) {
		this.handSize = handSize;
	}

	/**
	 * Get the number of cards in a hand of this game type.
	 * 
	 * @return the number of cards dealt to each player in a hand
	 */
	public int getHandSize() {
		return this.handSize;
	}

	/**
	 * Handle a played card. <br>
	 * The subclass game's rules will determine its implementation.
	 * 
	 * @param player
	 *            the GamePlayer throwing the card
	 * @param card
	 *            the card played
	 */
	public abstract void cardPlayed(GamePlayer player, Card card);

	/**
	 * Score a hand. <br>
	 * The subclass game's rules will determine its implementation.
	 * 
	 * @param thisHand
	 *            the just-played out hand to be scored.
	 */
	protected abstract void scoreHand(Hand thisHand);


	/**
	 * Get the game ID
	 */
	public Integer getGameId() {
		return this.gameRecord.getGameId();
	}

	
	/**
	 * Responsible for adding a new player to the players Vector
	 * and returning the new player's index in that Vector.
	 * @param newPlayer
	 * @return
	 */
	protected int seatPlayer(GamePlayer newPlayer) {
		int nextSeat = players.size();
		players.addElement(newPlayer);
		return nextSeat;
	}
	
	
	/**
	 * Add a player to the game. <br>
	 * Adds the player to the vector of players.
	 * 
	 * @param newPlayer
	 *            a player joining the game. FIX: maybe the SQLException should
	 *            be thrown?
	 */
	public synchronized boolean addPlayer(GamePlayer newPlayer) {
		if (this.isJoinable()) {
			touch();
			newPlayer.setGame(this);
			newPlayer.setIndex(seatPlayer(newPlayer));
			// notify all the other players
			if (HumanPlayer.class.isAssignableFrom(newPlayer.getClass())) {
				humanPlayers++;
			}
			for (GamePlayer player : players) {
				player.notifyPlayerAdded(newPlayer.getUsername());
			}
			if (players.size() == maxPlayers) {
				sendToAll("Game is now full. Starting game.");
				try {
					start();
				} catch (Exception e) {
					gameErred(e);
					return false;
				}
			}
			log.info("Player " + newPlayer.getUsername() + " joined game ("
					+ gameRecord.getGameId() + ")");
			return true;
		} else {
			log.info("Attempt to join unjoinable game (" + gameRecord.getGameId()
					+ ") failed");
			return false;
		}
	}
	

	public abstract void addCPUPlayer(CPUPlayerFactory playerFactory);

	/**
	 * Get all names of players in a game.
	 * 
	 * @return an array of the players' names, starting with the player at index
	 *         0 (the gatherer)
	 */
	public String[] getPlayerNames() {
		String[] playerNames;
		synchronized (players) {
			playerNames = new String[players.size()];
			int i = 0;
			for (GamePlayer player : players) {
				playerNames[i++] = player.getUsername();
			}
		}
		return playerNames;
	}
	
	
	public List<String> getPlayerNamesList() {
		List<String> l;
		synchronized (players) {
			l = new ArrayList<String>(players.size());
			for (GamePlayer player : players) {
				l.add(player.getUsername());
			}
		}
		return l;
	}

	
	public List<String> getHumanPlayerNames() {
		List<String> l;
		synchronized (players) {
			l = new ArrayList<String>(getNumHumanPlayers());
			for (GamePlayer player : players) {
				if (HumanPlayer.class.isAssignableFrom(player.getClass())) {
					l.add(player.getUsername());
				}
			}
		}
		return l;
	}


	/**
	 * Find out if a game is joinable
	 * 
	 * @return true if the game has less than the max number of players and it
	 *         hasn't been started yet
	 */
	public boolean isJoinable() {
		if ((players.size() < maxPlayers) && (status != GameStatus.RUNNING)) {
			return true;
		}
		return false;
	}

	/**
	 * Send a server message to all of the players.
	 * 
	 * @param message
	 *            the message to be sent to all players.
	 */
	protected final void sendToAll(String message) {
		for (GamePlayer player : players)
			player.serverMessage(message);
	}

	/**
	 * Get the status of the game.
	 * 
	 * @return one of the GameStatus enums representing the status of the game.
	 */
	public final GameStatus getStatus() {
		return status;
	}

	/**
	 * Get the status of the game (gathering or running)
	 * 
	 * @return the current status of this game (STATUS_GATHERING ||
	 *         STATUS_RUNNING || STATUS_OVER)
	 */
	public final int getNumericStatus() {
		if (status == GameStatus.GATHERING) {
			return NUM_STATUS_GATHERING;
		} else if (status == GameStatus.RUNNING) {
			return NUM_STATUS_RUNNING;
		} else if (status == GameStatus.OVER) {
			return NUM_STATUS_OVER;
		} else if (status == GameStatus.ABORTED) {
			return NUM_STATUS_ABORTED;
		}
		return -1;
	}

	/**
	 * Get game information. Structure of the returned string is as follows:
	 * game
	 * id;status;player_1_name[;player_2_name][;player_3_name][;player_4_name]
	 * Where status is one of: gathering | running | over | aborted
	 * 
	 * @return a String containing the game id, the current status, and a list
	 *         of players
	 */
	public String getInfo() {
		StringBuffer infoStr = new StringBuffer(getGameId() + "," + this.status);
		for (GamePlayer player : players)
			infoStr.append(",").append(player.getUsername());
		return infoStr.toString() + ";";
	}

	/**
	 * This should replace the method getInfo().
	 * @return a GameInfo object with all relevant information about this game.
	 */
	public GameInfo getGameInfo() {
		GameInfo info = new GameInfo();
		info.setGameId(getGameId());
		info.setStatus(this.status);
		List<String> playerList;
		if (players != null) {
			playerList = new ArrayList<String>(players.size());
			for (GamePlayer p : players) {
				playerList.add(p.getUsername());
			}
		}
		else {
			log.debug("players == null");
			playerList = new ArrayList<String>(0);
		}
		info.setPlayerNames(playerList);
		return info;
	}
	
	/**
	 * Start the game. <br>
	 * 
	 * @throws SQLException
	 *             if there was an error storing the game record in the DB.
	 */
	protected void start() {

		if (this.status != GameStatus.GATHERING) {
			return;
		}
		this.status = GameStatus.RUNNING;
		if (this.challenge != null) {
			this.challenge.expire();
		}
		this.gameRecord = factory.logGameStart(this);
		log.info("game " + gameRecord.getGameId() + " started");
		deal(handSize);
		
	}

	/**
	 * Start the game. <br>
	 * Only the creator of the game can start the game.
	 * 
	 * @param starter
	 *            the player attempting to start the game
	 * @throws SQLException
	 *             if there was an error storing the game record in the DB.
	 */
	public void start(GamePlayer starter) throws SQLException {
		if ((starter == (players.firstElement()))
				&& (players.size() >= getMinPlayers())) {
			start();
		} else if (players.size() < getMinPlayers()) {
			starter.serverMessage("Not enough players!");
		} else {
			log.warn("attempt to start game " + gameRecord.getGameId()
					+ " by player other than gatherer");
			starter.serverMessage("Only gatherer can begin game!");
		}
	}

	/**
	 * Deal the cards. <br>
	 * Creates a new hand, shuffles the (new) deck, deals to each player, and
	 * notifies the next player of his turn to bid. <BR>
	 * 
	 * @param numCards
	 *            the number of cards to deal
	 */
	protected void deal(int numCards) {
		hand = initNewHand(players.size());
		if (deck == null) {
			deck = new Deck();
		}
		deck.shuffle();
		if (dealerRotation == null) {
			dealerRotation = new Rotation(players.size());
		}
		playRotation = new Rotation(players.size(), dealerRotation.turn());
		while (playRotation.hasMoreTurns()) {
			(players.elementAt(playRotation.turn())).takeHand(deck
					.getCards(numCards));
			playRotation.increment();
		}
		trick = 1;
		dealerRotation.increment();
		if (!dealerRotation.hasMoreTurns()) {
			dealerRotation = new Rotation(players.size());
		}
	}

	/**
	 * Get the index of the dealer.
	 * @return
	 */
	public int getDealerIndex() {
		return dealerRotation.turn();
	}
	
	/**
	 * Find out if a winner has been declared
	 * 
	 * @return true if a winner has been declared.
	 */
	public final boolean isGameWon() {
		if (winner > -1)
			return true;
		return false;
	}

	/**
	 * Sends message to all players that game is being destroyed, and calls
	 * abortGame().
	 */
	private final void expireGame() {
		if (this.challenge != null) {
			this.challenge.expire();
		}
		if (this.status == GameStatus.GATHERING || this.status == GameStatus.RUNNING) {
			if (playRotation != null) {
				try {
					abortGame(players.elementAt(playRotation.turn()));
				} catch (Exception ignore) {
					log.warn("Error aborting game", ignore);
				}
			}
		}
		else if (this.status == GameStatus.OVER) {
			try {
				sendToAll("Game session ended");
			} catch (Throwable t) {
				// ignore
			}
		}
		log.info("game " + this.getGameId() + " expired");
	}

	/**
	 * Check whether this game is still alive or should be expired. If the game
	 * should be expired, it is.
	 * 
	 * @param expTime
	 * @return whether this game is still considered alive
	 */
	public final boolean checkPulse(Date expTime) {
		if (this.lastActivityDate.before(expTime)) {
			expireGame();
			return false;
		}
		return true;
	}

	/**
	 * Sends <gameover>notification to all players.
	 * 
	 * @param quitter
	 *            the name of the sissy aborting the game.
	 */
	private void abortGame(GamePlayer quitter) {
		abortGame(quitter, true);
	}
	
	/**
	 * Sends <gameover>notification to all players.
	 * 
	 * @param quitter the name of the sissy aborting the game.
	 * @param isQuit whether or not a quit should be registered
	 */
	private void abortGame(GamePlayer quitter, boolean isQuit) {
		if (this.challenge != null) {
			this.challenge.expire();
			this.challenge = null;
		}
		players.remove(quitter);
		if (HumanPlayer.class.isAssignableFrom(quitter.getClass())) {
			this.humanPlayers--;
		}
		for (GamePlayer p : players) {
			if (p != null) {
				p.gameAborted(quitter.getUsername());
			}
		}
		if (isQuit) {
			factory.logGameAborted(this, gameRecord, quitter);
		}
		else {
			factory.logGameAborted(this, gameRecord);
		}
		status = GameStatus.ABORTED;
	}
	
	/**
	 * Called when a player leaves a game.
	 * Will register a quit for the player.
	 * @param player the player leaving the game
	 */
	public void leaveGame(GamePlayer player) {
		leaveGame(player, true);
	}
	
	/**
	 * Called when a player leaves a game.
	 * @param player the player leaving the game
	 * @param isQuit whether or not a quit should be registered.
	 * TODO: This method should be hidden in games which remote CPU players may enter.
	 */
	public void leaveGame(GamePlayer player, boolean isQuit) {
		if (player == null || !players.contains(player)) {
			log.warn("leaveGame() called illegaly");
			return;
		}
		// factory will determine whether to register a quit or not based on game status
		if (status == GameStatus.RUNNING || status == GameStatus.GATHERING) {
			log.debug("calling abortGame()");
			abortGame(player, isQuit);
		}
		else  {
			players.remove(player);
			for (GamePlayer p : players) {
				if (p != null) {
					p.notifyPlayerLeftGame(p);
				}
			}
			log.debug("player " + player.getUsername() + " left game " + getGameId());
		}
		
	}

	/**
	 * Called when game a winner has been declared.
	 * Sends notification to all players and tells game factory to
	 * log the completion of this game.
	 * @param winnerIndex 
	 */
	protected void gameWon(int winnerIndex) {
		this.status = GameStatus.OVER;
		setWinnerIndex(winnerIndex);
	}
	
	/**
	 * Called when some unexpected error was an encountered.
	 * Logs the end of the game.
	 * @param e
	 */
	protected void gameErred(Exception e) {
		this.status = GameStatus.ABORTED;
		factory.logGameAborted(this, gameRecord, null);
	}


	/**
	 * Completely end this game session, telling all players to disconnect
	 * and dropping all player references.
	 * This should be called when numHumanPlayers is < 2.
	 */
	void endGame() {
		while (players != null && !players.isEmpty()) {
			GamePlayer iPlayer = (players.lastElement());
			if (iPlayer != null) {
				iPlayer.gameEnded();
				players.remove(iPlayer);
			}
		}		
	}

	
	/**
	 * Alert all players that one cheated, and end the game.
	 * 
	 * @param player
	 *            the cheater
	 */
	public void alertCheater(GamePlayer player) {
		alertCheater(player, null);
	}

	/**
	 * Alert all players that one cheated, and end the game.
	 * 
	 * @param player
	 *            the cheater
	 */
	public void alertCheater(GamePlayer player, String message) {
		log.info(player.getUsername() + " cheated; here's how: " + message);
		sendToAll(player.getUsername() + " is a lousy cheater!");
		abortGame(player);
	}

	/**
	 * Get the name of the winning person/team
	 */
	protected abstract String getWinnerName();

	/**
	 * Send a chat message to all players
	 * 
	 * @param playerName
	 *            the name of the player speaking
	 * @param message
	 *            his words of wisdom
	 */
	public void say(String playerName, String message) {
		for (GamePlayer player : players) {
			player.sendQuote(playerName, message);
		}
	}

	/**
	 * Get the hand (used for score-keeping)
	 * 
	 * @param numPlayers
	 *            the number of players in the game.
	 */
	protected abstract Hand initNewHand(int numPlayers);

	/**
	 * Get a player's score
	 * 
	 * @param player
	 *            the player's whose score is to be returned.
	 * @return the player's score.
	 */
	public int getPlayerScore(GamePlayer player) {
		return getPlayerScore(player.getIndex());
	}

	/**
	 * Get the current score for the player at the given index.
	 * 
	 * @param playerIndex
	 *            the index of the player whose score is being requested.
	 */
	public int getPlayerScore(int playerIndex) {
		return gameRecord.getGamePlayerRecordAtIndex(playerIndex).getScore().intValue();
	}

	/**
	 * Find out how many players are in this game.
	 */
	public int getNumPlayers() {
		if (players == null) {
			return 0;
		}
		return players.size();
	}

	/**
	 * Get the userId of a particular user
	 */
	public Integer getPlayerId(int index) {
		return players.elementAt(index).getUser().getUserId();
	}

	/**
	 * Get the index of the winner
	 */
	public int getWinnerIndex() {
		return winner;
	}

	/**
	 * Set the index of the winner
	 */
	void setWinnerIndex(int winnerIndex) {
		winner = winnerIndex;
		int i=0;
		for (GamePlayerRecord player : gameRecord.getGamePlayers()) {
			if (player.getSeat() == winnerIndex) {
				player.setWinner((byte)1);
			}
			else {
				player.setWinner((byte)0);
			}
		}
	}

	/**
	 * clean up
	 */
	@Override
	public void finalize() {
		this.lock = null;
		if (this.challenge != null) {
			this.challenge.expire();
		}
		this.gameRecord = null;
		this.players = null;
		log.debug("finalized");
	}
	

	/**
	 * Connect a human player in this game to its ClientConnector, allowing it to
	 * communicate to a remote client.
	 * @param user
	 * @param connector
	 * @return a reference to the SocketConnectionPlayer which was connected; null if a 
	 * SocketConnectionPlayer could not be found in the specified game
	 * @throws ServerException 
	 */
	public SocketConnectionPlayer connectHumanPlayer(User user, ClientConnector connector) throws ServerException {
		for (GamePlayer player : players) {
			if (player instanceof SocketConnectionPlayer) {
				SocketConnectionPlayer human = (SocketConnectionPlayer)player;
				if (human.getUser().getUsername().equals(user.getUsername())) {
					human.attachClientConnector(connector);
					human.confirmJoin();
					return human;
				}
			}
		}
		throw new ServerException(StatusCode.ILLEGAL_OPERATION, 
				"User " + user.getUsername() + " not found in game " + getGameId());
	}

	/**
	 * Set the Challenge related to this game.
	 * @param gameChallenge
	 */
	public void setChallenge(Challenge gameChallenge) {
		this.challenge = gameChallenge;
	}

	public GameRecord getGameRecord() {
		return gameRecord;
	}

	public Lock getLock() {
		return lock;
	}

	public boolean isRematchCreated() {
		return rematchCreated;
	}

	public void setRematchCreated() {
		this.rematchCreated = true;
	}

	
	
}

