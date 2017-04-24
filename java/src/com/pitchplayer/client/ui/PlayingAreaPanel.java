package com.pitchplayer.client.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.pitchplayer.client.ClientCard;
import com.pitchplayer.client.PitchClient;
import com.pitchplayer.client.ui.GuiPitchClient.BidButtonListener;

/**
 * AWT Panel which encapsulates all of the UI components of the playing area.
 * This includes: player names/icons, played cards, and this client's hand.
 * 
 * Need to make sure that the
 */
public class PlayingAreaPanel extends JPanel {

	public static final int CARD_WIDTH = 73;

	public static final int CARD_WIDTH_SMALL = 20;

	public static final int CARD_HEIGHT = 97;

	private static final int TOP_BUFFER = 20;

	private static final int BOTTOM_BUFFER = 10;

	private static final int LEFT_BUFFER = 10;

	private static final int RIGHT_BUFFER = 10;

	private ArrayList players = new ArrayList(4);

	private PitchClient client = null;

	private GuiCard[] guiCards = null; // the drawable cards

	MouseListener mouseListener = null;

	Timer blinkTimer = null;

	private Vector playedCardsQueue = null;

	private static final long BLINK_RATE = 400l; // 500 ms blink rate

	private int blankPlayerIndex = -1;

	private Rectangle handRect = null;

	int hoverCard = -1;

	Image offScreen = null;

	private PitchUI applet;
	
	
	public PlayingAreaPanel(PitchClient client, PitchUI applet) {
		this.client = client;
		this.applet = applet;
		setLayout(new GridBagLayout());
		addMouseListener(new HandMouseListener());// client);
		addMouseMotionListener(new HandMotionListener());
	}


	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		System.err.println("repainting PlayingAreaPanel");
		if (offScreen == null) {
			offScreen = createImage(getSize().width, getSize().height);

		}
		Graphics osg = offScreen.getGraphics();
		osg.setColor(GuiPitchClient.bgColor);
		osg.fillRect(0, 0, getSize().width, getSize().height);
		
		drawPlayerNames(osg);
		drawHand(osg);
		drawPlayedCards(osg);
		
