package com.pitchplayer.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pitchplayer.Card;
import com.pitchplayer.client.ClientCard;
import com.pitchplayer.client.PitchClient;

public class PitchSwingApplet extends JApplet implements PitchUI, ChangeListener {
	
	private static final String GAME_TAB_TITLE = "Game";


	private PitchClient client;
	
	//JPlayingAreaPanel gamePanel = null;
	JPanel gamePanel = null;
	PlayingAreaPanel playingAreaPanel = null;

	private ControlPanel controlPanel;

	private JDialog bidDialog;


	static ScoringTableModel scoreModel = new ScoringTableModel();

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
		String gameId = getParameter("gameId");
		client = new PitchClient(this, server, port, sessionId, username);
		System.err.println("created PitchClient");
		scoreModel.reinit();
		getFrame(this).addWindowListener(new WindowListener() {

			public void windowActivated(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
				if (client != null) {
					client.stop();
					client = null;
				}
			}

			public void windowClosing(WindowEvent e) {
			}

			public void windowDeactivated(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
				client.startGameListUpdates();
			}

			public void windowIconified(WindowEvent e) {
				client.pauseGameListUpdates();
			}

			public void windowOpened(WindowEvent e) {
			}
			
		});

		gamePanel = new JPanel();
		gamePanel.setBackground(GuiPitchClient.bgColor);

		playingAreaPanel = new PlayingAreaPanel(client, this);
		playingAreaPanel.setBackground(GuiPitchClient.bgColor);
		controlPanel = new ControlPanel(client);
		// controlPanel.setBackground(GuiPitchClient.bgColor);
		controlPanel.setBorder(BorderFactory.createEtchedBorder());
		gamePanel.setLayout(new BorderLayout());
		gamePanel.add(playingAreaPanel, BorderLayout.CENTER);
		gamePanel.add(controlPanel, BorderLayout.EAST);
		
