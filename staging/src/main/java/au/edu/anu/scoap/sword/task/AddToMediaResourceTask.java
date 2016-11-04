package au.edu.anu.scoap.sword.task;

import org.swordapp.client.Deposit;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SwordResponse;

import au.edu.anu.scoap.sword.SwordServerInfo;

/**
 * 
 * @author Rahul Khanna
 * 
 */
public class AddToMediaResourceTask extends AbstractSwordTask<SwordResponse> {
	private String editMediaUrl;
	private Deposit deposit;

	public AddToMediaResourceTask(SWORDClient swordClient, SwordServerInfo serverInfo, String editMediaUrl,
			Deposit deposit) {
		super(swordClient, serverInfo);
		this.editMediaUrl = editMediaUrl;
		this.deposit = deposit;
	}

	public SwordResponse call() throws Exception {
		SwordResponse sr = this.client.addToMediaResource(this.editMediaUrl, this.deposit, createAuth(true));
		return sr;
	}
}
