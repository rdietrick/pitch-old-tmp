package com.pitchplayer.client.ui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

import com.pitchplayer.client.PitchClient;

/**
 * Panel responsible for displaying the list of all available games on the
 * server.
 */
public class GameListPanel extends Panel {

	private GuiPitchClient client = null;

	public GameListPanel(PitchClient pitchClient) {
		super();
		setLayout(new BorderLayout());
		setBackground(GuiPitchClient.bgColor);
	}

	/**
	 * Set the game list this component is to display
	 */
	public void setGameList(String[][] gameList) {
		// remove all old components
		while (getComponentCount() > 0) {
			remove(0);
		}

		if (gameList == null || gameList.length == 0) {
			add(new GreenLabel(
					"No games in progress.  Click below to start a new one."));
		} else {
			GridLayout grid = new GridLayout(gameList.length + 1, 7, 2, 2);
			setLayout(grid);
			// add header row
			add(new GreenLabel("Table #"));
			add(new GreenLabel("Game Type"));
			add(new GreenLabel("Status"));
			add(new GreenLabel("Player 1"));
			add(new GreenLabel("Player 2"));
			add(new GreenLabel("Player 3"));
			add(new GreenLabel("Player 4"));
			for (int i = 0; i < gameList.length; i++) {
				System.err.println("game info: " + gameList[i]);
				String gameIdStr = gameList[i][0];
				System.err.println("game ID: " + gameIdStr);
				long gameId = Long.parseLong(gameIdStr);
				add(new GreenLabel(gameIdStr));
				
				String gameType = gameList[i][1];
				add(new GreenLabel( (gameType.equalsIgnoreCase("s")?"Singles":"Doubles") ));
				
				String status = gameList[i][2];
				add(new GreenLabel(status));
				
				int numPlayers = 0;
				for (int j=0;j<4;j++) {
					String playerName = gameList[i][3+j];
					if (playerName != null) {
						numPlayers++;
						add(new GreenLabel(playerName));						
					}
					else {
						System.out.println("null player slot; i = " + j + "; status = " + status);
						if (j == numPlayers && status.equals("gathering")) {
							Button joinButton = new Button("Join");
							joinButton.addActionListener(new JoinButtonListener(
									client, gameId));
							add(joinButton);
						}
						else {
							add(new GreenLabel(""));
						}
					}
				}
			}
		}
		doLayout();
	}

	class GreenLabel extends Label {
		public GreenLabel(String msg) {
			super(msg);
			setBackground(GuiPitchClient.bgColor);
		}
	}

	class GameButtonListener implements ActionListener {
		private GuiPitchClient client = null;

		private int gameType = 0;

		GameButtonListener(GuiPitchClient target, int gameType) {
			this.client = target;
			this.gameType = gameType;
		}

		public void actionPerformed(ActionEvent event) {
			client.sendCreateGame(gameType);
		}
	}

	class JoinButtonListener implements ActionListener {
		private GuiPitchClient client = null;

		private long gameId;

		JoinButtonListener(GuiPitchClient target, long gameId) {
			this.client = target;
			this.gameId = gameId;
		}

		public void actionPerformed(ActionEvent event) {
			client.sendJoinGame(gameId);
		}
	}

	/**
	 * Paint this component
	 */
	public void paint(Graphics g) {
		/*
		 * FontMetrics met = g.getFontMetrics(); lineHeight = met.getHeight() +
		 * 4; // two pixel buffer top and bottom
		 * 
		 * for (int i=0;i <gameList.length;i++) { g.drawString(LEFT_BUFFER, }
		 */
	}

}