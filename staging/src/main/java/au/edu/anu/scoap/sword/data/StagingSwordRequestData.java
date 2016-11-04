package au.edu.anu.scoap.sword.data;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class StagingSwordRequestData implements SwordRequestDataProvider {
	protected List<SwordRequestData> swordRequests = new ArrayList<SwordRequestData>();

	public void setSwordRequests(List<SwordRequestData> swordRequests) {
		this.swordRequests = swordRequests;
	}
	
	@Override
	public List<SwordRequestData> getSwordRequests() {
		return swordRequests;
	}

	@Override
	public void updateRequestStatus(SwordRequestData data, boolean isSuccess) {
		// Do nothing
	}

}
