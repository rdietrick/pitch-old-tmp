package com.pitchplayer.server.game;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.pitchplayer.server.ServerException;
import com.pitchplayer.server.game.player.GamePlayer;
import com.pitchplayer.stats.GameRecordService;
import com.pitchplayer.stats.om.GameRecord;
import com.pitchplayer.userprofiling.UserService;
import com.pitchplayer.userprofiling.om.User;

/**
 * Allocates and manages Games (somewhat) efficiently
 */
public class GameFactory {

	protected Logger log = Logger.getLogger(this.getClass().getName());
	private Vector<PitchGame> games;
	//	private List<PitchGame> newGames = new ArrayList(10);
	
	private GameRecordService gameRecordService;
	private UserService userService;

	final int MIN_GAMES = 0;

	final int MAX_GAMES = 25;

	private static final long GAME_CHECKER_FREQUENCY = 1000 * 10 * 1;
//	private static final long GAME_CHECKER_FREQUENCY = 1000 * 60 * 1; // frequency
																	  // with
																	  // which
																	  // to
																	  // prune
																	  // game
																	  // list: 1
																	  // min

	private static final int MULTIPLAY_EXP_MINUTES = 5; // # of minutes before a
														// game is considered
														// expired

	private static final int SINGLE_PLAY_EXP_MINUTES = 15; // # of minutes
														   // before a
														   // one-human-player
														   // game is considered
														   // expired
	private static final int GAME_OVER_EXP_MINUTES = 10;
	
	private static final int NON_CONNECTED_GAME_EXP_SECONDS = 12;

	Timer gameCheckerTimer = new Timer(true);

	private boolean pruning = false;
	
	/**
	 * Create a new GameFactory.
	 */
	private GameFactory() {
		games = new Vector<PitchGame>();
		gameCheckerTimer.schedule(new GameChecker(this), new Date(),
				GAME_CHECKER_FREQUENCY);
	}

	
	/**
	 * Create a new game with the parameterized player and options
	 * @param player
	 * @param options
	 * @return
	 * @throws ServerException
	 */
	public PitchGame createGame(GamePlayer player, GameOptions options)
			throws ServerException {
		PitchGame newGame = createPitchGame(options, player);
		games.addElement(newGame);
		log.debug("created game with ID " + newGame.getGameId() + ", class = "
				+ newGame.getClass().getName());
		player.setGame(newGame);
		player.setIndex(0);
		return newGame;
	}

	protected PitchGame createPitchGame(GameOptions options, GamePlayer player) {
		PitchGame newGame = null;
		// TODO: should add some exception handling here
		GameRecord gameRecord = logGameCreated(options.getGameType());
		switch (options.getGameType()) {
		case SINGLES:
			newGame = new PitchGame(gameRecord, this, player, options);
			break;
		case DOUBLES:
			newGame = new DoublesPitchGame(gameRecord, this, player, options);
			break;
		case SIM_SINGLES:
			newGame = new PitchGame(gameRecord, this, player, options);
			break;
		case SIM_DOUBLES:
			newGame = new DoublesPitchGame(gameRecord, this, player, options);
			break;
		case AUTO_START:
			newGame = new AutoStartPitchGame(gameRecord, this, player, options);
			break;
		default:
			log.error("Unrecognized game type: " + options.getGameType());				
		}
		return newGame;
	}

