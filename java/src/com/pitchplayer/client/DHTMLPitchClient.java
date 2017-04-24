package com.pitchplayer.client;

import java.applet.Applet;
import java.awt.Image;

import netscape.javascript.JSObject;

import com.pitchplayer.Card;
import com.pitchplayer.Command;
import com.pitchplayer.client.ui.PitchUI;

/**
 * PitchClient.
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
public class DHTMLPitchClient extends Applet implements PitchUI {

	JSObject window;
	private PitchClient client;
	private String username;

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
		username = getParameter("username");
		client = new PitchClient(this, server, port, sessionId, username);

		// talk to HTML page
		window = JSObject.getWindow(this);
		System.err.println("got HTML window");

		client.authenticate();
		client.startGameListUpdates();

		super.init();
	}

	/**
	 * Send list of current games to browser
	 * 
	 * @param gameList
	 *            the list of games received from the server
	 */
	public void notifyDisplayGameList(String[] gameList) {
		StringBuffer infoString = new StringBuffer();
		for (int i = 0; i < gameList.length; i++) {
			infoString.append(gameList[i]);
		}
		System.err.println("sending gameList to JS: " + infoString.toString());
		//	String[] g = {gameList};
		try {
			//	    window.call("takeInfo", g);
			jsEval("takeInfo", infoString.toString());
		} catch (Exception e) {
			System.err.println("Error calling takeInfo: " + e.getMessage());
		}
	}

	/**
	 * Receive message from server whenever a game join is successful.
	 * 
	 * This is bullshit. Can't pass a fucking array to JS and then access it in
	 * JS as an array??? Now I've got to StringTokenize it on the JS end.
	 */
	public void notifyJoinOk(String[] playerNames) {
		String playerNamesString = "";
		for (int i = 0; i < playerNames.length; i++) {
			if (i > 0)
				playerNamesString += ";";
			playerNamesString += playerNames[i];
		}
		//	String[] dogshitArray = { playerNamesString };
		//	window.call("joinedOk", dogshitArray);
		jsEval("joinedOk", playerNamesString);
	}

	/**
	 * Notify browser that join was unsuccesful
	 */
	public void notifyJoinFailed() {
		window.call("joinFailed", null);
	}

	/**
	 * Notify browser that a player was added to the game
	 */
	public void notifyPlayerAdded(String playerName) {
		//	String[] playerNameArray = { playerName };
		//	window.call("playerAdded", playerNameArray);
		jsEval("playerAdded", playerName);
		System.err.println("playerAddded!");
	}

	/**
	 * Notify browser of a chat message
	 */
	public void notifyChatMessage(String msg) {
		//	String[] args = {msg};
		//	window.call("showPlayerMessage", args);
		jsEval("showPlayerMessage", msg);
	}

	/**
	 * Notify browser of a server message
	 */
	public void notifyServerMessage(String msg) {
		//	String[] args = {msg};
		//	window.call("showServerMessage", args);
		jsEval("showServerMessage", msg);
	}

	/**
	 * Notify the browser that a card was played by another player.
	 */
	public void notifyCardPlayed(int playerIndex, ClientCard card) {
		// display the card in the UI
		//	String[] args = {playerIndex+"|"+card.toString()};
		jsEval("cardPlayed", playerIndex + "|" + card.toString());
	}

	/**
	 * Called to notify this client that it is the user's turn to bid.
	 */
	public void notifyMyBid(String bidStr) {
		//	String[] bidStrArray = { bidStr };
		//	window.call("getBid", bidStrArray);
		jsEval("getBid", bidStr);
	}

	/**
	 * Dispay new hand
	 */
	public void notifyTakeHand(String strHand) {
		jsEval("takeHandJS", strHand);
	}

	/**
	 * Get the player's name
	 */
	public String getName() {
		return username;
	}

	/**
	 * Notify the browser that it's this player's turn
	 */
	public void notifyMyTurn(boolean isMyTurn) {
		window.call("myTurn", null);
	}

	/**
	 * Notify the browser to display all scores.
	 */
	public void notifyDisplayScores(String scoreString) {
		String[] args = { scoreString };
		window.call("displayScores", args);
	}

	/**
	 *  
	 */
	public void notifyGameAborted(String quitterName) {
		window.call("gameOver", null);
	}

	/**
	 *  
	 */
	public void notifyGameOver(String winnerName) {
		window.call("gameOver", null);
	}


	public void jsEval(String method) {
		window.eval(method + "();");
	}

	public void jsEval(String method, String arg) {
		window.eval(method + "(\"" + arg + "\");");
	}

	public void jsEval(String method, String[] args) {
		StringBuffer sb = new StringBuffer(method);
		sb.append("(");
		for (int i = 0; i < args.length; i++) {
			sb.append("\"");
			sb.append(args[i]);
			sb.append("\"");
			if (i < args.length - 1) {
				sb.append(",");
			}
		}
		window.eval(sb.toString());
	}

	public void notifyDisplayGameList(String[][] gameInfo) {
		// TODO Auto-generated method stub
		
	}

	public void notifyJoinOk(int gameType, String[] playerNames) {
		String playerNamesString = "";
		for (int i = 0; i < playerNames.length; i++) {
			if (i > 0)
				playerNamesString += ";";
			playerNamesString += playerNames[i];
		}
		//	String[] dogshitArray = { playerNamesString };
		//	window.call("joinedOk", dogshitArray);
		jsEval("joinedOk", playerNamesString);
	}

	public void notifyWinningBid(int playerIndex, int bidAmt) {
		// TODO Auto-generated method stub
		
	}

	public void notifyTrickWon(int playerIndex, Card card) {
		jsEval("trickWon", new String[] {String.valueOf(playerIndex), card.toString()});
	}

	public void playSound(String soundFile) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Get an image relative to the applet's codebase.
	 */
	public Image getImage(String s) {
		return getImage(getCodeBase(), s);
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