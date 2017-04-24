package com.pitchplayer.servlet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.pitchplayer.Card;
import com.pitchplayer.Command;
import com.pitchplayer.server.PitchServer;
import com.pitchplayer.server.game.player.PollingPlayer;

public class PitchServlet extends BaseServlet {

	public static final String CMD_LOGIN = "l";

	public static final String CMD_GAME_LIST = "i";

	public static final String CMD_JOIN_GAME = "j";

	public static final String CMD_PLAY_CARD = "p";

	public static final String CMD_BID = "b";

	public static final String CMD_QUIT = "q";

	public static final String CMD_CREATE_GAME = "c";

	public static final String CMD_ADD_PLAYER = "a";

	public static final String CMD_START_GAME = "s";

	public static final String CMD_POLL = "o";

	protected static final String PLAYER_ATTR = "player";

	protected static final String NOT_LOGGED_IN_ERR = "not logged in";

	protected Logger log = Logger.getLogger(this.getClass().getName());
	private PitchServer pitchServer;

	/**
	 * Initialize the servlet with configuration information from the web.xml
	 * file.
	 */
	public void init(ServletConfig config) throws ServletException {
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		pitchServer = (PitchServer)ctx.getBean("pitchServer");
		super.init(config);
		log.debug("initialized");
	}

	/**
	 * Handle GET requests. Calls doPost.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	/**
	 * Handle POST requests.
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String cmd = req.getParameter("cmd");
		if (cmd == null) {
			// ?
			return;
		}
		if (cmd.equals(CMD_LOGIN)) {
			handleLogin(req, res);
		} else if (cmd.equals(CMD_GAME_LIST)) {
			handleGameList(req, res);
		} else if (cmd.equals(CMD_JOIN_GAME)) {
			handleJoinGame(req, res);
		} else if (cmd.equals(CMD_PLAY_CARD)) {
			handlePlayCard(req, res);
		} else if (cmd.equals(CMD_BID)) {
			handleBid(req, res);
		} else if (cmd.equals(CMD_QUIT)) {
			handleQuit(req, res);
		} else if (cmd.equals(CMD_CREATE_GAME)) {
			handleCreateGame(req, res);
		} else if (cmd.equals(CMD_START_GAME)) {
			handleStartGame(req, res);
		} else if (cmd.equals(CMD_ADD_PLAYER)) {
			handleAddPlayer(req, res);
		} else if (cmd.equals(CMD_POLL)) {
			handlePoll(req, res);
		}
	}

	/**
	 * Handle a LOGIN request. If authentication was successful, writes the
	 * session ID back to the client in the body of the HTTP response (as well
	 * as in a cookie named JSESSIONID).
	 */
	protected void handleLogin(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String username = req.getParameter("u");
		String password = req.getParameter("p");
		PollingPlayer player = PollingPlayer.loginUser(username, password, req
				.getRemoteAddr(), pitchServer);
		if (player != null) {
			setPlayer(req, player);
			writeResponse(res, req.getSession().getId());
		} else {
			writeResponse(res, "false");
		}
	}

	/**
	 * Handle a GAME_LIST request. Structure of the byte array written to the
	 * client is as follows: [int:# of games][long:game 1 id][int:game
	 * type][int:status][int:# of players][int:p1 name length][p1 name][int:p2
	 * name length][p2 name]... Where int values are 4-bytes and long values are
	 * 8-bytes.
	 */
	public void handleGameList(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		byte[] gameList = pitchServer.listGamesAsBytes();
		writeResponse(res, gameList);
	}

