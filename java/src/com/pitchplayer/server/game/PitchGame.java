package com.pitchplayer.server.game;

import java.util.ArrayList;

import com.pitchplayer.Card;
import com.pitchplayer.server.game.player.CPUPlayer;
import com.pitchplayer.server.game.player.CPUPlayerFactory;
import com.pitchplayer.server.game.player.GamePlayer;
import com.pitchplayer.server.game.player.HumanPlayer;
import com.pitchplayer.server.game.player.PitchPlayer;
import com.pitchplayer.stats.om.GameRecord;

/**
 * A Game of "cut-throat" or singles Pitch. Subclass of CardGame. <BR>
 */
public class PitchGame extends CardGame {

	public static final int INITIAL_PLAYER_SCORE = 11;
	
	private GameOptions gameOptions;
	private Rotation bidRotation;

	/**
	 * Creates a game with the supplied id number and one initial player.
	 * 
	 * @param gameRecord
	 *            the game's id number
	 * @param player
	 *            an initial GamePlayer
	 */
	PitchGame(GameRecord gameRecord, GameFactory gf, GamePlayer player, GameOptions gameOptions) {
		super(gameRecord, gf, INITIAL_PLAYER_SCORE, gameOptions.getMinPlayers(), gameOptions.getMaxPlayers(), player);
/*		
		if (player.getUsername().equals("robd")) {
			this.initialPlayerScore = 1;
		}
*/		
		this.handSize = 6;
		this.gameOptions = gameOptions;
	}
	

	public void addCPUPlayer(CPUPlayerFactory playerFactory) {
		addPlayer(playerFactory.getRandomCPUPlayer(gameOptions.getGameType(), getComputerPlayerIds()));
	}
	
	/**
	 * Get an array containing the Player objects for all computer players in this game.
	 * @return
	 */
	protected int[] getComputerPlayerIds() {
		int cpuPlayers = this.players.size() - this.getNumHumanPlayers();
		int[] userIds = new int[cpuPlayers];
		if (cpuPlayers > 0) {
			int i = 0;
			for (GamePlayer p : players) {
				if (CPUPlayer.class.isAssignableFrom(p.getClass())) {
					userIds[i++] = p.getUser().getUserId();
				}
			}
		}
		return userIds;
	}
	

	/**
	 * Accept a bid from a Player. Sets the going bid to the amount this player
	 * bid
	 * 
	 * @param player
	 *            the GamePlayer making the bid
	 * @param bid
	 *            the amount of the bid (in points)
	 */
	public void makeBid(GamePlayer player, int bid) throws GameException {
		if (bid == 1 || bid < 0 || bid > 4) {
			throw new CheaterException(player, "Illegal bid: " + bid);
		}
		touch();
		Bid newBid = new Bid(player.getIndex(), bid);
		hand.noteBid(newBid);
		for (GamePlayer p : players) {
			((PitchPlayer)p).notifyBidMade(player.getIndex(), bid);
		}
		// increment the bidding rotation
		bidRotation.increment();
		if (getNumHumanPlayers() > 0 && isHumanPlayer((PitchPlayer)player)) {
			executeGamePlay();
		}
	}

	/**
	 * Initialize a new hand for this type of game.
	 * 
	 * @return a SinglesHand
	 */
	protected Hand initNewHand(int size) {
		return new SinglesHand(size);
	}
	

	/**
	 * Start the game. <br>
	 * 
	 * @throws SQLException
	 *             if there was an error storing the game record in the DB.
	 */
	protected void start() {
		
//		this.status = GameStatus.RUNNING;
//		if (this.challenge != null) {
//			this.challenge.expire();
//		}
//		gameWon(0);
		
		super.start();
		
		bidRotation = new Rotation(players.size(), dealerRotation.turn());
		executeGamePlay();

	}
	
	protected static boolean isHumanPlayer(PitchPlayer player) {
		return HumanPlayer.class.isAssignableFrom(player.getClass());
	}
	
	/**
	 * Run the bidding loop.
	 * Should only be called from executeGamePlay()
	 */
	private void executeBidding() {
		while (bidRotation.hasMoreTurns() && getStatus() == GameStatus.RUNNING) {
			PitchPlayer player = (PitchPlayer) (players.elementAt(bidRotation.turn()));
			int bidAmount = player.notifyBidTurn(hand.getBids());
			if (this.getNumHumanPlayers() > 0 && isHumanPlayer(player)) {
				// makeBid() will be called from human player after client sends bid
				return;
			}
			else {
				makeBid(player, bidAmount);				
			}
		}
		// bidding is over
		Bid highBid = hand.getHighBid();
		playRotation = new Rotation(players.size(), hand.getHighBid().getPlayerIndex());
		for (int i = 0; i < players.size(); i++) {
			((PitchPlayer) players.elementAt(i)).notifyBidder(highBid);
		}
		bidRotation = null;
	}
	
