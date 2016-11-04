package au.edu.anu.scoap.sword.data;

import java.util.List;

/**
 * 
 * @author Rahul Khanna
 *
 */
public interface SwordRequestDataProvider {

	public List<SwordRequestData> getSwordRequests();
	
	public void updateRequestStatus(SwordRequestData data, boolean isSuccess);
}
