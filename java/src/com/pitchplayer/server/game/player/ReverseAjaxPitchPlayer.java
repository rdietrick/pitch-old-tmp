package com.pitchplayer.server.game.player;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;

import com.pitchplayer.Card;
import com.pitchplayer.server.ChallengeInitiator;
import com.pitchplayer.server.ServerException;
import com.pitchplayer.server.TargetedChallenge;
import com.pitchplayer.server.Challenge.ChallengeType;
import com.pitchplayer.server.ServerException.StatusCode;
import com.pitchplayer.server.game.Bid;
import com.pitchplayer.server.game.CardGame;
import com.pitchplayer.server.game.GameFactory;
import com.pitchplayer.server.game.GameInfo;
import com.pitchplayer.server.game.GameOptions;
import com.pitchplayer.server.game.PitchGame;
import com.pitchplayer.userprofiling.om.User;

public class ReverseAjaxPitchPlayer extends PitchPlayer implements HumanPlayer {

	private final User user;
	private GameFactory gameFactory;
	private Logger log = Logger.getLogger(this.getClass().getName());
	private DWRPlayerProxy dwrProxy;
	private Card[] hand;
	private boolean connected = false;
	
	public ReverseAjaxPitchPlayer(User user) {
		this.user = user;
	}
	
	/**
	 * Connect the player to a game.
	 * If the player is not already in the game, an attempt will be made to join him to it.
	 * @param proxy
	 * @param gameId
	 * @return
	 * @throws ServerException 
	 */
	public GameInfo connect(DWRPlayerProxy proxy, int gameId) throws ServerException {
		this.dwrProxy = proxy;
		CardGame crntGame = getGame();
		if (crntGame == null || crntGame.getGameId() != gameId) {
			if (crntGame != null) {
				crntGame.leaveGame(this);
			}
			crntGame = gameFactory.joinGame(this, gameId);
			if (crntGame != null) {
				setGame(crntGame);
			}
			else {
				throw new ServerException(StatusCode.NO_GAME, "Could not join game.");
			}
		}
		this.connected = true;
		return crntGame.getGameInfo();
	}
	
	public GameInfo connectToChallengeGame(int gameId) throws ServerException {
		CardGame oldGame = getGame();
		CardGame newGame = gameFactory.joinGame(this, gameId);
		if (newGame != null) {
			// setGame(newGame);
			if (oldGame != null) {
				oldGame.leaveGame(this, false);
			}
		}
		else {
			throw new ServerException(StatusCode.NO_GAME, "Could not join game.");
		}
		this.connected = true;
		return newGame.getGameInfo();
	}
	
	
	public void disconnect() {
		this.connected = false;
	}
	
	/**
	 * Notify the client that a new player has been added to the game. <br>
	 * 
	 * @param playerName
	 *            the name of the player added to a game
	 */
	public void notifyPlayerAdded(String playerName) {
		if (dwrProxy != null) {
			dwrProxy.notifyPlayerAdded(playerName);
		}
	}
	

	/**
	 * Send a hand to this player's client
	 * 
	 * @param hand
	 *            an array of cards to be sent to the client
	 */
	public void takeHand(Card[] hand) {
		sortCards(hand);
		this.hand = hand;
		if (dwrProxy != null) {
			dwrProxy.notifyHand(hand);
		}
	}
	
	@Override
	public int notifyBidTurn(Bid[] bids) {
		if (dwrProxy != null) {
			dwrProxy.notifyBid(bids);
		}
		return -1;
	}

	@Override
	public void notifyBidder(Bid bid) {
		if (dwrProxy != null) {
			dwrProxy.notifyBidder(bid);
		}
	}

	/**
	 * Notify this player of a new bid.
	 * Default implementation does nothing. 
	 * @param index index of the player making the bid
	 * @param bid amount bid
	 */
	@Override
	public void notifyBidMade(int index, int bid) {
		if (dwrProxy != null) {
			dwrProxy.notifyBidMade(index, bid);
		}
	}
	