	/**
	 * Main game loop.
	 * Exits when a human player has been notified of their turn to bid/play
	 * or when the game is over.
	 */
	protected void executeGamePlay() {
		while (getStatus() == GameStatus.RUNNING) {
			if (bidRotation != null) {
				executeBidding();
				if (bidRotation != null) {
					return;
				}
			}
			else {
				while (trick <= 6 && getStatus() == GameStatus.RUNNING) {
					while (playRotation.hasMoreTurns() && getStatus() == GameStatus.RUNNING) {
						PitchPlayer player = (PitchPlayer) (players.elementAt(playRotation.turn()));
						Card card = player.notifyTurn();
						if (getNumHumanPlayers() > 0 && isHumanPlayer(player)) {
							return;
						}
						else {
							cardPlayed(player, card);
						}
					}
					// trick is over
					hand.scoreTrick(); // gives cards to winner of trick

					// send trick info to all players
					for (int i = 0, n = players.size(); i < n; i++) {
						((GamePlayer) (players.elementAt(i))).notifyTrickWon(hand
								.getWinningPlayer(), hand.getWinningCard());
					}

					trick++;
					if (trick <= 6) {
						// start a new trick
						playRotation = new Rotation(players.size(), hand.getTrickWinner());
						hand.newTrick();
					}
				}
				// hand is over, score it
				scoreHand(hand);
				// send scores to players
				showScores(hand);

				if (getWinnerIndex() < 0) {
					deal(this.handSize);
					bidRotation = new Rotation(players.size(), dealerRotation.turn());
				}
			}
		}
		if (getStatus() == GameStatus.OVER) {
			// game was won
			GamePlayer winner = players.elementAt(getWinnerIndex());
			String winnerName = getWinnerName();
			for (GamePlayer iPlayer : players) {
				iPlayer.gameWon(winnerName);
			}
			factory.logGameWon(this, gameRecord, winner);
		}
	}


	
	/**
	 * Register a played card
	 * 
	 * @param player
	 *            the player throwing the card
	 * @param playedCard
	 *            the card the player played
	 */
	public void cardPlayed(GamePlayer player, Card playedCard) {
		touch();
		// notify all players
		for (int i = 0; i < players.size(); i++) {
			((GamePlayer) (players.elementAt(i))).notifyPlay(player.getIndex(),
					playedCard);
		}
		
		// check card for points
		hand.scoreCard(playRotation.turn(), playedCard);
		// increment the turn
		playRotation.increment();

		if (getNumHumanPlayers() > 0 && isHumanPlayer((PitchPlayer)player)) {
			executeGamePlay();
		}
	}
	
	
	/**
	 * Assign points at the end of a hand. <br>
	 * Sums game points.
	 * 
	 * @param thisHand
	 *            the hand which needs to be scored
	 */
	protected void scoreHand(Hand thisHand) {
		// sequence of events in this method is important
		
		hand.tallyGamePoints();

		// give jack & game stats as necessary
		int jackWinner = hand.getJackWinner();
		if (jackWinner > -1) {
			if (hand.wasJackStolen()) {
				gameRecord.getGamePlayerRecordAtIndex(hand.getJackThrower()).addJackLoss();
				gameRecord.getGamePlayerRecordAtIndex(jackWinner).addJackSteal();
			}
			gameRecord.getGamePlayerRecordAtIndex(jackWinner).addJackPoint();
		}
		int gameWinner = hand.getGameWinner();
		if (gameWinner > -1) {
			gameRecord.getGamePlayerRecordAtIndex(gameWinner).addGamePoint();
		}

		// loop through all players and adjust points
		int n = players.size();
		for (int i = 0; i < n; i++) {
			//( get PlayerScore object for i ).adjustScore(-1*points player i
			// made)
			int pointAdjustment = -1* hand.getPointsMade(i);
			gameRecord.getGamePlayerRecordAtIndex(i).adjustScore(pointAdjustment);
			GamePlayer player = (GamePlayer)players.get(i);
		}

		// check for a winner:
		int winnerIndex = -1;
		for (int i = 0; i < n; i++) {
			int iScore = getPlayerScore(i);

			if (iScore > 0) {
				// not a winner; continue to the next player
				continue;
			} else if (thisHand.getHighBid().getPlayerIndex() == i) {
				// player bid and went out, he's the winner
				winnerIndex = i;
				break;
			} else if (winnerIndex > -1) {
				// two players with sub-1 scores, score according to H,L,J,G
				if (thisHand.getHighWinner() == i || thisHand.getHighWinner() == winnerIndex) {
					winnerIndex = thisHand.getHighWinner();
				}
				else if (thisHand.getLowWinner() == i || thisHand.getLowWinner() == winnerIndex) {
					winnerIndex = thisHand.getLowWinner();
				}
				else if (thisHand.getJackWinner() == i || thisHand.getJackWinner() == winnerIndex) {
					winnerIndex = thisHand.getJackWinner();
				}
				else if (thisHand.getGameWinner() == i || thisHand.getGameWinner() == winnerIndex) {
					winnerIndex = thisHand.getGameWinner();
				}
			} else {
				// so far this player is the only one with a sub-1 score
				winnerIndex = i;
			}
		}
		if (winnerIndex > -1) {
			gameWon(winnerIndex);
		}
	}

