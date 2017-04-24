package com.pitchplayer.server.game.player;

import com.pitchplayer.server.ChallengeListener;
import com.pitchplayer.server.ServerException;
import com.pitchplayer.server.TargetedChallenge;
import com.pitchplayer.server.game.GameInfo;
import com.pitchplayer.server.game.GameOptions;


public interface RemotePlayer extends ChallengeListener {

	/**
	 * Connect to game and get its details.
	 * @param gameId
	 * @return
	 * @throws ServerException 
	 */
	public GameInfo connectToGame(int gameId) throws ServerException;
	
	public void disconnect();
	
	public void addPlayer();
	
	public void startGame();

	/**
	 * Place a bid.
	 * @param session
	 * @param bid
	 */
	public void placeBid(int bid);
	
	/**
	 * Play a card.
	 * @param session
	 * @param cardIndex
	 */
	public void playCard(int cardIndex);
	
	/**
	 * Send a chat message to players in a game 
	 * @param session
	 * @param message
	 */
	public void sendGameChat(String message);
	
	
	public void leaveGame();
	
	/**
	 * Create or join a rematch
	 * @return the ID of the newly created game
	 * @throws ServerException 
	 */
	public TargetedChallenge createRematch(GameOptions gameOpts) throws ServerException;
	
	/**
	 * Accept a rematch request
	 * @return the ID of the rematch game
	 * @throws ServerException
	 */
	public int acceptRematch() throws ServerException;
	
	/**
	 * Decline a rematch request.
	 * @throws ServerException
	 */
	public void declineRematch() throws ServerException;
	
	/**
	 * Create a new game
	 * @param opts the game options
	 * @param challengeType the type of challenge to send
	 * @return
	 * @throws ServerException 
	 */
	public Integer createGame(GameOptions opts, String challengeType) throws ServerException;
	
	/**
	 * Join a game by Id
	 * @param gameId the ID of the game to join.
	 * @return
	 * @throws ServerException 
	 */
	public boolean joinGame(Integer gameId) throws ServerException;

}
