package com.pitchplayer.client.ui;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class BlankCardIcon implements Icon, UIConstants {

	public int getIconHeight() {
		return CARD_WIDTH;
	}

	public int getIconWidth() {
		return CARD_HEIGHT;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	      g.setColor(GuiPitchClient.bgColor);
	      g.fillRect(x, y, CARD_WIDTH-1, CARD_HEIGHT-1);
	}
	
}