	/**
	 * Handle a JOIN_GAME request. Writes a 1 (join succeeded) or 0 (join
	 * failed) back to the client.
	 */
	protected void handleJoinGame(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		int gameNum;
		try {
			gameNum = Integer.parseInt(req.getParameter("n"));
		} catch (NumberFormatException e) {
			writeErrorResponse(res, HttpServletResponse.SC_BAD_REQUEST,
					"Invalid game number");
			return;
		}

		PollingPlayer player = getPlayer(req);
		if (player == null) {
			writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
					NOT_LOGGED_IN_ERR);
			return;
		} else {
			try {
				writeResponse(res, player.joinGame(gameNum));
			} catch (Exception e) {
				writeErrorResponse(res, e);
			}
		}
	}

	/**
	 * Handle a PLAY_CARD request.
	 */
	protected void handlePlayCard(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		PollingPlayer player = getPlayer(req);
		if (player == null) {
			writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
					NOT_LOGGED_IN_ERR);
			return;
		}
		player.playCard(new Card(req.getParameter("c")));
		writeResponse(res, "OK");
	}

	/**
	 * Handle a BID request.
	 */
	protected void handleBid(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PollingPlayer player = getPlayer(req);
		if (player == null) {
			writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
					NOT_LOGGED_IN_ERR);
			return;
		}
		player.bid(Integer.parseInt(req.getParameter("b")));
		writeResponse(res, "OK");
	}

	/**
	 * Handle a QUIT request.
	 */
	protected void handleQuit(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PollingPlayer player = getPlayer(req);
		if (player == null) {
			writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
					NOT_LOGGED_IN_ERR);
			return;
		}
		player.quitGame();
		writeResponse(res, "OK");
	}

	/**
	 * Handle a START request
	 */
	protected void handleStartGame(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		PollingPlayer player = getPlayer(req);
		if (player == null) {
			writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
					NOT_LOGGED_IN_ERR);
			return;
		}
		player.startGame();
		writeResponse(res, "OK");
	}

	/**
	 * Handle an ADD request
	 */
	protected void handleAddPlayer(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		PollingPlayer player = getPlayer(req);
		if (player == null) {
			writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
					NOT_LOGGED_IN_ERR);
			return;
		}
		player.addCPUPlayer();
		writeResponse(res, "OK");
	}

	/**
	 * Handle a CREATE_GAME request. Parameter 't' indicates the type of game
	 * request: 1=singles;2=doubles
	 */
	protected void handleCreateGame(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		PollingPlayer player = getPlayer(req);
		if (player == null) {
			writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
					NOT_LOGGED_IN_ERR);
			return;
		}
		try {
			int gameType = Integer.parseInt(req.getParameter("t"));
			writeResponse(res, player.createGame(gameType));
		} catch (NumberFormatException e) {
			writeErrorResponse(res, HttpServletResponse.SC_BAD_REQUEST,
					"Invalid game type");
		} catch (Exception e) {
			writeErrorResponse(res, e);
		}
	}

	/**
	 * Handle a POLL request. Format of data sent back to client is as follows:
	 * [int: # of commands][int: # of elements in command (1 for command name + #
	 * of args][int:length of command name][str: command name][int: length of
	 * arg][str: arg value]...
	 */
	protected void handlePoll(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PollingPlayer player = getPlayer(req);
		if (player == null) {
			writeErrorResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
					NOT_LOGGED_IN_ERR);
			return;
		}
		byte[] bytes = null;
		Vector commands = player.getCommands();
		try {
			if (commands != null) {
				DataOutputStream buffer = null;
				ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
				buffer = new DataOutputStream(byteBuffer);
				// WRITE: # of commands
				buffer.writeInt(commands.size());
				for (int i = 0; i < commands.size(); i++) {
					Command cmd = (Command) commands.elementAt(i);
					String[] args = cmd.getArgs();
					// WRITE: # of total elements getting written (# of args + 1
					// for command[i])
					if (args == null)
						buffer.writeInt(1);
					else
						buffer.writeInt(args.length + 1);
					// WRITE: length of command[i] name (first element)
					buffer.writeInt(cmd.getCommand().length());
					// WRITE: command[i] name
					buffer.writeBytes(cmd.getCommand());
					if (args != null) {
						for (int j = 0; j < args.length; j++) {
							// WRITE: length of argument[j]
							buffer.writeInt(args[j].length());
							// WRITE: argument[j]
							buffer.writeBytes(args[j]);
						}
					}
				}
				bytes = byteBuffer.toByteArray();
				buffer.close();
			} else {
				bytes = new byte[0];
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		res.setContentType("text/xml");
		res.setContentLength(bytes.length);
		ServletOutputStream out = null;
		try {
			out = res.getOutputStream();
			out.write(bytes);
			out.flush();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignore) {
				}
				out = null;
			}
		}
	}

	/**
	 * Write a response back to the client
	 */
	protected void writeResponse(HttpServletResponse res, String response)
			throws IOException {
		res.setContentType("text/xml");
		res.setContentLength(response.length());
		PrintWriter out = null;
		try {
			out = res.getWriter();
			out.print(response);
			out.flush();
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
		}
	}

	/**
	 * Write a response back to the client
	 */
	protected void writeResponse(HttpServletResponse res, byte[] response)
			throws IOException {
		res.setContentType("text/xml");
		res.setContentLength(response.length);
		ServletOutputStream out = null;
		try {
			out = res.getOutputStream();
			out.write(response);
			out.flush();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignore) {
				}
				out = null;
			}
		}
	}

	/**
	 * Write a response back to the client
	 */
	protected void writeResponse(HttpServletResponse res, boolean response)
			throws IOException {
		if (response) {
			writeResponse(res, 1);
		} else {
			writeResponse(res, 0);
		}
	}

	/**
	 * Write a response back to the client
	 */
	protected void writeResponse(HttpServletResponse res, int response)
			throws IOException {
		res.setContentType("text/xml");
		res.setContentLength(4);
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(res.getOutputStream());
			out.writeInt(response);
			out.flush();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignore) {
				}
				out = null;
			}
		}
	}

	/**
	 * Write a response back to the client
	 */
	protected void writeErrorResponse(HttpServletResponse res,
			int responseCode, String message) throws IOException {
		res.setContentType("text/xml");
		res.setContentLength(message.length());
		res.sendError(responseCode, message);
	}

	/**
	 * Write a response back to the client
	 */
	protected void writeErrorResponse(HttpServletResponse res, Exception e)
			throws IOException {
		writeErrorResponse(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
				.getMessage());
	}

	/**
	 * Put a PollingPlayer in the session associated with the request
	 */
	protected static void setPlayer(HttpServletRequest req, PollingPlayer player) {
		req.getSession().setAttribute(PLAYER_ATTR, player);
	}

	/**
	 * Get a PollingPlayer object from the session associated with the request.
	 */
	protected static PollingPlayer getPlayer(HttpServletRequest req) {
		Object playerObj = req.getSession().getAttribute(PLAYER_ATTR);
		if (playerObj != null) {
			return (PollingPlayer) playerObj;
		} else
			return null;
	}

}