		getContentPane().setBackground(GuiPitchClient.bgColor);
		getContentPane().add(gamePanel);
		System.err.println("authenticating");
		client.authenticate(gameId);
		System.err.println("authenticated");
	}

	public void notifyJoinOk(int gameType, String[] playerNames) {
		reInitGamePanel();
		for (int i = 0; i < playerNames.length; i++) {
			playingAreaPanel.addPlayer(playerNames[i], i);
		}
		scoreModel.setPlayers(client.getPlayerNames());
	}

	public void reInitGamePanel() {
		scoreModel.reinit();
		controlPanel.reinitialize();
		playingAreaPanel.reinitialize();
	}

	public void notifyJoinFailed() {
		// lobbyPanel.showServerMessage("notifyJoinFailed not implemented yet");
	}

	public void notifyPlayerAdded(String playerName) {
		playingAreaPanel.addPlayer(playerName);
		scoreModel.setPlayers(client.getPlayerNames());
		// need to update the scoreboard in the control panel
		if (client.getNumPlayers() > 2 && client.getIsGatherer()) {
			controlPanel
					.showServerMessage("Click the \"Start Game\" button below to start the game.");
		}
	}

	public void notifyCardPlayed(int playerIndex, ClientCard card) {
		if (client.getTrumpSuit() == -1) {
			// first card of hand, update the bid info in the UI
			controlPanel.setBidder(client.getBidder(), (String) client.getPlayerNames()
					.get(playerIndex), card.getSuit(), client.getBidAmt());
		}
		playingAreaPanel.cardPlayed(card, playerIndex);
	}

	public void notifyServerMessage(String msg) {
		if (client.getInGame()) {
			controlPanel.showServerMessage(msg);
		}
		else {
			// lobbyPanel.showServerMessage(msg);
		}
	}

	public void notifyChatMessage(String msg) {
		controlPanel.showChatMessage(msg);
	}

	public void notifyWinningBid(int playerIndex, int bidAmt) {
		controlPanel.showServerMessage(client.getPlayerNames().get(playerIndex) + 
				" got final bid of" + bidAmt);
	}

	/**
	 * Called when scores are received from the server.
	 * Updates the score model to reflect the latest scores.
	 */
	public void notifyDisplayScores(String scoreString) {
		StringTokenizer st = new StringTokenizer(scoreString, ";");
		//	StringBuffer scores = new StringBuffer();
		String[] scores = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			StringTokenizer scoreToker = new StringTokenizer(st.nextToken(),
					",");
			scoreToker.nextToken(); // first token is name -- no longer used
			int score = Integer.parseInt(scoreToker.nextToken()); // 2nd token is score
			String gamePoints = scoreToker.nextToken(); // 3rd token is game points
			scores[i++] = String.valueOf(score) + " (" + gamePoints + ")";
		}
		controlPanel.clearBids();
		scoreModel.updateScores(scores);
	}

	public void notifyGameAborted(String quitterName) {
		// lobbyPanel.toggleInGame(false);
		controlPanel.showServerMessage("Player " + quitterName + " quit.  Game Over.");
	}

	public void notifyGameOver(String winnerName) {
		// lobbyPanel.toggleInGame(false);
		controlPanel.showServerMessage("And the winner is ... " + winnerName);
	}

	
	public void notifyTakeHand(String strHand) {
		playSound(GuiPitchClient.SHUFFLE_SOUND);
		if (!client.getPlaying()) {
			controlPanel.invalidate();
			controlPanel.initScores();
		}
		playingAreaPanel.takeHand();
	}

	public void notifyMyTurn(boolean isMyTurn) {
		// nothing to do here
	}

	public void notifyTrickWon(int playerIndex, Card card) {
		playingAreaPanel.trickWon(playerIndex);
	}

	/**
	 * Display the list of current games.
	 */
	public void notifyDisplayGameList(String[][] gameInfo) {
		// lobbyPanel.notifyGameListUpdate(gameInfo);
	}

	/**
	 * Display the list of currently logged-in users.
	 */
	public void notifyDisplayUserList(String[] args) {
		// lobbyPanel.notifyUserListUpdate(args);
	}


	public void notifyMyBid(String bidStr) {
		StringTokenizer st = new StringTokenizer(bidStr, ";");
		Frame myFrame = getFrame(this);
		bidDialog = new JDialog(myFrame, "Your bid", true);
		bidDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		bidDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				// do not allow window to close
			}
		});
		
		bidDialog.getContentPane().setLayout(new BorderLayout());
		int goingBid = 0;
		int numBids = st.countTokens();

		// TODO: don't need to use two panels if it's the first bid
		if (numBids > 0) {
			JPanel bidInfoPanel = new JPanel();
			bidInfoPanel.setLayout(new GridLayout(numBids, 2));
			while (st.hasMoreTokens()) {
				String bidInfo = st.nextToken();
				String bidAmtStr = bidInfo.substring(bidInfo.length() - 1);
				int bidAmt = Integer.parseInt(bidAmtStr);
				if (bidAmt > goingBid) {
					goingBid = bidAmt;
				}
				bidInfoPanel.add(new JLabel(bidInfo.substring(0, bidInfo
						.length() - 1)));
				if (bidAmt == 0) {
					bidInfoPanel.add(new JLabel("pass", JLabel.CENTER));
				} else {
					bidInfoPanel.add(new JLabel(bidAmtStr, JLabel.CENTER));
				}
			}
			bidDialog.getContentPane().add(bidInfoPanel, BorderLayout.CENTER);
		}

		JPanel buttonPanel = new JPanel();
		if (!((numBids == (client.getNumPlayers() - 1)) && (goingBid == 0))) {
			JButton passButton = new JButton("Pass");
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
			JButton bidButton = new JButton(String.valueOf(i));
			bidButton.addActionListener(new BidButtonListener(i));
			buttonPanel.add(bidButton);
		}
		bidDialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		bidDialog.pack();
		//	bidDialog.setResizable(false);
		Point newLocation = myFrame.getLocationOnScreen();
		newLocation.translate((playingAreaPanel.getWidth()/2)-bidDialog.getWidth()/2, playingAreaPanel.getHeight()/2);
		bidDialog.setLocation(newLocation);
		bidDialog.show();
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


	public void stateChanged(ChangeEvent e) {
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

	public void finalize() {
		client.stop();
		client = null;
	}

	public void notifyAuthFailed() {
		// lobbyPanel.showServerMessage("Authentication failed.  Close window and try again.");
		client.stop();
		client = null;
	}

	public void notifyAuthSucceeded() {
		// lobbyPanel.showServerMessage("User " + client.getName() + " logged in.");
		client.startGameListUpdates();
	}

	/**
	 * Notify this UI that a lobby chat message has been received.
	 */
	public void notifyLobbyChat(String username, String message) {
		// lobbyPanel.showChatMessage(username, message);
	}

}
