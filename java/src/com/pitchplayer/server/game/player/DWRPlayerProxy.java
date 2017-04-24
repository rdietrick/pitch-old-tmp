package com.pitchplayer.server.game.player;


import org.directwebremoting.ScriptBuffer;

import com.pitchplayer.Card;
import com.pitchplayer.server.Challenge;
import com.pitchplayer.server.ChallengeFilter;
import com.pitchplayer.server.ChallengeInitiator;
import com.pitchplayer.server.GameChallengeService;
import com.pitchplayer.server.ServerException;
import com.pitchplayer.server.TargetedChallenge;
import com.pitchplayer.server.UserPreferenceBasedChallengeFilter;
import com.pitchplayer.server.Challenge.ChallengeType;
import com.pitchplayer.server.ServerException.StatusCode;
import com.pitchplayer.server.game.Bid;
import com.pitchplayer.server.game.CardGame;
import com.pitchplayer.server.game.GameFactory;
import com.pitchplayer.server.game.GameInfo;
import com.pitchplayer.server.game.GameOptions;
import com.pitchplayer.server.game.GameStatus;
import com.pitchplayer.userprofiling.UserAware;
import com.pitchplayer.userprofiling.om.User;

/**
 * Class which is accessible via JavaScript using DWR.
 * @author robd
 *
 */
public class DWRPlayerProxy extends ReverseAjaxDWRProxy implements RemotePlayer, UserAware {
	
	public static final String ATTR_PITCH_PLAYER = "raPlayer";
	private GameChallengeService gameChallengeService;
	private TargetedChallenge rematchChallenge;
	private User user;
	private GameFactory gameFactory;
	private CPUPlayerFactory cpuPlayerFactory;
	private ReverseAjaxPitchPlayer player = null;
	private ChallengeFilter challengeFilter;
	
	public DWRPlayerProxy() {
		super();
	}
	
	/**
	 * Connect to a game.
	 * This method should be called upon load of the game client.
	 * If the connecting player is not the creator of the game, an attempt will be
	 * made to join the player to the game.
	 * @throws ServerException if an attempt to join a new game fails
	 */
	public GameInfo connectToGame(int gameId) throws ServerException {
		log.debug("player " + this.user.getUsername() + " connecting");
		clearScriptQueue();
		connect();
		GameInfo info = player.connect(this, gameId);
		log.info("player " + user.getUsername() + " connected to game " + gameId);
		return info;
	}

	
	public GameInfo acceptChallenge(int gameId) throws ServerException {
		log.debug("acceptChallenge called");
		try {
			CardGame crntGame = player.getGame();
			if (crntGame != null) {
				if (crntGame.getStatus() == GameStatus.RUNNING && crntGame.getNumHumanPlayers() > 1) {
					throw new ServerException(ServerException.StatusCode.ILLEGAL_OPERATION, "Cannot quit a multiplayer game.");
				}
			}
			GameInfo gameInfo = player.connectToChallengeGame(gameId);
			log.debug("player " + user.getUsername() + " connected to game " + gameInfo.getGameId());
			return gameInfo;
		} catch (Exception e) {
			log.error("Error accepting challenge", e);
			throw new ServerException(ServerException.StatusCode.UNKNOWN, e.getMessage(), e);
		}
	}

	/**
	 * Add a player to a game.
	 * @param session
	 */
	public void addPlayer() {
		player.addCPUPlayer(cpuPlayerFactory);
	}
	
	public void startGame() {
		try {
			player.startGame();
		} catch (Exception e) {
			log.error("Error starting game", e);
		}
	}

	/**
	 * Place a bid.
	 * @param session
	 * @param bid
	 */
	public void placeBid(int bid) {
		player.placeBid(bid);
	}
	
	/**
	 * Play a card.
	 * @param session
	 * @param cardIndex
	 */
	public void playCard(int cardIndex) {
		player.playCard(cardIndex);
	}
	
	/**
	 * Send a chat message to players in a game 
	 * @param session
	 * @param message
	 */
	public void sendGameChat(String message) {
		player.chat(message);
	}

	/**
	 * Leave/quit a game
	 */
	public void leaveGame() {
		if (player != null && player.getInGame()) {
			player.leaveGame();
		}
	}
	
	@Override
	public void disconnect() {
		leaveGame();
		player.disconnect();
		super.disconnect();
	}

