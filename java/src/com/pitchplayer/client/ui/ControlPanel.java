package com.pitchplayer.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.pitchplayer.client.PitchClient;
import com.pitchplayer.Card;
import com.pitchplayer.Command;

/**
 * Panel displayed in right half of playing screen. Displays chat dialog and
 * text ares, scoreboard, and buttons for start game, quit, & list games.
 */
public class ControlPanel extends JPanel {

	// suit characters
	public static final char HEART = '\u2665';
	public static final char SPADE = '\u2660';
	public static final char CLUB = '\u2663';
	public static final char DIAMOND = '\u2666';

	private static final String CHAT_INSTRUCTIONS = "type here to chat";

	private PitchClient client = null;

	// UI components
	private JTextArea gameMsgArea = new JTextArea("", 10, 25);
	private JTextArea lobbyMsgArea = new JTextArea("", 10, 25);
	private JTextField chatField = new JTextField(CHAT_INSTRUCTIONS);
	private JButton startButton = new JButton("Start Game");
	JButton addPlayerButton = new JButton("Add Player");

	
	public Insets getInsets() {
		return new Insets(5, 10, 5, 10);
	}

	public ControlPanel(PitchClient pClient) {
		this.client = pClient;
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gbl);

		JLabel lbl = new JLabel("Scoreboard");
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.gridwidth = 2;
		gbl.setConstraints(lbl, gbc);
		add(lbl);
		
		JTable scoreTable = new JTable(PitchSwingApplet.scoreModel);
		// scoreTable.setBackground(GuiPitchClient.bgColor);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        //scrollPane.getViewport().setBackground(GuiPitchClient.bgColor);
		//scrollPane.setBackground(GuiPitchClient.bgColor);
        scrollPane.setPreferredSize(new Dimension(280, 145));
        //scoreTable.setBackground(GuiPitchClient.bgColor);
		gbc.insets = new Insets(4,0,2,0);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 0.9;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbl.setConstraints(scrollPane, gbc);
		add(scrollPane);
		
		// messageArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		gameMsgArea.setLineWrap(true);
		JScrollPane msgPane = new JScrollPane(gameMsgArea);
		msgPane.setMaximumSize(new Dimension(280, 200));
		gbc.insets = new Insets(6,2,4,2);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbl.setConstraints(msgPane, gbc);
		add(msgPane, gbc);

		gbc.weighty = 0.01;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		JLabel chatLbl = new JLabel("chat:");
		gbl.setConstraints(chatLbl, gbc);
		add(chatLbl);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbl.setConstraints(chatField, gbc);
		chatField.setToolTipText("Enter messages here to chat");
		// chatField.setEnabled(false);
		add(chatField);
		chatField.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent event) {
				if (event.getKeyChar() == KeyEvent.VK_ENTER) {
					client.sendChat(chatField.getText());
					chatField.setText("");
				}
			}

			public void keyReleased(KeyEvent event) {
			}

			public void keyPressed(KeyEvent event) {
			}
		});
		chatField.addFocusListener(new FocusListener() {
			private boolean chatInstructionsShown = true;

			public void focusGained(FocusEvent event) {
				if (chatInstructionsShown ) {
					chatField.setText("");
					chatInstructionsShown = false;
				}
			}

			public void focusLost(FocusEvent event) {
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		startButton.addActionListener(new StartButtonListener());
		startButton.setEnabled(false);
		startButton.setToolTipText("Press to start game");
		addPlayerButton.addActionListener(new AddPlayerButtonListener());
		addPlayerButton.setEnabled(false);
		addPlayerButton.setToolTipText("Add a computer player to the game");

		buttonPanel.add(addPlayerButton);
		buttonPanel.add(startButton);

		// use the following test to determine whether the AddPlayer button is
		// clickable
		// 	if ((!client.getGameStarted()) && client.getIsGatherer() &&
		// (client.getNumPlayers() < 4)) { }

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(4,2,4,2);
		gbl.setConstraints(buttonPanel, gbc);
		add(buttonPanel);
		
	}

	public void showServerMessage(String msg) {
		gameMsgArea.append("\n" + msg);
		scrollChatArea();
	}

	public void invalidate() {
		System.err.println("ControlPanel.invalidate() called");
		if ((!client.getGameStarted()) && client.getIsGatherer()) {
			if (client.getNumPlayers() < 4) {
				if (!addPlayerButton.isEnabled()) {
					addPlayerButton.setEnabled(true);
				}
			} else {
				if (addPlayerButton.isEnabled()) {
					addPlayerButton.setEnabled(false);
				}
			}
			if (!startButton.isEnabled()) {
				startButton.setEnabled(true);
			}
		} else {
			if (addPlayerButton.isEnabled()) {
				addPlayerButton.setEnabled(false);
			}
			if (startButton.isEnabled()) {
				startButton.setEnabled(false);
			}
		}
		super.invalidate();
	}



	/**
	 * Set the bidder
	 */
	public void setBidder(int playerIndex, String playerName, int suit,
			int bidAmount) {
		char trumpChar = SPADE;
		Color trumpColor = Color.BLACK;
		if (suit == Card.HEART) {
			trumpChar = HEART;
			trumpColor = Color.RED;
		} else if (suit == Card.DIAMOND) {
			trumpChar = DIAMOND;
			trumpColor = Color.RED;
		} else if (suit == Card.CLUB) {
			trumpChar = CLUB;
		}
		for (int i = 0; i < client.getNumPlayers(); i++) {
			StringBuffer bid = new StringBuffer();
			if (i == playerIndex) {
				for (int j = 0; j < bidAmount; j++) {
					bid.append(trumpChar);
				}
			}
			// TODO: replace the following lines with code which shows the bidder and amount
			// bidLabels[i].setForeground(trumpColor);
			// bidLabels[i].setText(bid.toString());
		}
	}


	class BottomPanel extends JPanel {
		BottomPanel() {
			this.setLayout(new GridLayout(2, 1));
		}

		public Insets getInsets() {
			return new Insets(5, 0, 0, 0);
		}

		public void paint(Graphics g) {
			g.setColor(GuiPitchClient.bgColor);
			Dimension dim = getSize();
			g.fillRect(0, 0, dim.width, dim.height);

		}
	}

	class StartButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			client.sendCommand("start");
		}
	}

	class AddPlayerButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			client.addCPUPlayer();
		}
	}

	class GameListButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			// client.showGameList();
			System.err.println("Button disabled due to refactoring");
		}
	}

	public void setPlayerNames(String[] strings) {
		// TODO Auto-generated method stub
		
	}

	public void initScores() {
		// TODO Auto-generated method stub
		
	}

	public void clearBids() {
		// TODO Auto-generated method stub
		
	}

	
	public void setScores(int[] scores) {
		// TODO Auto-generated method stub
		
	}

	public void showChatMessage(String msg) {
		gameMsgArea.append("\n" + msg);
		scrollChatArea();
	}

	private void scrollChatArea() {
		int x;
		gameMsgArea.selectAll();
		x = gameMsgArea.getSelectionEnd();
		gameMsgArea.select(x,x);		
	}
	
	public void addPlayer(String playerName) {
		showServerMessage(playerName + " joined game");
	}

	/**
	 * Reinitialize the chat area before a new game.
	 *
	 */
	public void reinitialize() {
		gameMsgArea.setText("");
	}
	


}