	/**
	 * Send scores to the clients
	 * 
	 * @param hand
	 */
	protected void showScores(Hand hand) {
		// construct the message
		// ("name,score,gamePoints;name,score,gamePoints,...")
		// Also, construct a server message indicating which points were scored by each player
		StringBuilder sb = new StringBuilder();
		int n = players.size();
		ArrayList<String> playerScoreMsgs = new ArrayList<String>(players.size());
		for (int i = 0; i < n; i++) {
			GamePlayer player = (GamePlayer) players.elementAt(i);
			sb.append(player.getUsername()).append(",").append(getPlayerScore(i)).append(",")
			.append(hand.getPlayerGamePoints(i)).append((i < (players.size() - 1) ? ";" : ""));
			StringBuilder points = new StringBuilder();
			if (getHighWinner() == i) {
				points.append((points.length() > 0?", ":"")).append("High");
			}
			if (getLowWinner() == i) {
				points.append((points.length() > 0?", ":"")).append("Low");
			}
			if (getJackWinner() == i) {
				points.append((points.length() > 0?", ":"")).append("Jack");
			}
			if (getGameWinner() == i) {
				points.append((points.length() > 0?", ":"")).append("Game");
			}
			if (points.length() > 0) {
				playerScoreMsgs.add(player.getUsername() + " scored " + points);
			}
		}
		String scoresStr = sb.toString();
		String[] playerMessages = playerScoreMsgs.toArray(new String[] {});
		for (int i = 0; i < n; i++) {
			((PitchPlayer) players.elementAt(i)).notifyScores(scoresStr);
			players.elementAt(i).serverMultiMessage(playerMessages);
		}
	}

	/**
	 * Get the winning player's name.
	 */
	protected String getWinnerName() {
		if (getWinnerIndex() > -1) {
			return ((GamePlayer) (players.elementAt(getWinnerIndex())))
					.getUsername();
		} else {
			return null;
		}
	}

	/**
	 * Get the current winner of the High point in this hand.
	 */
	public int getHighWinner() {
		return hand.getHighWinner();
	}

	/**
	 * Get the current winner of the Low point in this hand.
	 */
	public int getLowWinner() {
		return hand.getLowWinner();
	}

	/**
	 * Get the current winner of the Jack point in this hand.
	 */
	public int getJackWinner() {
		return hand.getJackWinner();
	}

	/**
	 * Get the current winner of the Game point in this hand.
	 */
	public int getGameWinner() {
		return hand.getGameWinner();
	}

	/**
	 * Get the going bid
	 */
	public Bid getHighBid() {
		return hand.getHighBid();
	}

	/**
	 * Get game information.
	 * 
	 * @return a String containing the game id, game type, current status, and a
	 *         list of players
	 */
	public String getInfo() {
		StringBuffer infoStr = new StringBuffer(getGameId() + ",s,"
				+ this.status);
		for (int i = 0, n = players.size(); i < n; i++)
			infoStr.append(","
					+ ((GamePlayer) (players.elementAt(i))).getUsername());
		return infoStr.toString() + ";";
	}

	public GameOptions getGameOptions() {
		return gameOptions;
	}
	
	
	@Override
	public GameInfo getGameInfo() {
		GameInfo info = super.getGameInfo();
		info.setGameOptions(gameOptions);
		return info;
	}



}