	/**
	 * Request a rematch
	 * @throws ServerException 
	 */
	public TargetedChallenge createRematch(GameOptions gameOpts) throws ServerException {
		// if game is null or numHumanPlayers == 1 
		TargetedChallenge challenge = null;
		ChallengeInitiator challenger = new ChallengeInitiator() {

			private User user = getSessionUser();

			public void challengeAccepted(String playerName) {
				notifyChallengeAccepted(playerName);
			}

			public void challengeDeclined(String playerName) {
				notifyChallengeDeclined(playerName);
			}
			public User getUser() {
				return this.user;
			}
		};
		CardGame game = player.getGame();
		if (game == null ) { // || game.getNumHumanPlayers() == 1
			// just create a regular game
			game = gameFactory.createGame(player, gameOpts);
			challenge = new TargetedChallenge(ChallengeType.REMATCH, game.getGameId(),
					challenger, gameOpts, null);
		}
		else {
			if (!game.isRematchCreated()) {
				challenge = player.createRematch(challenger);
				if (challenge != null && challenge.getTargetedUsernames().length > 0) {
					gameChallengeService.sendChallenge(challenge);
				}
			}
		}
		return challenge;
	}


	public int acceptRematch() throws ServerException {
		this.rematchChallenge.accept(user.getUsername());
		player.getGameFactory().joinGame(player, rematchChallenge.getGameId());
		return rematchChallenge.getGameId();
	}

	public void declineRematch() throws ServerException {
		if (this.rematchChallenge != null) {
			this.rematchChallenge.decline(user.getUsername());
		}
	}

	public Integer createGame(GameOptions opts, String challengeType) throws ServerException {
		log.debug("createGame called");
		if (player.getInGame()) {
			log.debug("player still in game");
			return -1;
		}
		CardGame game;
		clearScriptQueue();
		game = gameFactory.createGame(player, opts);
		Challenge gameChallenge = null;
		if (challengeType.equals(ChallengeType.ALL.toString())) {
			gameChallenge = new Challenge(ChallengeType.ALL, game.getGameId(), 
					getSessionUser(), opts);
		}
		else if (challengeType.equals(ChallengeType.FRIENDS.toString())) {
			gameChallenge = new Challenge(ChallengeType.FRIENDS, game.getGameId(),
					getSessionUser(), opts);			
		}
		if (gameChallenge != null) {
			game.setChallenge(gameChallenge);
			gameChallengeService.sendChallenge(gameChallenge);
		}
		return game.getGameId();
	}
	
	/**
	 * Doesn't actually join the player to the game, as that has to happen after
	 * the game window pops up on the client.
	 * Just checks that the game is joinable and that the player is not already in a game.
	 * @throws ServerException 
	 */
	public boolean joinGame(Integer gameId) throws ServerException {
		CardGame game = gameFactory.getGameById(gameId);
		if (game != null) {
			// TODO: need to check whether player is in cpu-only game
			
			return (!player.getInGame() && game.isJoinable());
		}
		else {
			throw new ServerException(StatusCode.NO_GAME, "Game no longer exists.");
		}
	}

	public Integer getCurrentGameId() {
		return player.getGameId();
	}
	

	/****************************************************************
	 ************ End methods called from client ********************
	 ****************************************************************/

	/****************************************************************
	 **************** Methods called from game/server ***************
	 ****************************************************************/
	