		osg.setClip(0, 0, getSize().width, getSize().height);
		g.drawImage(offScreen, 0, 0, null);
		osg.dispose();
	}

	public Insets getInsets() {
		return new Insets(5, 5, 5, 5);
	}

	public void reinitialize() {
		if (blinkTimer != null) {
			blinkTimer.cancel();
			blinkTimer = null;
		}
		players = new ArrayList(4);
		guiCards = null;
		playedCardsQueue = null;
		blankPlayerIndex = -1;
		hoverCard = -1;
		repaint();
	}

	public void invalidate() {
		for (int i = 0; i < players.size(); i++) {
			((Player) players.get(i)).invalidate();
		}
		initGuiHand();
		super.invalidate();
		offScreen = null;
	}

	public void takeHand() {
		initGuiHand();
		repaint(0, getHandY(), getSize().width, CARD_HEIGHT);
	}

	protected void blankCard(int playerIndex) {
		blankPlayerIndex = playerIndex;
		Point p = ((Player) players.get(playerIndex))
				.getCardPosition(getGraphics());
		repaint(p.x, p.y, CARD_WIDTH, CARD_HEIGHT);
	}

	protected void unBlankCard(int playerIndex) {
		blankPlayerIndex = -1;
		Point p = ((Player) players.get(playerIndex))
				.getCardPosition(getGraphics());
		repaint(p.x, p.y, CARD_WIDTH, CARD_HEIGHT);
	}

	protected void blinkOver() {
		for (int i = 0; i < players.size(); i++) {
			Player player = (Player) players.get(i);
			player.clearCard();
		}
		Dimension dim = getSize();
		repaint(0, 0, dim.width, getHandY() - 1);
		// blinkTimer must be set to null before a call to cardPlayed(), or
		// it will add the card to the queue, creating an infinite loop
		blinkTimer.cancel();
		blinkTimer = null;
		if (playedCardsQueue != null) {
			for (int i = 0; i < playedCardsQueue.size(); i++) {
				applet.playSound(GuiPitchClient.PLAY_CARD_SOUND);
				PlayedCard pCard = (PlayedCard) playedCardsQueue.elementAt(i);
				cardPlayed(pCard.card, pCard.playerIndex);
			}
			playedCardsQueue = null;
		}
	}

	/**
	 * Notify this UI component that a trick was won. Results in the winning
	 * card blinking.
	 * 
	 * @param playerIndex
	 *            the index of the player who won the trick.
	 */
	public void trickWon(int playerIndex) {
		blinkTimer = new Timer();
		blinkTimer.schedule(new BlinkTask(playerIndex), 0, BLINK_RATE);
	}

	class BlinkTask extends TimerTask {
		int playerIndex;

		int blinkCount = 0;

		int numBlinks = 3;

		BlinkTask(int index) {
			this.playerIndex = index;
		}

		public void run() {
			if (blinkCount++ % 2 == 1) {
				blankCard(playerIndex);
			} else {
				unBlankCard(playerIndex);
			}
			if (blinkCount >= (numBlinks * 2) + 1) {
				cancel();
				blinkOver();
			}
		}
	}

	/**
	 * Get the X coordinate of a hand of cards with the specified number of
	 * cards
	 */
	private int getHandX(int handSize) {
		Dimension dim = getSize();
		int cardsWidth = ((handSize - 1) * CARD_WIDTH_SMALL) + CARD_WIDTH;
		return (dim.width - cardsWidth) / 2;
	}

	/**
	 * Get the Y coordinate of the hand of cards
	 */
	private int getHandY() {
		return getSize().height - BOTTOM_BUFFER - CARD_HEIGHT;
	}

	/**
	 * Add a player to this panel
	 */
	public void addPlayer(String playerName, int index) {
		players.add(index, new Player(playerName, index, this));
		repaint();
	}

	/**
	 * Add a player to this panel
	 */
	public void addPlayer(String playerName) {
		players.add(new Player(playerName, players.size(), this));
		repaint();
	}

	/**
	 * Draw all of the player's played cards
	 */
	private void drawPlayedCards(Graphics g) {
		for (int i = 0; i < players.size(); i++) {
			if (blankPlayerIndex != i) {
				((Player) players.get(i)).drawPlayedCard(g);
			} else {
				g.setColor(GuiPitchClient.bgColor);
				Point p = ((Player) players.get(i)).getCardPosition(g);
				g.fillRect(p.x, p.y, CARD_WIDTH, CARD_HEIGHT);
			}
		}
	}

	/**
	 * (Re)initialize the drawable, clickable GUI hand
	 */
	public void initGuiHand() {
		ClientCard[] hand = client.getHand();

		if ((hand == null) || (hand.length == 0)) {
			guiCards = new GuiCard[0];
			return;
		}

		int handTop = getHandY();
		int displayableCards = 0;
		boolean[] gaps = new boolean[hand.length];
		int lastCardIndex = 0;
		int firstCardIndex = -1;
		// count the displayable cards, & initialize an array indicating the
		// indices of the already played cards
		for (int i = 0; i < hand.length; i++) {
			if (!hand[i].wasPlayed()) {
				displayableCards++;
				if (firstCardIndex < 0)
					firstCardIndex = i;
				if (lastCardIndex < i)
					lastCardIndex = i;
			} else {
				gaps[i] = true;
			}
		}
		guiCards = new GuiCard[displayableCards];
		int handLeft = getHandX(hand.length);
		int guiCardCount = 0;
		for (int i = 0; i <= lastCardIndex; i++) {
			if (hand[i] != null && !hand[i].wasPlayed()) {
				int xPos = handLeft + (CARD_WIDTH_SMALL * i);

				// figure out the bounding rectangle of this card by determining
				// if proceeding
				// cards overlap it
				int width = CARD_WIDTH_SMALL; // default to SMALL width
				if (lastCardIndex == i) {
					width = CARD_WIDTH;
				} else {
					for (int j = i + 1; j < lastCardIndex; j++) {
						if (gaps[j]) {
							if (CARD_WIDTH_SMALL * (j - i) >= CARD_WIDTH) {
								width = CARD_WIDTH;
								break;
							} else {
								width += CARD_WIDTH_SMALL;
							}
						} else {
							break;
						}
					}
				}
				guiCards[guiCardCount++] = new GuiCard(hand[i], new Rectangle(
						xPos, handTop, width, CARD_HEIGHT), i);
			}
		}
		handRect = new Rectangle(handLeft, handTop, (CARD_WIDTH_SMALL * 5)
				+ CARD_WIDTH, CARD_HEIGHT);
	}

	/**
	 * Draw the hand of cards
	 */
	private void drawHand(Graphics osg) {
		osg.setColor(GuiPitchClient.bgColor);
		osg.fillRect(getHandX(GuiPitchClient.HAND_SIZE), getHandY(),
				(CARD_WIDTH_SMALL * 5) + CARD_WIDTH, CARD_HEIGHT);
		if (guiCards == null)
			return;
		for (int i = 0; i < guiCards.length; i++) {
			Image cardImg = applet.getImage(guiCards[i].getCard().getFullImageFilename());
			Rectangle rect = guiCards[i].getBoundingRect();
			osg
					.drawImage(cardImg, rect.x, rect.y, GuiPitchClient.bgColor,
							this);
			if (hoverCard == i) {
				osg.setColor(Color.YELLOW);
				osg.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
			}
		}
	}

	/**
	 * Notify the UI that a player has played a card
	 */
	public void cardPlayed(ClientCard card, int index) {
		if (blinkTimer != null) {
			// if currently blinking, add the cards to a queue of cards to
			// be displayed after blinking is finished
			if (playedCardsQueue == null) {
				playedCardsQueue = new Vector();
			}
			playedCardsQueue.add(new PlayedCard(card, index));
		} else {
			applet.playSound(GuiPitchClient.PLAY_CARD_SOUND);
			if (client.getPlaying()) {
				if (players.size() > index) {
					synchronized (players) {
						Player player = (Player) players.get(index);
						player.cardPlayed(card);
						Point p = player.getCardPosition(getGraphics());
						repaint(p.x, p.y, CARD_WIDTH, CARD_HEIGHT);
					}
				}
			}
		}
	}

	/**
	 * Draw all of the player's names
	 */
	public void drawPlayerNames(Graphics g) {
		g.setColor(Color.BLACK);
		for (int i = 0; i < players.size(); i++) {
			((Player) players.get(i)).drawName(g);
		}
	}

	class Player {
		Point namePos;

		Point cardPos;

		String name;

		int index;

		ClientCard myCard = null;

		JPanel parentPanel = null;

		Player(String playerName, int index, JPanel parentPanel) {
			this.name = playerName;
			this.parentPanel = parentPanel;
			this.index = index;
		}

		public void invalidate() {
			namePos = null;
			cardPos = null;
		}

		/**
		 * Indicate that this player has played a card
		 */
		public void cardPlayed(ClientCard card) {
			this.myCard = card;
		}

		public void clearCard() {
			this.myCard = null;
		}

		/**
		 * Get the upper-left coordinate at which the specified player's name
		 * should be drawn.
		 */
		private Point getNamePosition(Graphics g) {
			if (namePos == null) {
				int x, y;
				x = y = 0;
				FontMetrics met = g.getFontMetrics();
				Dimension dim = getSize();
				if (index == 0) {
					x = (dim.width - met.stringWidth(name)) / 2;
					y = TOP_BUFFER;
				} else if (index == 1) {
					x = dim.width - RIGHT_BUFFER - met.stringWidth(name);
					y = (dim.height - BOTTOM_BUFFER - met.getHeight()) / 2;
				} else if (index == 2) {
					x = (dim.width - met.stringWidth(name)) / 2;
					y = dim.height - BOTTOM_BUFFER * 2 - CARD_HEIGHT
							- met.getHeight();
				} else if (index == 3) {
					x = LEFT_BUFFER;
					y = (dim.height - BOTTOM_BUFFER - met.getHeight()) / 2;
				}
				namePos = new Point(x, y);
			}
			return namePos;
		}

		/**
		 * Get the upper-left coordinate at which the specified player's played
		 * card image should appear
		 */
		private Point getCardPosition(Graphics g) {
			if (cardPos == null) {
				int x, y;
				x = y = 0;
				Dimension dim = getSize();
				FontMetrics met = g.getFontMetrics();
				int fHeight = met.getHeight();
				if (index == 0) {
					x = (dim.width - CARD_WIDTH) / 2;
					y = getNamePosition(g).y + fHeight + 5;
				} else if (index == 1) {
					x = dim.width - RIGHT_BUFFER - CARD_WIDTH;
					y = getNamePosition(g).y - fHeight - 5 - CARD_HEIGHT;
				} else if (index == 2) {
					x = (dim.width - CARD_WIDTH) / 2;
					y = getNamePosition(g).y - fHeight - 5 - CARD_HEIGHT;
				} else if (index == 3) {
					x = LEFT_BUFFER;
					y = getNamePosition(g).y - fHeight - 5 - CARD_HEIGHT;
				}
				cardPos = new Point(x, y);
			}
			return cardPos;
		}

		/**
		 * Draw this player's currently played card
		 */
		public void drawPlayedCard(Graphics g) {
			if (myCard != null) {
				Point p = getCardPosition(g);
				Image cardImg = applet.getImage(myCard.getFullImageFilename());
				g.drawImage(cardImg, p.x, p.y, GuiPitchClient.bgColor,
						parentPanel);
			} else {
				g.setColor(GuiPitchClient.bgColor);
				Point p = getCardPosition(g);
				g.fillRect(p.x, p.y, CARD_WIDTH, CARD_HEIGHT);
			}
		}

		/**
		 * Draw this player's name to the screen
		 */
		public void drawName(Graphics g) {
			Point p = getNamePosition(g);
			g.drawString(name, p.x, p.y);
		}

	}

	public boolean imageUpdate(Image image, int flags, int x, int y, int w,
			int h) {
		if ((flags & ALLBITS) == 0)
			return true;// need more updates
		else {
			repaint();
			return true;// image is fully loaded
		}
	}

	/**
	 * Class which listens for mouse clicks If the click coordinates match a
	 * playable card and it's the user's turn, the card is played.
	 */
	class HandMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent event) {
			if ((guiCards == null) || !client.getIsMyTurn()
					|| (blinkTimer != null)
					|| !handRect.contains(event.getPoint())) {
				// disregard clicks that aren't in the hand rectangle
				return;
			}
			for (int i = 0; i < guiCards.length; i++) {
				Rectangle rect = guiCards[i].getBoundingRect();
				if (!guiCards[i].getCard().wasPlayed()
						&& client.isCardPlayable(guiCards[i].getHandIndex())
						&& rect.contains(event.getPoint())) {

					client.sendPlayCard(guiCards[i].getHandIndex());
					initGuiHand();
					Dimension dim = getSize();
					hoverCard = -1;
					repaint(rect.x, rect.y, CARD_WIDTH, CARD_HEIGHT);
					break;
				}
			}
		}

		public void mouseEntered(MouseEvent event) {
		}

		public void mouseExited(MouseEvent event) {
		}

		public void mousePressed(MouseEvent event) {
		}

		public void mouseReleased(MouseEvent event) {
		}
	}

	/**
	 * Class which listens for mouse focuses If it's this player's turn, and the
	 * mouse is hovering over a card in the player's hand, a yellow background
	 * is draw around the card
	 */
	class HandMotionListener implements MouseMotionListener {

		public void mouseMoved(MouseEvent event) {

			if ((guiCards == null) || !client.getIsMyTurn()
					|| !handRect.contains(event.getPoint())) {
				// disregard movements that aren't in the hand rectangle
				if (hoverCard >= 0) {
					Rectangle rect = guiCards[hoverCard].getBoundingRect();
					repaint(rect.x, rect.y, rect.width, rect.height);
				}
				hoverCard = -1;
				return;
			}
			boolean hovering = false;
			for (int i = 0; i < guiCards.length; i++) {
				Rectangle rect = guiCards[i].getBoundingRect();
				if (rect.contains(event.getPoint())) {
					if (!guiCards[i].getCard().wasPlayed()) {
						if (hoverCard >= 0 && hoverCard < i) {
							Rectangle oldRect = guiCards[hoverCard]
									.getBoundingRect();
							repaint(oldRect.x, oldRect.y, rect.x + rect.width
									- oldRect.x, rect.height);
						} else if (hoverCard >= 0 && hoverCard > i) {
							Rectangle oldRect = guiCards[hoverCard]
									.getBoundingRect();
							repaint(rect.x, rect.y, oldRect.x + oldRect.width
									- rect.x, rect.height);
						} else if (hoverCard == -1) {
							repaint(rect.x, rect.y, rect.width, rect.height);
						}
						hoverCard = i;
						hovering = true;
						break;
					} else {
						// currently hovering over a played card (a gap between
						// cards)
						// repaint the old hover area
						if (hoverCard > -1) {
							Rectangle oldRect = guiCards[hoverCard]
									.getBoundingRect();
							repaint(oldRect.x, oldRect.y, oldRect.width,
									oldRect.height);
						}
					}
				}
			}
			if (!hovering && (hoverCard >= 0)
					&& hoverCard < GuiPitchClient.HAND_SIZE) {
				Rectangle oldRect = guiCards[hoverCard].getBoundingRect();
				repaint(oldRect.x, oldRect.y, oldRect.width, oldRect.height);
				hoverCard = -1;
			}
		}

		public void mouseDragged(MouseEvent event) {
		}
	}

	class PlayedCard {
		ClientCard card;

		int playerIndex = -1;

		public PlayedCard(ClientCard card, int playerIndex) {
			this.card = card;
			this.playerIndex = playerIndex;
		}
	}

	public void notifyBid(String bidStr) {
		
	}

}