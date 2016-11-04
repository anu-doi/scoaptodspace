package au.edu.anu.scoap.sword.task;

import org.swordapp.client.Deposit;
import org.swordapp.client.DepositReceipt;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SWORDCollection;

import au.edu.anu.scoap.sword.SwordServerInfo;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class DepositTask extends AbstractSwordTask<DepositReceipt> {
	private SWORDCollection collection;
	private String url;
	private Deposit deposit;

	public DepositTask(SWORDClient swordClient, SwordServerInfo serverInfo, String url, Deposit deposit) {
		super(swordClient, serverInfo);
		this.url = url;
		this.deposit = deposit;
	}

	public DepositTask(SWORDClient swordClient, SwordServerInfo serverInfo, SWORDCollection collection, Deposit deposit) {
		super(swordClient, serverInfo);
		this.collection = collection;
		this.deposit = deposit;
	}

	public DepositReceipt call() throws Exception {
		DepositReceipt depositReceipt = null;
		if (this.collection != null) {
			depositReceipt = this.client.deposit(this.collection, this.deposit, createAuth(true));
		} else if (this.url != null) {
			depositReceipt = this.client.deposit(this.url, this.deposit, createAuth(true));
		}
		return depositReceipt;
	}
}