	void notifyChallengeAccepted(String player) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyChallengeAccepted(").appendData(player).appendScript(");");
		sendScript(sb);		
	}

	void notifyChallengeDeclined(String player) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyChallengeDeclined(").appendData(player).appendScript(");");
		sendScript(sb);		
	}

	/**
	 * Notify the player that another player was added
	 * @param session
	 */
	void notifyPlayerAdded(String playerName) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyPlayerAdded(").appendData(playerName).appendScript(");");
		sendScript(sb);
	}

	/**
	 * Send a new hand of cards to the client.
	 * @param hand
	 */
	void notifyHand(Card[] hand) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyNewHand(").appendData(hand).appendScript(");");
		sendScript(sb);
	}

	/**
	 * Notify the client that it is their bid, passing all going bids as an argument.
	 * @param bids
	 */
	void notifyBid(Bid[] bids) {
		ScriptBuffer sb = new ScriptBuffer();
	    sb.appendScript("notifyBid(")
	      .appendData(bids)
	      .appendScript(");");
	    sendScript(sb);
	}

	/**
	 * Notify the client of an error.
	 * @param message
	 */
	public void notifyError(String message) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyError(")
			.appendData(message)
			.appendScript(");");
		sendScript(sb);
	}

	/**
	 * Notify the client that a card was played.
	 * @param playerIndex
	 * @param playedCard
	 */
	public void notifyPlay(int playerIndex, Card playedCard) {
		ScriptBuffer sb = new ScriptBuffer();
	    sb.appendScript("notifyPlay(")
	      .appendData(playerIndex).appendScript(",").appendData(playedCard)
	      .appendScript(");");
	    sendScript(sb);
	}

	/**
	 * Notify the client that it is their turn.
	 */
	public void notifyTurn() {
		ScriptBuffer sb = new ScriptBuffer();
	    sb.appendScript("notifyTurn();");
	    sendScript(sb);
	}

	public void notifyTrickWon(int playerIndex, Card card) {
		ScriptBuffer sb = new ScriptBuffer();
	    sb.appendScript("notifyTrickWon(")
	      .appendData(playerIndex).appendScript(",").appendData(card)
	      .appendScript(");");
	    sendScript(sb);
	}

	public void notifyScores(String scoreMsg) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyScores(").appendData(scoreMsg).appendScript(");");
		sendScript(sb);
	}

	public void notifyBidder(Bid bid) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyBidder(").appendData(bid).appendScript(");");
		sendScript(sb);
	}

	public void notifyChat(String playerName, String message) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyChat(").appendData(playerName).appendScript(",").appendData(message).appendScript(");");
		sendScript(sb);
	}

	public void notifyWinner(String winnerName) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyWinner(").appendData(winnerName).appendScript(");");
		sendScript(sb);
	}

	public void notifyGameAborted(String quitter) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyGameAborted(").appendData(quitter).appendScript(");");
		sendScript(sb);
	}

	public void notifyServerMessage(String message) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyServerMessage(").appendData(message).appendScript(");");
		sendScript(sb);
	}


	public void notifyServerMultiMessage(String[] messages) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyServerMultiMessage(").appendData(messages).appendScript(");");
		sendScript(sb);		
	}

	public void notifyPlayerLeftGame(int index) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyPlayerLeft(").appendData(index).appendScript(");");
		sendScript(sb);
	}

	public void notifyBidMade(int index, int bid) {
		ScriptBuffer sb = new ScriptBuffer();
		sb.appendScript("notifyBidMade(").appendData(index).appendScript(", ").appendData(bid).appendScript(");");
		sendScript(sb);
	}


	/**
	 * Receive a challenge from another player
	 */
	public void receiveChallenge(Challenge challenge) {
		
		if (!isConnected() || !challengeFilter.filterChallenge(challenge)) {
			return;
		}

		if (challenge.getType() == ChallengeType.REMATCH) {
			TargetedChallenge tChallenge = (TargetedChallenge)challenge;
			if (tChallenge.isUserTargeted(user.getUsername())) {
				this.rematchChallenge = tChallenge;
				ScriptBuffer sb = new ScriptBuffer();
				sb.appendScript("notifyRematchChallenge(").appendData(challenge.getChallengerName()).appendScript(");");
				sendScript(sb);
			}
		}
		else {
			CardGame game = player.getGame();
			if (game != null && game.getStatus() == GameStatus.RUNNING && game.getNumHumanPlayers() > 1) {
				// don't display challenge if in multi-player game
				log.debug("not sending challenge; user already in multiplayer game");
			}
			else {
				ScriptBuffer sb = new ScriptBuffer();
				sb.appendScript("notifyChallenge(").appendData(challenge.getChallengerName()).appendScript(",")
				.appendData(challenge.getGameOptions().getGameType()).appendScript(",")
				.appendData(challenge.getGameId()).appendScript(");");
				// TODO: add game options to argument list
				sendScript(sb);
			}
		}
	}

	/**
	 * Revoke a previously received challenge
	 */
	public void revokeChallenge(Challenge c) {
		if (!isConnected() || c.getChallengerName().equals(user.getUsername())) {
			return;
		}
		else {
			if (c.getType() == ChallengeType.REMATCH) {
				ScriptBuffer sb = new ScriptBuffer();
				sb.appendScript("notifyRevokeRematchChallenge();");
				sendScript(sb);
			}
			else {
				ScriptBuffer sb = new ScriptBuffer();
				sb.appendScript("notifyRevokeChallenge(").appendData(c.getChallengerName()).appendScript(",")
				.appendData(c.getGameId()).appendScript(");");
				sendScript(sb);
			}
		}
	}


	public void setGameChallengeService(GameChallengeService gameChallengeService) {
		this.gameChallengeService = gameChallengeService;
	}

	public void setUser(User user) {
		this.user = user;
		player = new ReverseAjaxPitchPlayer(user);
		if (gameFactory != null) {
			player.setGameFactory(gameFactory);
			log.debug("set player's game factory");
		}
		log.debug("user = " + this.user.getUsername());
		challengeFilter = new UserPreferenceBasedChallengeFilter(user);
	}

	public void setGameFactory(GameFactory gameFactory) {
		this.gameFactory = gameFactory;
		if (player != null) {
			player.setGameFactory(gameFactory);
			log.debug("set player's game factory");
		}
	}

	public void setPlayerFactory(CPUPlayerFactory cpuPlayerFactory) {
		this.cpuPlayerFactory = cpuPlayerFactory;
	}


	/****************************************************************
	 ************** End Methods called from game/server *************
	 ****************************************************************/

	
}
