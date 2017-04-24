package com.pitchplayer.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.pitchplayer.server.PitchServer;

public class AdminServlet extends BaseServlet {

	public static final String CMD_RESTART = "restart";

	public static final String CMD_SHOW_USAGE = "usage";

	public static final String restartPage = "/_admin.jsp";

	private PitchServer pitchServer;

	/**
	 * Does nothing for now.
	 */
	public void init(ServletConfig config) throws ServletException {
		WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
		pitchServer = (PitchServer)ctx.getBean("pitchServer");
		super.init(config);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String cmd = req.getParameter("cmd");
		if (cmd == null) {
			// ?
		} else if (cmd.equals(CMD_RESTART)) {
			restartServer(req, res);
		} else if (cmd.equals(CMD_SHOW_USAGE)) {
			showUsage(req, res);
		}
	}

	/**
	 * Restart the pitch server
	 */
	public void restartServer(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {
		pitchServer.restart();
		showSuccess(req, res, restartPage);
	}

	/**
	 */
	public void showUsage(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
	}
}