	public void pruneGameList() {
		if (pruning)
			return;
		pruning = true;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -1 * MULTIPLAY_EXP_MINUTES);
		Date multiPlayExpTime = cal.getTime();
		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -1 * SINGLE_PLAY_EXP_MINUTES);
		Date singlePlayExpTime = cal.getTime();
		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -1 * GAME_OVER_EXP_MINUTES);
		Date gameOverExpTime = cal.getTime();
		Date deadGameExpTime = new Date();
		cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -1 * NON_CONNECTED_GAME_EXP_SECONDS);
		Date nonConnectedGameExpTime = cal.getTime();
		
		Date expTime = null;
		ListIterator<PitchGame> gameIterator = games.listIterator();
		while (gameIterator.hasNext()) {
			PitchGame game = gameIterator.next();
			if (game == null) {
				log.warn("Null game found");
				gameIterator.remove();
				continue;
			}
			// this is getting complicated.
			// maybe better to implement this logic in expireGame() and let CardGame subclasses handled it
			GameType gameType = game.getGameOptions().getGameType();
			if (gameType != GameType.SIM_SINGLES && gameType != GameType.SIM_DOUBLES && game.getNumHumanPlayers() == 0) {
				// if it's not a sim and there are no players left in the game, kill it
				expTime = deadGameExpTime;
			}
			else if (game.getStatus() == GameStatus.OVER || game.getStatus() == GameStatus.ABORTED) {
				// games which have ended but still have > 1 humans in them can linger for a while for chat
				if (game.getNumHumanPlayers() > 1) {
					expTime = gameOverExpTime;
				}
				else {
					expTime = deadGameExpTime;
				}
			}
			else if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GATHERING) {
				// games with human players
				if (game.getNumHumanPlayers() > 1) {
					expTime = multiPlayExpTime;
				} else if (game.getNumHumanPlayersConnected() == 0) {
						expTime = nonConnectedGameExpTime;
				} else {
					expTime = singlePlayExpTime;
				}
			}
			if (!game.checkPulse(expTime)) {
				log.debug("game " + game.getGameId() + " pruned.");
				game.endGame();
				gameIterator.remove();
			}
		}
		pruning = false;
	}

	/**
	 * Insert a new record for the game in the DB, returning the new game ID
	 * (the primary key column in the game table)
	 */
	public GameRecord logGameCreated(GameType gameType) {
		GameRecord game = new GameRecord(gameType.getDbFlag());
		gameRecordService.createGameRecord(game);
		return game;
	}

	/**
	 * Log the start of a game in the DB.
	 */
	public GameRecord logGameStart(CardGame game) {
		GameRecord gameRecord = gameRecordService.logGameStart(game);
		return gameRecord;
	}

	/**
	 * Logs the unexpected end of a game.
	 * 
	 * @param game
	 *            the game which was aborted
	 */
	public void logGameAborted(CardGame game, GameRecord gameRecord) {
		log.debug("game aborted");
		gameRecordService.deleteGameRecord(gameRecord);
	}
	
	/**
	 * Logs a game aborted by a player.
	 * 
	 * @param game
	 *            the game which was aborted
	 * @param quitter
	 *            the name of the player who aborted the game, if any.
	 */
	public void logGameAborted(CardGame game, GameRecord gameRecord, GamePlayer quitter) {
		log.debug("game aborted; status = " + game.getStatus());
		if (game.getStatus() == GameStatus.RUNNING && quitter != null) {
			User quitterUser = quitter.getUser();// userService.getUserById(quitter.getUser().getUserId());
			gameRecord.setUserByQuitterId(quitterUser);
			// TODO: should probably add some exception-handling here
			gameRecordService.updateGameRecord(gameRecord);
		}
		else {
			gameRecordService.deleteGameRecord(gameRecord);
		}
	}

	
	
	/**
	 * Ends a particular game.
	 * 
	 * @param game
	 *            the game which ended
	 * @param gameRecord 
	 * @param winner2 
	 */
	public void logGameWon(CardGame game, GameRecord gameRecord, GamePlayer winner) {
		log.debug("ending game");
		// don't remove the game till numHumanPlayers < 2, so that they can continue chatting
		// games.remove(game);
		// User winner = userService.getUserById(game.getPlayerId(game.getWinnerIndex()));
		gameRecord.setUserByWinnerId(winner.getUser());
		gameRecord.setEndDate(new Date());
		gameRecordService.updateGameRecord(gameRecord);
		log.debug("game over");
	}

	/**
	 * Get a game by its id
	 * 
	 * @param gameId
	 *            the ID of the game requested
	 * @return the game with ID gameId in games vector
	 */
	public PitchGame getGameById(int gameId) {
		if (games == null) {
			return null;
		}
		for (int i = 0; i < games.size(); i++) {
			PitchGame game = games.elementAt(i);
			if (game != null && game.getGameId() == gameId) {
				return game;
			}
		}
		return null;
	}

	/**
	 * Get a list of all games Structure of each element of the returned array
	 * is as follows: game
	 * id;status;player_1_name[;player_2_name][;player_3_name][;player_4_name]
	 * 
	 * @return an array containing all info (players and status) of each game.
	 */
	public String[] listGames() {
		String[] allInfo = new String[games.size()];
		int validGames = 0;
		for (int i = 0, n = games.size(); i < n; i++) {
			PitchGame cardGame = (PitchGame) games.elementAt(i);
			// if the game is not null, and it's not over, list it
			if ((cardGame != null)
					&& cardGame.getStatus() != GameStatus.OVER) {
				String gameInfo = cardGame.getInfo();
				allInfo[validGames] = gameInfo;
				validGames++;
			}
		}
		return allInfo;
	}
	
	public PitchGame[] getGames() {
		return games.toArray(new PitchGame[games.size()]);
	}

	public List<GameInfo> getGameInfoList() {
		List<GameInfo> gameInfoList = new ArrayList<GameInfo>();
		for (CardGame game : games) {
			GameInfo info = game.getGameInfo();
			if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GATHERING) {
				gameInfoList.add(info);
			}
		}
		return gameInfoList;
	}
	
	/**
	 * Get a count of all the gathering games.
	 */
	public int getGatheringGameCount() {
		int gameCount = 0;
		for (int i = 0; i < games.size(); i++) {
			PitchGame cardGame = games.elementAt(i);
			// if the game is not null, and it's not over, list it
			if ((games.elementAt(i) != null)
					&& cardGame.getStatus() == GameStatus.GATHERING) {
				gameCount++;
			}
		}
		return gameCount;
	}

	/**
	 * Get a list of all games Structure of the returned byte array is as
	 * follows: [# of games][game 1 id][status][# of players][p1 name length][p1
	 * name][p2 name length][p2 name]...
	 * 
	 * @return an array containing all info (players and status) of each game.
	 */
	public byte[] listGamesAsBytes() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream dataOut = new DataOutputStream(bytes);
		try {
			dataOut.writeInt(games.size());
			for (int i = 0, n = games.size(); i < n; i++) {
				PitchGame cardGame = games.elementAt(i);
				// if the game is not null, and it's not over, list it
				if ((games.elementAt(i) != null)
						&& cardGame.getStatus() != GameStatus.OVER) {
					// write game info
					dataOut.writeLong(cardGame.getGameId());
					if (cardGame instanceof DoublesPitchGame)
						dataOut.writeInt(1);
					else
						dataOut.writeInt(0);
					dataOut.writeInt(cardGame.getNumericStatus());
					String[] playerNames = cardGame.getPlayerNames();
					dataOut.writeInt(playerNames.length);
					for (int j = 0; j < playerNames.length; j++) {
						dataOut.writeInt(playerNames[j].length());
						dataOut.writeBytes(playerNames[j]);
					}
				}
			}
		} catch (IOException ignore) {
		} finally {
			try {
				dataOut.close();
			} catch (IOException ignore) {
			}
		}
		return bytes.toByteArray();
	}

	public GameRecordService getGameRecordService() {
		return gameRecordService;
	}

	public void setGameRecordService(GameRecordService gameRecordService) {
		this.gameRecordService = gameRecordService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void logGameUpdate(GameRecord gameRecord) {
		gameRecordService.updateGameRecord(gameRecord);		
	}
	
	/**
	 * Parse an int (probably passed from a client) to a valid GameType enum.
	 * @param parseInt
	 * @return
	 */
	public static GameType parseGameType(int parseInt) {
		switch (parseInt) {
		case 1:	return GameType.SINGLES;
		case 2: return GameType.DOUBLES;
		case 3: return GameType.SIM_SINGLES;
		case 4: return GameType.SIM_DOUBLES;
		case 5: return GameType.AUTO_START;
		default: throw new IllegalArgumentException("No matching GameType enum for value " + parseInt);
		}
	}
	
	
	/**
	 * Join a player to a game.
	 * @param player
	 * @param gameId
	 * @return
	 * @throws ServerException
	 */
	public PitchGame joinGame(GamePlayer player, int gameId) throws ServerException {
		PitchGame game = this.getGameById(gameId);
		if (game != null && game.isJoinable()) {
			if (game.addPlayer(player)) {
				return game;
			}
		}
		return null;
	}
	
}

