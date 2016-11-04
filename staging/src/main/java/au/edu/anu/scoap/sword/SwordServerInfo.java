package au.edu.anu.scoap.sword;

import org.swordapp.client.AuthCredentials;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class SwordServerInfo {
	
	private String serviceDocUrl;
	private String username;
	private String password;
	private String onBehalfOf;
	
	public SwordServerInfo(String serviceDocUrl, String username, String password) {
		this(serviceDocUrl, username, password, null);
	}

	public SwordServerInfo(String serviceDocUrl, String username, String password, String onBehalfOf) {
		this.serviceDocUrl = serviceDocUrl;
		this.username = username;
		this.password = password;
		this.onBehalfOf = onBehalfOf;
	}

	public String getServiceDocUrl() {
		return serviceDocUrl;
	}

	public AuthCredentials createAuth() {
		AuthCredentials credentials;
		if (onBehalfOf != null && onBehalfOf.length() > 0) {
			credentials = new AuthCredentials(username, password, onBehalfOf);
		} else {
			credentials = new AuthCredentials(username, password);
		}
		return credentials;
	}
	
	public AuthCredentials createNoOnBehalfAuth() {
		return new AuthCredentials(username, password);
	}
}