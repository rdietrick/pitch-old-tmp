package com.pitchplayer.client.ui;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.pitchplayer.Card;
import com.pitchplayer.Command;
import com.pitchplayer.client.ClientCard;
import com.pitchplayer.client.PitchClient;

/**
 * GuiPitchClient.
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
public class GuiPitchClient extends Applet implements PitchUI {

	public static final String SHUFFLE_SOUND = "../sounds/shuffle.au";

	public static final String PLAY_CARD_SOUND = "../sounds/dealcard.au";

	public static final int HAND_SIZE = 6; // num cards in hand (used for
										   // figuring drawing coordinates)


	/** **************** UI Components: ****************** */
	// three top-level panels, togglable via a CardLayout
	CardLayout cardLayout = new CardLayout(0, 0);

	Panel gamePlayPanel = new Panel();

	GameListPanel gameListPanel = null;

	LoginPanel loginPanel = null;

	// panels displayed in the gamePlayPanel
	PlayingAreaPanel playingAreaPanel = null;

	ControlPanel controlPanel = null;

	Dialog bidDialog = null;

	private static final String GAME_PANEL = "gamePanel";

	private static final String GAME_LIST_PANEL = "gameListPanel";

	private static final String LOGIN_PANEL = "loginPanel";

	// BG should be #339900
	public static final int BG_COLOR = 0x339900;

	public static final Color bgColor = new Color(BG_COLOR);

	/** ************** End UI Components ***************** */

	PitchClient client;
	
	public GuiPitchClient() {
		setBackground(bgColor);
	}

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
	public void init() {
		int port = 0;
		try {
			port = Integer.parseInt(getParameter("port"));
		} catch (NumberFormatException e) {
			System.err.println("Bad port number in <APPLET> tag.");
			System.exit(1);
		}
		String server = getParameter("server");
		String sessionId = getParameter("sessionId");
		String username = getParameter("username");

		client = new PitchClient(this, server, port, sessionId, username);

		// Initialize the UI
		Dimension size = getSize();
		System.err.println("applet size = " + size.width + "x" + size.height);
		setLayout(cardLayout);

		playingAreaPanel = new PlayingAreaPanel(client, this);
		controlPanel = new ControlPanel(client);

		playingAreaPanel.setBackground(GuiPitchClient.bgColor);
		controlPanel.setBackground(GuiPitchClient.bgColor);
		gamePlayPanel.setBackground(GuiPitchClient.bgColor);

		//	gamePlayPanel.setLayout(new GridLayout(1,2));
		gamePlayPanel.setLayout(new BorderLayout());
		gamePlayPanel.add(playingAreaPanel, BorderLayout.CENTER);
		gamePlayPanel.add(controlPanel, BorderLayout.EAST);

		loginPanel = new LoginPanel(client);
		gameListPanel = new GameListPanel(client);
		gameListPanel.setBackground(GuiPitchClient.bgColor);

		add(GAME_PANEL, gamePlayPanel);
		add(GAME_LIST_PANEL, gameListPanel);
		add(LOGIN_PANEL, loginPanel);

		cardLayout.show(this, GAME_PANEL);
		super.init();
	}


	/**
	 * Notify this client to display the requested list of games
	 * 
	 * @param gameList
	 *            the list of games received from the server Structure of each
	 *            element of the returned array is as follows: game
	 *            id;status;player_1_name[;player_2_name][;player_3_name][;player_4_name]
	 */
	public void notifyDisplayGameList(String[][] gameList) {
		gameListPanel.setGameList(gameList);
		// switch to the game list panel
		gameListPanel.invalidate();
		cardLayout.show(this, GAME_LIST_PANEL);
	}

	/**
	 * Receive message from server whenever a game join is successful.
	 *  
	 */
	public void notifyJoinOk(int gameType, String[] playerNames) {
		for (int i = 0; i < playerNames.length; i++) {
			playingAreaPanel.addPlayer(playerNames[i], i);
		}
		cardLayout.show(this, GAME_PANEL);
	}

	/**
	 * Switch to the game panel
	 */
	public void showGame() {
		cardLayout.show(this, GAME_PANEL);
	}

	/**
	 * Notify browser that join was unsuccesful
	 */
	public void notifyJoinFailed() {
		//	window.call("joinFailed", null);
	}

	/**
	 * Notify browser that a player was added to the game
	 */
	public void notifyPlayerAdded(String playerName) {
		playingAreaPanel.addPlayer(playerName);
		controlPanel.setPlayerNames((String[]) client.getPlayerNames()
				.toArray(new String[] {}));
		if (client.getNumPlayers() > 2 && client.getIsGatherer()) {
			controlPanel
					.showServerMessage("Click the \"Start Game\" button below to start the game.");
		}
	}

	/**
	 * Notify the browser that a card was played by another player.
	 */
	public void notifyCardPlayed(int playerIndex, ClientCard card) {
		if (client.getTrumpSuit() == -1) {
			// first card of hand, update the bid info in the UI
			controlPanel.setBidder(client.getBidder(), (String) client.getPlayerNames()
					.get(playerIndex), card.getSuit(), client.getBidAmt());
		}
		playingAreaPanel.cardPlayed(card, playerIndex);
	}

	/**
	 * Notify browser of a server message
	 */
	public void notifyServerMessage(String msg) {
		//	String[] args = {msg};
		//	window.call("showServerMessage", args);
		// jsEval("showServerMessage", msg);

		controlPanel.showServerMessage("dealer> " + msg);
		if (msg.indexOf(" logged in.") > -1) {
			//	    playingAreaPanel.addPlayer(loginPanel.getUsername());
			loginPanel.loginSuccessful(msg);
		} else if (msg.indexOf("Invalid login.") == 0) {
			loginPanel.loginFailed("Invalid username or password.");
		}
	}

	/**
	 * Notify UI of chat message
	 */
	public void notifyChatMessage(String msg) {
		controlPanel.showServerMessage(msg);
	}

	/**
	 * Get the (window) frame for this applet
	 */
	static Frame getFrame(Component c) {
		Frame frame = null;

		while ((c = c.getParent()) != null) {
			if (c instanceof Frame)
				frame = (Frame) c;
		}
		return frame;
	}

	/**
	 * Called to notify this client that it is the user's turn to bid.
	 */
	public void notifyMyBid(String bidStr) {
		StringTokenizer st = new StringTokenizer(bidStr, ";");
		Frame myFrame = getFrame(this);
		bidDialog = new Dialog(myFrame, "Your bid", true);
		bidDialog.setBackground(GuiPitchClient.bgColor);
		bidDialog.setLocation(myFrame.getLocationOnScreen());

		bidDialog.setLayout(new BorderLayout());
		int goingBid = 0;
		int numBids = st.countTokens();

		// FIX: don't need to use two panels if it's the first bid
		if (numBids > 0) {
			Panel bidInfoPanel = new Panel();
			bidInfoPanel.setLayout(new GridLayout(numBids, 2));
			while (st.hasMoreTokens()) {
				String bidInfo = st.nextToken();
				String bidAmtStr = bidInfo.substring(bidInfo.length() - 1);
				int bidAmt = Integer.parseInt(bidAmtStr);
				if (bidAmt > goingBid) {
					goingBid = bidAmt;
				}
				bidInfoPanel.add(new Label(bidInfo.substring(0, bidInfo
						.length() - 1)));
				if (bidAmt == 0) {
					bidInfoPanel.add(new Label("pass", Label.CENTER));
				} else {
					bidInfoPanel.add(new Label(bidAmtStr, Label.CENTER));
				}
			}
			bidDialog.add(bidInfoPanel, BorderLayout.CENTER);
		}

		Panel buttonPanel = new Panel();
		if (!((numBids == (client.getNumPlayers() - 1)) && (goingBid == 0))) {
			Button passButton = new Button("Pass");
			passButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					bidDialog.dispose();
					client.sendBid(0);
				}
			});
			buttonPanel.add(passButton);
		}
		// up the lowest bid to two, so we don't print a 1-bid option
		if (goingBid == 0) {
			goingBid = 1;
		}
		for (int i = goingBid + 1; i <= 4; i++) {
			Button bidButton = new Button(String.valueOf(i));
			bidButton.addActionListener(new BidButtonListener(i));
			buttonPanel.add(bidButton);
		}
		bidDialog.add(buttonPanel, BorderLayout.SOUTH);
		bidDialog.pack();
		//	bidDialog.setResizable(false);
		bidDialog.show();
	}

	class BidButtonListener implements ActionListener {
		private int bidAmt = 0;

		public BidButtonListener(int bidAmt) {
			this.bidAmt = bidAmt;
		}

		public void actionPerformed(ActionEvent event) {
			bidDialog.dispose();
			client.sendBid(bidAmt);
		}
	}

	/**
	 * Create a new game by sending a 'create' command to the server. Assumes
	 * that the create command was successfull, and displays the game screen.
	 * 
	 * @param gameType
	 *            the type of game (singles, doubles) to create
	 */
	public void sendCreateGame(int gameType) {
		client.sendCreateGame(gameType);
		playingAreaPanel.addPlayer(getName());
		controlPanel.invalidate();
		controlPanel.setPlayerNames((String[]) client.getPlayerNames()
				.toArray(new String[] {}));
		cardLayout.show(this, GAME_PANEL);
		notifyServerMessage("Click the \"Add Player\" button to add computer players to your game.");
		notifyServerMessage("When three players are in the game, you can press the \"Start Game\" button to begin playing.");
	}

	/**
	 * Dispay new hand
	 */
	public void notifyTakeHand(String strHand) {
		playSound(SHUFFLE_SOUND);
		if (!client.getPlaying()) {
			controlPanel.invalidate();
			controlPanel.initScores();
		}
		playingAreaPanel.takeHand();
	}

	/**
	 * Notify the UI that it is this player's turn.
	 */
	public void notifyMyTurn(boolean isMyTurn) {
		// need to add some visual indicator that it is the player's turn  	
	}

	/**
	 * Notify the UI to display all scores.
	 */
	public void notifyDisplayScores(String scoreString) {
		System.err.println("received scores string: " + scoreString);
		StringTokenizer st = new StringTokenizer(scoreString, ";");
		//	StringBuffer scores = new StringBuffer();
		int[] scores = new int[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			StringTokenizer scoreToker = new StringTokenizer(st.nextToken(),
					",");
			int score = Integer.parseInt(scoreToker.nextToken());
			scores[i++] = score;
		}
		controlPanel.clearBids();
		controlPanel.setScores(scores);
	}

	/**
	 *  
	 */
	public void notifyGameAborted(String quitterName) {
		playingAreaPanel.reinitialize();
	}

	/**
	 *  
	 */
	public void notifyGameOver(String winnerName) {
		playingAreaPanel.reinitialize();
	}

	/**
	 * Quit the current game by sending a 'quit' command to the server.
	 */
	public void quitGame() {
		client.send(new Command("quit"));
		playingAreaPanel.reinitialize();
	}

	/**
	 * Notify this client that a trick was won by a player.
	 * 
	 * @param winnerString
	 *            is of the format playerIndex|Card.toString()
	 */
	public void notifyTrickWon(int playerIndex, Card card) {
		//	controlPanel.showServerMessage(((String)playerNames.get(playerIndex))
		// + " won trick with " + card);
		playingAreaPanel.trickWon(playerIndex);
	}

	/**
	 * Paint the component on the screen.
	 */
	public void paint(Graphics g) {
		g.setColor(GuiPitchClient.bgColor);
		Dimension dim = getSize();
		g.fillRect(0, 0, dim.width, dim.height);
	}

	/**
	 * Find out whether this client is actively playing in a game.
	 */
	public boolean getIsPlaying() {
		return client.getPlaying();
	}

	/**
	 * Find out whether this client is in a game (running or gathering)
	 */
	public boolean getInGame() {
		return client.getInGame();
	}

	/**
	 * Find out which type of game this user is in.
	 * 
	 * @return one of NO_GAME, GAME_TYPE_SINGLES, or GAME_TYPE_DOUBLES
	 */
	public int getGameType() {
		return client.getGameType();
	}

	public void playSound(String soundFile) {
		play(getCodeBase(), soundFile);
	}
	
	/**
	 * Returns an image relative to the location of the applet.
	 */
	public Image getImage(String s) {
		return getImage(getCodeBase(), s);
	}


	public void notifyWinningBid(int playerIndex, int bidAmt) {
		// TODO Auto-generated method stub
		
	}

	public void sendJoinGame(long gameId) {
		client.sendJoinGame(gameId);
	}

	public void notifyDisplayUserList(String[] args) {
		// TODO Auto-generated method stub
		
	}

	public void notifyAuthFailed() {
		// TODO Auto-generated method stub
		
	}

	public void notifyAuthSucceeded() {
		// TODO Auto-generated method stub
		
	}

	public void notifyLobbyChat(String username, String message) {
		// TODO Auto-generated method stub
		
	}

}