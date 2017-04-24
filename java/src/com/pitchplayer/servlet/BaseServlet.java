package com.pitchplayer.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.pitchplayer.common.validator.ValidationException;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.util.UserCookieUtil;

/**
 * Base servlet class.
 */
public class BaseServlet extends HttpServlet {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	public static final String REQ_ERRORS_ATTR = "errors";

	public static final String REQ_SUCCESS_ATTR = "success";

	public static final String SESSION_USER_ATTR = "user";

	//    public static final String JSP_ERROR_ATTR =
	// "javax.servlet.jsp.jspException";

	public static final String SYS_ERROR_MSG = "System error.  Please try again later.";

	public static final String ERROR_URI_PARAM = "errorUrl";

	public static final String SUCCESS_URI_PARAM = "successUrl";

	public String getServletName() {
		return this.getClass().getName();
	}

	public String getBaseUrl(HttpServletRequest req) {
		return getNonsecureBaseUrl(req);
	}

	/**
	 * Initialize the servlet. Initializes the logging component and calls
	 * super.init(config)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/**
	 * Get the full path to the root of the web application.
	 */
	protected static String getDocumentBase(ServletContext ctx) {
		String path = ctx.getRealPath("/");
		return path.substring(0, path.lastIndexOf(File.separator));
	}

	/**
	 * Confirms a user is authenticates by setting user object as an attribute
	 * of the session associated with the request.
	 */
	protected void setUser(HttpServletRequest req, 
			       HttpServletResponse res,
			       User user) {
		HttpSession session = req.getSession();
		session.setAttribute("user", user);
		Cookie userCookie = new Cookie("user", UserCookieUtil.toCookieString(user));
		userCookie.setPath("/");
		res.addCookie(userCookie);
	}

	/**
	 * Get the base url of the request
	 */
	public static final String getNonsecureBaseUrl(HttpServletRequest req) {
		String hostname = req.getServerName();
		int port = req.getServerPort();
		return "http://" + hostname
				+ (port == 80 ? "" : ":" + String.valueOf(port));
	}

	/**
	 * Get the base url of the request
	 */
	public static final String getSecureBaseUrl(HttpServletRequest req) {
		String hostname = req.getServerName();
		int port = req.getServerPort();
		return "http://" + hostname + (port == 80 ? "" : String.valueOf(port));
	}

	// legacy
	public void redirectLocal(HttpServletRequest req, HttpServletResponse res,
			String page) {
		try {
			res.sendRedirect(getBaseUrl(req) + page);
		} catch (IOException ioe) {
			log("Error redirecting to: " + page);
		}
	}

	// legacy
	public void redirect(HttpServletResponse res, String url) {
		try {
			res.sendRedirect(url);
		} catch (IOException ioe) {
			log("Error redirecting to: " + url);
		}
	}

	/**
	 * Forward to another uri. (NEW)
	 */
	public void forward(String uri, HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		RequestDispatcher disp = req.getRequestDispatcher(uri);
		disp.forward(req, res);
	}

	/**
	 * generic method to retrieve a parameter from the query string.
	 */
	public String getParameter(HttpServletRequest req, String param)
			throws NullParameterException {
		String str = req.getParameter(param);
		if (str == null || str.equals(""))
			throw new NullParameterException(param);
		return str;
	}

	/**
	 *  
	 */
	public String[] getParameterValues(HttpServletRequest req, String param)
			throws NullParameterException {
		String[] str = req.getParameterValues(param);
		if (str == null)
			throw new NullParameterException(param);
		return str;
	}

	/**
	 * Test whether a string is null or empty.
	 */
	public static boolean isNull(String s) {
		if ((null == s) || s.equals("")) {
			return true;
		} else
			return false;
	}

	/**
	 * Legacy method
	 */
	public static boolean notBlank(String str) {
		return !isNull(str);
	}

	/**
	 * show the error page (legacy)
	 */
	protected void showError(HttpServletResponse res, String errMsg) {
		try {
			res.setContentType("text/html");
			//      PrintWriter out = res.getWriter();
			ServletOutputStream out = res.getOutputStream();
			out
					.println("<HTML><HEAD><TITLE>Error</TITLE></HEAD><BODY bgcolor=\"#FFFFFF\">");
			out.println("<P><B>Error: </B>" + errMsg);
			out.println("</body></html>");
			out.close();
		} catch (IOException ioe) {
			System.out.println(this.getServletName()
					+ ":showError() - error writing response.");
		}
	}

