package au.edu.anu.scoap.sword.task;

import org.swordapp.client.Deposit;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SwordResponse;

import au.edu.anu.scoap.sword.SwordServerInfo;

/**
 * Task to replace an item via sword in DSpace
 * 
 * @author Genevieve Turner
 */
public class ReplaceTask extends AbstractSwordTask<SwordResponse> {
	private String editUrl;
	private Deposit deposit;
	
	/**
	 * Constructor
	 * 
	 * @param swordClient The sword client
	 * @param serverInfo The server information
	 * @param editUrl The edit url
	 * @param deposit The deposit
	 */
	public ReplaceTask(SWORDClient swordClient, SwordServerInfo serverInfo, String editUrl, Deposit deposit) {
		super(swordClient, serverInfo);
		this.editUrl = editUrl;
		this.deposit = deposit;
	}

	@Override
	public SwordResponse call() throws Exception {
		return this.client.replace(editUrl, deposit, createAuth(true));
	}

}