	@Override
	public void notifyScores(String scoreMsg) {
		if (dwrProxy != null) {
			dwrProxy.notifyScores(scoreMsg);
		}
	}

	@Override
	public void notifyPlay(int playerIndex, Card playedCard) {
		if (dwrProxy != null) {
			dwrProxy.notifyPlay(playerIndex, playedCard);
		}
	}

	@Override
	public void notifyTrickWon(int playerIndex, Card card) {
		if (dwrProxy != null) {
			dwrProxy.notifyTrickWon(playerIndex, card);
		}
	}

	@Override
	public Card notifyTurn() {
		if (dwrProxy != null) {
			dwrProxy.notifyTurn();
		}
		return null;
	}
	
	@Override
	public void notifyPlayerLeftGame(GamePlayer player) {
		if (dwrProxy != null) {
			dwrProxy.notifyPlayerLeftGame(player.getIndex());
		}
	}
	
	@Override
	public void gameWon(String winnerName) {
		// Calling the superclass method sets game = null
		// Defer that till later so that players may continue chatting.
		// super.gameOver(winnerName);
		
		if (dwrProxy != null) {
			dwrProxy.notifyWinner(winnerName);
		}
	}

	@Override
	public void gameAborted(String quitter) {
		if (dwrProxy != null) {
			dwrProxy.notifyGameAborted(quitter);
		}
		if (getGame().getNumHumanPlayers() < 2) {
			// TODO: set game = null and tell client game over
			// setGame(null);
		}
	}

	@Override
	public void sendQuote(String username, String message) {
		if (dwrProxy != null) {
			dwrProxy.notifyChat(username, message);
		}
	}
	
	@Override
	public void serverMessage(String message) {
		if (dwrProxy != null) {
			dwrProxy.notifyServerMessage(message);
		}
	}
	
	public void serverMultiMessage(String[] messages) {
		if (dwrProxy != null) {
			dwrProxy.notifyServerMultiMessage(messages);
		}		
	}
	
	public GameFactory getGameFactory() {
		return gameFactory;
	}


	public void setGameFactory(GameFactory gameFactory) {
		this.gameFactory = gameFactory;
	}


	@Override
	public final User getUser() {
		return this.user;
	}


	@Override
	public final String getUsername() {
		return this.user.getUsername();
	}

	public void addCPUPlayer(CPUPlayerFactory playerFactory) {
		getGame().addCPUPlayer(playerFactory);
	}

	public void startGame() {
		try {
			getGame().start(this);
		} catch (SQLException e) {
			dwrProxy.notifyError(e.getMessage());
		}
	}

	public void placeBid(int bid) {
		PitchGame game = (PitchGame)getGame();
		game.makeBid(this, bid);
	}

	public void playCard(int cardIndex) {
		PitchGame game = (PitchGame)getGame();
		game.cardPlayed(this, hand[cardIndex]);
	}

	public void chat(String message) {
		getGame().say(getUsername(), message);
	}

	public Integer getGameId() {
		if (getGame() == null) {
			return -1;
		}
		else {
			return getGame().getGameId();
		}
	}
	
	/**
	 * @throws ServerException 
	 * 
	 */
	public TargetedChallenge createRematch(ChallengeInitiator initiator) throws ServerException {
		// should first check the status of the game to make sure it's over			
		CardGame crntGame = getGame();
		GameOptions opts = crntGame.getGameInfo().getGameOptions();
		List<String> targets = crntGame.getHumanPlayerNames();
		Lock rematchLock = crntGame.getLock();
		TargetedChallenge challenge = null;
		rematchLock.lock();
		try {
			if (!crntGame.isRematchCreated()) {
				leaveGame();
				CardGame rematch = gameFactory.createGame(this, opts);
				challenge = new TargetedChallenge(ChallengeType.REMATCH, rematch.getGameId(),
						initiator, rematch.getGameInfo().getGameOptions(), targets);
				crntGame.setRematchCreated();
				rematch.setChallenge(challenge);
			}
		} finally {
			rematchLock.unlock();
		}
		return challenge;
	}

	public boolean isConnected() {
		return connected;
	}

}
