package com.opera.link.webapp;

import java.io.IOException;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opera.link.apilib.LinkClient;
import com.opera.link.apilib.exceptions.LibOperaLinkException;
import com.opera.link.apilib.exceptions.LinkAccessDeniedException;
import com.opera.link.apilib.exceptions.LinkItemNotFound;
import com.opera.link.apilib.exceptions.LinkResponseFormatException;

/**
 * LinkServlet is a controller servlet that performs the OAuth authentication
 * and authorization and keeps track of the authentication state
 * 
 * @author michaell
 * 
 */
public class LinkServlet extends HttpServlet {
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LinkServlet() {
		super();
		props = new Properties();
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream("opera.properties");

		try {
			props.load(inputStream);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed to load opera.properties file"
					+ "Please make sure that opera.properties is on the "
					+ "classpath");
		}
	}

	/**
	 * 
	 * Controller for OAuth and Opera Link API example
	 * 
	 * Oauth in a nutshell
	 * 
	 * State 1. There are no auth tokens associated with the incoming request,
	 * so we need to fetch request tokens and stash them in the session.
	 * 
	 * State 2. We have request tokens and the request is a callback from the
	 * OAuth server containing an OAuth verifier which we need to use to request
	 * access tokens, which we stash in the session
	 * 
	 * State 3. We have access tokens and can re-direct to the appropriate view.
	 * If the view determines that the access tokens are invalid, it will
	 * invalidate the session and se start again.
	 * 
	 * By passing an http parameter in the request called reset, with any value
	 * set, the entire session and all data will be cleared.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// get the stored data model from the session if there is one
		LinkModel model = (LinkModel) request.getSession().getAttribute(
				LinkModel.MODEL);

		// Reset the session and model if this parameter is passed
		if (!(request.getParameter("reset") == null)) {
			resetSession(request, model);
		}

		// Initialise the model for the session if it does not exist
		if (model == null) {
			model = initModel(request);
		}

		// Process OAuth authentication and get the user's speeddials
		if (model.hasAccessTokenAndSecret()) {
			link = LinkClient.createFromAccessToken(model.getConsumerKey(),
					model.getConsumerKeySecret(), model.getAccessToken(), model
							.getTokenSecret());

			// if view has explicitly asked us to update the Speed Dial data
			if (!(request.getParameter("update") == null)) {
				updateSpeedDials(request, response, model);
			}

			// send user to view
			response.sendRedirect(INDEX_JSP);

		} else if (model.hasRequestToken()) {
			link = LinkClient.createFromRequestToken(model.getConsumerKey(),
					model.getConsumerKeySecret(), model.getRequestToken(),
					model.getTokenSecret());

			// this is the callback from the oauth server
			if (request.getParameter(OAUTH_VERIFIER) != null) {
				if (verifyConnection(request.getParameter(OAUTH_VERIFIER))) {
					// keep hold of the token and secret
					storeAccessTokenAndSecret(request, model);

					// we have access, so update the speed dials and send
					// user to view
					updateSpeedDials(request, response, model);
					response.sendRedirect(INDEX_JSP);
				} else {
					request.getSession().setAttribute(LinkModel.ERROR,
							"Failed to Verify OAuth connection");
					response.sendRedirect(ERROR_JSP);
				}
			} else {
				// we have the request token but never verified the connection
				// so we need to abort and restart the OAuth process
				resetSession(request, model);
				response.sendRedirect(AUTH_SERVLET);
			}

		} else {
			link = new LinkClient(model.getConsumerKey(), model
					.getConsumerKeySecret());

			try {
				// get Request token and secret
				String authUrl = link.getAuthorizationURL(props
						.getProperty(OAUTH_CALLBACK_URL_PROPERTY));

				// keep the request token and secret
				storeRequestTokenAndSecret(request, model);

				// redirect to the auth login page
				response.sendRedirect(authUrl);
			} catch (LibOperaLinkException e) {
				LOGGER.log(Level.WARNING, "LibOperaLinkException: ", e);
				request
						.getSession()
						.setAttribute(
								LinkModel.ERROR,
								"Failed to get OAuth request token and"
										+ "secret. Have you set the correct consumerKey and conusmerKeysecret in the"
										+ "properties file?");
				response.sendRedirect(ERROR_JSP);
			}
		}
	}

	/**
	 * Stores the request token and secret into the session
	 * 
	 * @param request
	 * @param model
	 */
	private void storeRequestTokenAndSecret(HttpServletRequest request,
			LinkModel model) {
		model.setRequestToken(link.getRequestToken());
		model.setTokenSecret(link.getTokenSecret());
		request.getSession().setAttribute(LinkModel.MODEL, model);
	}

	/**
	 * Stores the access token and secret into the session
	 * 
	 * @param request
	 * @param model
	 */
	private void storeAccessTokenAndSecret(HttpServletRequest request,
			LinkModel model) {
		model.setAccessToken(link.getAccessToken());
		model.setTokenSecret(link.getTokenSecret());
		request.getSession().setAttribute(LinkModel.MODEL, model);
	}

	/**
	 * Makes a request to the Link server for SpeedDial data and stores the
	 * updated SpeedDials in the session.
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws IOException
	 */
	private void updateSpeedDials(HttpServletRequest request,
			HttpServletResponse response, LinkModel model) throws IOException {
		try {
			ArrayList<com.opera.link.apilib.items.SpeedDial> sd = link
					.getSpeedDials();

			/*
			 * The SpeedDials returned from the Link client are unfortunatley
			 * not serializable. We'll grab the data that we want and stuff it
			 * into a simple bean that we can store in the session.
			 */

			ArrayList<com.opera.link.webapp.SpeedDial> serializableSd = new ArrayList<com.opera.link.webapp.SpeedDial>();
			for (com.opera.link.apilib.items.SpeedDial sditem : sd)
				serializableSd.add(new com.opera.link.webapp.SpeedDial(
						sditem.title, sditem.uri, sditem.thumbnail, Integer.valueOf(sditem.getId())));

			model.setSpeeddials(serializableSd);
			request.getSession().setAttribute(LinkModel.MODEL, model);
		} catch (LinkItemNotFound e) {
			request.getSession().setAttribute(LinkModel.ERROR,
					"User has no speedials");
			response.sendRedirect(ERROR_JSP);
		} catch (LinkAccessDeniedException e) {
			// access token may have expired
			response.sendRedirect(AUTH_SERVLET_RESET);
			e.printStackTrace();
		} catch (LinkResponseFormatException e) {
			request.getSession().setAttribute(LinkModel.ERROR,
					"The Link Server sent badly formatted data");
			response.sendRedirect(ERROR_JSP);
		} catch (LibOperaLinkException e) {
			request.getSession().setAttribute(LinkModel.ERROR,
					"The Link Server failed to process the request");
			response.sendRedirect(ERROR_JSP);
		}
	}

	/**
	 * Clears the current session and model
	 * 
	 * @param request
	 * @param model
	 */
	private void resetSession(HttpServletRequest request, LinkModel model) {
		request.getSession().invalidate();
		if (model != null) {
			model.reset();
		}
	}

	/**
	 * Initialises the data model that we will keep in the session
	 * 
	 * @param request
	 * @param model
	 */
	private LinkModel initModel(HttpServletRequest request) {
		LinkModel lmodel = new LinkModel();

		lmodel
				.setConsumerKey((String) props
						.getProperty(CONSUMER_KEY_PROPERTY));
		lmodel.setConsumerKeySecret((String) props
				.getProperty(CONSUMER_SECRET_PROPERTY));

		return lmodel;
	}

	/**
	 * Uses the verifier returned from the OAuth server to verify the session
	 * 
	 * @param verifier
	 * @return
	 */
	private boolean verifyConnection(String verifier) {
		try {
			link.grantAccess(verifier);
			return true;
		} catch (LibOperaLinkException e) {
			LOGGER.log(Level.SEVERE,
					"Granting Access to OAuth connection FAILED ");
			e.printStackTrace();
			return false;
		}
	}

	private static final long serialVersionUID = 100L;
	private final static Logger LOGGER = Logger.getLogger(LinkServlet.class
			.getName());

	// application paths
	private static final String ROOT = "/Link/";
	public static final String INDEX_JSP = ROOT + "index.jsp";
	public static final String ERROR_JSP = ROOT + "error.jsp";
	public static final String AUTH_SERVLET_RESET = ROOT
			+ "LinkServlet?reset=1";
	public static final String AUTH_SERVLET_UPDATE = ROOT
			+ "LinkServlet?update=1";
	public static final String AUTH_SERVLET = ROOT + "LinkServlet";
	private static final String OAUTH_VERIFIER = "oauth_verifier";

	// keys to the properties file
	private static final String CONSUMER_KEY_PROPERTY = "opera.consumerKey";
	private static final String CONSUMER_SECRET_PROPERTY = "opera.consumerSecret";
	private static final String OAUTH_CALLBACK_URL_PROPERTY = "opera.callbackUrl";

	private Properties props = null;
	private LinkClient link;
}