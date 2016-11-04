package au.edu.anu.scoap.sword.task;

import org.swordapp.client.Deposit;
import org.swordapp.client.DepositReceipt;
import org.swordapp.client.SWORDClient;

import au.edu.anu.scoap.sword.SwordServerInfo;

/**
 * Task to add metadata to an item in DSpace via sword
 * 
 * @author Genevieve Turner
 *
 */
public class AddToContainerTask extends AbstractSwordTask<DepositReceipt> {
	private String editUrl;
	private Deposit deposit;
	
	/**
	 * Constructor
	 * 
	 * @param swordClient The sword client
	 * @param serverInfo The server information
	 * @param editUrl The edit url
	 * @param deposit The deposit to make
	 */
	public AddToContainerTask(SWORDClient swordClient, SwordServerInfo serverInfo, String editUrl, Deposit deposit) {
		super(swordClient, serverInfo);
		this.editUrl = editUrl;
		this.deposit = deposit;
	}

	@Override
	public DepositReceipt call() throws Exception {
		return this.client.addToContainer(editUrl, deposit, createAuth(true));
	}
	
}
