package com.opera.link.webapp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Helper class to manage session data
 * 
 * @author michaell
 * 
 */
public class LinkModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -999214406465034908L;
	
	public String getRequestToken() {
		return requestToken;
	}

	public void setRequestToken(String requestToken) {
		this.requestToken = requestToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public void reset() {
		this.accessToken = null;
		this.tokenSecret = null;
		this.requestToken = null;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerKeySecret() {
		return consumerKeySecret;
	}

	public void setConsumerKeySecret(String consumerKeySecret) {
		this.consumerKeySecret = consumerKeySecret;
	}
	
	public boolean hasAccessTokenAndSecret() {
		if (!(this.accessToken == null) && !(this.tokenSecret == null)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasRequestToken() {
		if ( (this.requestToken != null) && !hasAccessTokenAndSecret()) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setSpeeddials(ArrayList<SpeedDial> sd) {
		this.speeddials = sd;
	}
	
	public ArrayList<SpeedDial> getSpeeddials() {
		return this.speeddials;
	}

	private ArrayList<com.opera.link.webapp.SpeedDial> speeddials;
	private String consumerKey;
	private String consumerKeySecret;
	private String accessToken = null;
	private String tokenSecret = null;
	private String requestToken = null;
	
	public static final String ERROR = "ERROR";
	public static final String MODEL = "MODEL";
}
