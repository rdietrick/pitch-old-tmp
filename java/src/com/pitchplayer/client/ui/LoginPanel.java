package com.pitchplayer.client.ui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.pitchplayer.client.PitchClient;

public class LoginPanel extends Panel {

	private static final String LOGIN_PROMPT_MSG = "Enter your username and password to login.";

	private Button loginButton = new Button("Login");

	private TextField usernameField = new TextField();

	private TextField passwordField = new TextField();

	private Label msgLabel = new Label(LOGIN_PROMPT_MSG);

	public LoginPanel(PitchClient client) {
		setLayout(new BorderLayout());

		add(msgLabel, BorderLayout.NORTH);

		Panel textPanel = new Panel();
		GridLayout layout = new GridLayout(2, 2);
		layout.setVgap(4);
		textPanel.setLayout(layout);
		textPanel.setBackground(GuiPitchClient.bgColor);

		loginButton.addActionListener(new LoginButtonListener(client));
		loginButton.setBackground(GuiPitchClient.bgColor);
		passwordField.setEchoChar('*');

		Label usernameLabel = new Label("Username: ");
		Label passwordLabel = new Label("Password: ");
		usernameLabel.setBackground(GuiPitchClient.bgColor);
		passwordLabel.setBackground(GuiPitchClient.bgColor);
		usernameField.setBackground(GuiPitchClient.bgColor);
		passwordField.setBackground(GuiPitchClient.bgColor);
		msgLabel.setBackground(GuiPitchClient.bgColor);

		textPanel.add(usernameLabel);
		textPanel.add(usernameField);
		textPanel.add(passwordLabel);
		textPanel.add(passwordField);

		add(textPanel, BorderLayout.CENTER);
		/*
		 * Panel loginPanel = new Panel(); loginPanel.setLayout(new
		 * GridBagLayout()); loginPanel.add(loginButton);
		 */
		add(loginButton, BorderLayout.SOUTH);
	}

	public String getUsername() {
		return usernameField.getText();
	}

	/**
	 * Login succeeded. Show message and wait for game list.
	 */
	public void loginSuccessful(String msg) {
		msgLabel.setText(msg);
	}

	public void loginFailed(String msg) {
		msgLabel.setText(msg);
	}

	public Insets getInsets() {
		Dimension dim = getSize();
		FontMetrics met = getGraphics().getFontMetrics();
		int vertSpace = (dim.height - 100) / 2;
		int horSpace = (dim.width - met.stringWidth(LOGIN_PROMPT_MSG)) / 2;
		return new Insets(vertSpace, horSpace, vertSpace, horSpace);
	}

	class LoginButtonListener implements ActionListener {
		private PitchClient client = null;

		LoginButtonListener(PitchClient target) {
			this.client = target;
		}

		public void actionPerformed(ActionEvent event) {
			client.login(usernameField.getText(), passwordField.getText());
		}
	}

	public void paint(Graphics g) {
		g.setColor(GuiPitchClient.bgColor);
		Dimension dim = getSize();
		g.fillRect(0, 0, dim.width, dim.height);
	}

	/*
	 * class TextPanel extends Panel { public void paint(Graphics g) {
	 * g.setColor(GuiPitchClient.bgColor); Dimension dim = getSize();
	 * g.fillRect(0,0,dim.width, dim.height); } }
	 */

}