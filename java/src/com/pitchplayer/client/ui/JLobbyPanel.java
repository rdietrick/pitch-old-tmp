package com.pitchplayer.client.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.pitchplayer.client.PitchClient;

public class JLobbyPanel extends JPanel implements MouseListener {

	PitchClient client = null;
	
	JTable gameTable;
	GameListTableModel gameListModel = new GameListTableModel();
	UserListModel userListModel = new UserListModel();
	int selectedRow = -1;

	private JTextArea chatLogArea; // chat log
	private JList  userList; // list of logged-in users
	private JButton singlesTableBtn;

	private JTextField msgBox;

	private JButton quitBtn;

	private JButton doublesTableBtn;
	
	/**
	 * Sets up the two top-level components in the user interface.
	 * @param client
	 */
	public JLobbyPanel(PitchClient client) {
		this.client = client;
		JPanel topPanel = new JPanel();
		Dimension dim = new Dimension(790,150);
		topPanel.setPreferredSize(dim);
		topPanel.setMinimumSize(dim);
		initTopPanel(topPanel);
		JPanel bottomPanel = new JPanel();
		bottomPanel.setPreferredSize(dim);
		bottomPanel.setMinimumSize(dim);
		initBottomPanel(bottomPanel);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
		splitPane.setDividerLocation(0.5);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
	}
	
