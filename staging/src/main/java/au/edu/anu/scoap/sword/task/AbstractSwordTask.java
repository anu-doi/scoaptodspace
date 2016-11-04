package au.edu.anu.scoap.sword.task;

import java.util.concurrent.Callable;

import org.swordapp.client.AuthCredentials;
import org.swordapp.client.SWORDClient;

import au.edu.anu.scoap.sword.SwordServerInfo;

/**
 * 
 * @author Rahulk Hanna
 *
 * @param <T>
 */
public abstract class AbstractSwordTask<T> implements Callable<T> {
	protected SwordServerInfo serverInfo;
	protected SWORDClient client;

	protected AbstractSwordTask(SWORDClient swordClient, SwordServerInfo serverInfo) {
		this.client = swordClient;
		this.serverInfo = serverInfo;
	}

	protected AuthCredentials createAuth(boolean includeOnBehalfOf) {
		AuthCredentials auth;
		if (includeOnBehalfOf) {
			auth = serverInfo.createAuth();
		} else {
			auth = serverInfo.createNoOnBehalfAuth();
		}
		return auth;
	}
}
