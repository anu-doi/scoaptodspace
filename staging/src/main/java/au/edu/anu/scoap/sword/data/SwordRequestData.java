/**
 * 
 */
package au.edu.anu.scoap.sword.data;

import java.util.Map;
import java.util.Set;

import au.edu.anu.scoap.sword.utils.SwordURIConstants;

/**
 * @author Rahul Khanna
 *
 */
public class SwordRequestData {
	
	private final String collectionName;
	private final Map<String, Set<String>> metadata;
	private String editMediaLink;
	private String editLink;
	private final Set<BitstreamInfo> bitstreams;
	private final boolean inProgress;
	
	public SwordRequestData(String collectionName, Map<String, Set<String>> metadata,
			Set<BitstreamInfo> bitstreams, boolean inProgress) {
		super();
		this.collectionName = collectionName;
		this.metadata = metadata;
		this.editMediaLink = null;
		this.editLink = null;
		this.bitstreams = bitstreams;
		this.inProgress = inProgress;
	}
	
	public SwordRequestData(Integer itemId, Map<String, Set<String>> metadata, Set<BitstreamInfo> bitstreams, boolean inProgress) {
		super();
		this.collectionName = null;
		this.metadata = metadata;
		if (itemId != null) {
			this.editMediaLink = SwordURIConstants.EDIT_MEDIA_PREFIX + itemId.toString();
			this.editLink = SwordURIConstants.EDIT_PREFIX + itemId.toString();
		}
		else {
			this.editMediaLink = null;
			this.editLink = null;
		}
		this.bitstreams = bitstreams;
		this.inProgress = inProgress;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Map<String, Set<String>> getMetadata() {
		return metadata;
	}

	public String getEditMediaLink() {
		return editMediaLink;
	}
	
	public void setEditMediaLink(String editMediaLink) {
		this.editMediaLink = editMediaLink;
	}
	
	public String getEditLink() {
		return editLink;
	}
	
	public void setEditLink(String editLink) {
		this.editLink = editLink;
	}

	public Set<BitstreamInfo> getBitstreams() {
		return bitstreams;
	}
	
	public boolean isInProgress() {
		return inProgress;
	}
	
}