	/**
	 * Initializes the top panel in the UI, containing the "Play Now", "New Table",
	 * and "Options" buttons and the game list table.
	 * @param topPanel
	 */
	protected void initTopPanel(JPanel topPanel) {
		Insets insets = new Insets(4,4,4,4);
		GridBagLayout gbl = new GridBagLayout();
		topPanel.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = insets;
		gbc.weightx = .1;
/*
   		gbc.gridx = 0;
		gbc.gridy = 0;
		JButton playBtn = new JButton("Play Now");
		gbl.setConstraints(playBtn, gbc);
		topPanel.add(playBtn);
*/
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		singlesTableBtn = new JButton("New Singles Table");
		singlesTableBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!client.getInGame()) {
					client.sendCreateGame(1);
					singlesTableBtn.setEnabled(false);
				}
			}
		});
		singlesTableBtn.setToolTipText("Create a new game table");
		gbl.setConstraints(singlesTableBtn, gbc);
		topPanel.add(singlesTableBtn);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 2;
		doublesTableBtn = new JButton("New Doubles Table");
		doublesTableBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!client.getInGame()) {
					client.sendCreateGame(2);
					doublesTableBtn.setEnabled(false);
				}
			}
		});
		doublesTableBtn.setToolTipText("Create a new game table");
		gbl.setConstraints(doublesTableBtn, gbc);
		topPanel.add(doublesTableBtn);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 3;
		JButton optionsBtn = new JButton("Options");
		optionsBtn.setEnabled(false);
		gbl.setConstraints(optionsBtn, gbc);
		topPanel.add(optionsBtn);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 4;
		quitBtn = new JButton("Quit");
		quitBtn.setEnabled(false);
		quitBtn.setToolTipText("Quit the current game");
		quitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (client.getInGame()) {
					client.sendQuitGame();
					toggleInGame(false);
				}
			}
		});
		gbl.setConstraints(quitBtn, gbc);
		topPanel.add(quitBtn);

		JLabel gameListLbl = new JLabel("Current games:");
		gbc.insets = new Insets(4,2,2,2);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weighty = 0.01;
		gbl.setConstraints(gameListLbl, gbc);
		topPanel.add(gameListLbl);
		
		gbc.weightx = .9;
		gbc.weighty = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 5;
		gbc.fill = GridBagConstraints.BOTH;
		gameTable = new JTable(gameListModel);
		gameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gameTable.addMouseListener(this);
		gameTable.setToolTipText("Double-click a gathering game to join");
		// gameListModel.addTableModelListener(gameTable);
		//gameTable.setPreferredScrollableViewportSize(new Dimension(280, 200));
        JScrollPane scrollPane = new JScrollPane(gameTable);
        scrollPane.setPreferredSize(new Dimension(280, 150));
        gbl.setConstraints(scrollPane, gbc);
        topPanel.add(scrollPane);
	}
	
	/**
	 * Initializes the bottom panel in the UI, containing the user list and the
	 * lobby chat components.
	 * @param bottomPanel
	 */
	protected void initBottomPanel(JPanel bottomPanel) {
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		bottomPanel.setLayout(gbl);
		Insets insets = new Insets(4,4,4,4);
			
		userList = new JList(userListModel);
		JScrollPane scrollPane = new JScrollPane(userList);
		scrollPane.setPreferredSize(new Dimension(80, 150));
		gbc.insets = insets;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbl.setConstraints(scrollPane, gbc);
		bottomPanel.add(scrollPane);
		
		chatLogArea = new JTextArea("Welcome to Pitchplayer.com.");
		scrollPane = new JScrollPane(chatLogArea);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = .9;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbl.setConstraints(scrollPane, gbc);
		bottomPanel.add(scrollPane);
		
		insets = new Insets(2,4,8,4);
		JLabel lbl = new JLabel("chat: ");
		gbc.insets = insets;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = .01;
		gbc.weighty = 0.1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(lbl, gbc);
		bottomPanel.add(lbl);
		
		msgBox = new JTextField("enter text to chat");
		msgBox.setToolTipText("Enter messages here to chat");
		msgBox.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent event) {
				if (event.getKeyChar() == KeyEvent.VK_ENTER) {
					client.sendLobbyChat(msgBox.getText());
					msgBox.setText("");
				}
			}

			public void keyReleased(KeyEvent event) {
			}

			public void keyPressed(KeyEvent event) {
			}
		});
		msgBox.addFocusListener(new FocusListener() {
			private boolean chatInstructionsShown = true;

			public void focusGained(FocusEvent event) {
				if (chatInstructionsShown) {
					msgBox.setText("");
					chatInstructionsShown = false;
				}
			}

			public void focusLost(FocusEvent event) {
			}
		});
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = .99;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		gbl.setConstraints(msgBox, gbc);
		bottomPanel.add(msgBox);
	}
	
	/**
	 * Force a refresh of the game list model, which in turn forces the UI
	 * component to refresh.
	 * Called when an "info" message is received from the server, containing
	 * the latest game list information.
	 * @param gameInfo
	 */
	public void notifyGameListUpdate(String[][] gameInfo) {
		gameListModel.setGameList(gameInfo);
	}
	
	/**
	 * Force a refresh of the list of logged-in users.
	 * @param args ths list of users
	 */
	public void notifyUserListUpdate(String[] args) {
		userListModel.setUserList(args);
	}	


	/**
	 * Handle mouse clicks.
	 * In particular, this handles double-clicks on table rows in the game list
	 * table and joins the chosen game.
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			int rowNum = gameTable.rowAtPoint(e.getPoint());
			String gameId = gameListModel.getGameIdForJoin(rowNum);
			if (gameId != null) {
				client.joinGame(gameId);
			}
		}
	}

	/**
	 * Not implemented. 
	 */
	public void mouseEntered(MouseEvent e) {
	}


	/**
	 * Not implemented. 
	 */
	public void mouseExited(MouseEvent e) {
	}


	/**
	 * Not implemented. 
	 */
	public void mousePressed(MouseEvent e) {
	}


	/**
	 * Not implemented. 
	 */
	public void mouseReleased(MouseEvent e) {
	}

	public void showServerMessage(String s) {
		StringBuffer sb = new StringBuffer("server> ").append(s);
		if (chatLogArea.getText() == null || chatLogArea.getText().equals("")) {
			chatLogArea.append(sb.toString());
		}
		else {
			chatLogArea.append(sb.insert(0, "\n").toString());
		}
	}

	public void showChatMessage(String username, String message) {
		chatLogArea.append("\n" + username + "> " + message);
	}

	public void toggleInGame(boolean inGame) {
		quitBtn.setEnabled(inGame);
		singlesTableBtn.setEnabled(!inGame);
		doublesTableBtn.setEnabled(!inGame);
	}
	
}