	// legacy
	protected void writePage(HttpServletResponse res, String page) {
		try {
			res.setContentType("text/html");
			ServletOutputStream out = res.getOutputStream();
			out.println(page);
			out.flush();
			out.close();
		} catch (IOException ioe) {
			System.err.println(this.getServletName()
					+ ": writePage() - error writing response.");
			ioe.printStackTrace();
		}
	}

	// legacy
	public void showStackTrace(HttpServletResponse res, Throwable t) {
		try {
			res.setContentType("text/html");
			PrintWriter out = new PrintWriter(res.getOutputStream());
			out.write("<PRE>");
			t.printStackTrace(out);
			out.write("</PRE>");
			out.flush();
			out.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Forward a request to an error page. If there is a parameter named
	 * 'errorUrl', the request will be forwarded to that URL. Otherwise, the
	 * request is forwarded to whatever is passed in as the defaultErrorPage
	 * param.
	 * 
	 * @param req
	 *            the request
	 * @param res
	 *            the response
	 * @param defaultErrorPage
	 *            the page to redirect to if there is no 'errorUrl' param in the
	 *            request.
	 */
	protected void showError(HttpServletRequest req, HttpServletResponse res,
			String defaultErrorPage) throws ServletException, IOException {
		String errorPage = defaultErrorPage;
		String redirParam = req.getParameter(ERROR_URI_PARAM);
		if (redirParam != null) {
			errorPage = redirParam;
		}
		forward(errorPage, req, res);
	}

	/**
	 * Forward a request to a 'success' page following a form submission. If
	 * there is a parameter named 'successUrl', the request will be forwarded to
	 * that URL. Otherwise, the request is forwarded to whatever is passed in as
	 * the defaultSuccessPage param.
	 * 
	 * @param req
	 *            the request
	 * @param res
	 *            the response
	 * @param defaultSuccessPage
	 *            the page to redirect to if there is no 'successUrl' param in
	 *            the request.
	 */
	protected void showSuccess(HttpServletRequest req, HttpServletResponse res,
			String defaultSuccessPage) throws ServletException, IOException {
		String successPage = defaultSuccessPage;
		req.setAttribute(REQ_SUCCESS_ATTR, "true");
		String redirParam = req.getParameter(SUCCESS_URI_PARAM);
		if (redirParam != null) {
			successPage = redirParam;
		}
		if ( successPage.substring(0,4).equalsIgnoreCase("http")) {
		    redirect(res, successPage);
		}
		else {
		    forward(successPage, req, res);
		}
	}

	// new methods to add errors to req
	/**
	 * Add an error message to the request.
	 */
	protected void addError(HttpServletRequest req, String errorMsg) {
		System.out.println("error reported: " + errorMsg);
		Vector errors = null;
		Object attr = req.getAttribute(REQ_ERRORS_ATTR);
		if (attr != null) {
			errors = (Vector) attr;
		} else {
			errors = new Vector();
		}
		errors.addElement(errorMsg);
		req.setAttribute(REQ_ERRORS_ATTR, errors);
	}

	/**
	 * Adds the message (via getMessage()) of the Throwable tot he request.
	 */
	protected void addError(HttpServletRequest req, Throwable t) {
		System.out.println("Error reported: " + t.getMessage());
		if (!(t instanceof ValidationException)) {
			t.printStackTrace();
		}
		Vector errors = null;
		Object attr = req.getAttribute(REQ_ERRORS_ATTR);
		if (attr != null) {
			errors = (Vector) attr;
		} else {
			errors = new Vector();
		}
		errors.addElement(t.getMessage());
		req.setAttribute(REQ_ERRORS_ATTR, errors);
	}

	/**
	 * Find out whether a request contains errors.
	 */
	protected boolean hasErrors(HttpServletRequest req) {
		Object attr = req.getAttribute(REQ_ERRORS_ATTR);
		return (null != attr);
	}

	// New logging methods
	// Generic logging methods
	public void logError(String msg) {
		log.error(msg);
	}

	public void logDebug(String msg) {
		log.debug(msg);
	}

	public void logInfo(String msg) {
		log.info(msg);
	}

	public void logError(String msg, Throwable t) {
		log.error(msg, t);
	}

	public void logDebug(String msg, Throwable t) {
		log.debug(msg, t);
	}

	public void logInfo(String msg, Throwable t) {
		log.info(msg, t);
	}

	public void logError(Throwable t) {
		log.error(t);
	}

}
