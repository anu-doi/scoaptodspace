package au.edu.anu.scoap.sword.utils;

import au.edu.anu.scoap.util.ScoapConfiguration;

/**
 * Constant class to retrieve appropriate URLs and values
 * 
 * @author Genevieve Turner
 *
 */
public class SwordURIConstants {
	/**
	 * The prefix of the edit url
	 */
	public static final String EDIT_PREFIX = ScoapConfiguration.getProperty("staging", "sword.server") + "/edit/";
	
	/**
	 * The prefix of the edit media url
	 */
	public static final String EDIT_MEDIA_PREFIX = ScoapConfiguration.getProperty("staging", "sword.server") + "/edit-media/";
	
	/**
	 * The service document url
	 */
	public static final String SERVICE_DOCUMENT = ScoapConfiguration.getProperty("staging", "sword.server") + "/servicedocument";
	
	/**
	 * The prefix of the collection url
	 */
	public static final String COLLECTION_PREFIX = ScoapConfiguration.getProperty("staging", "sword.server") + "/collection/";
	
	/**
	 * The collection
	 */
	public static final String DEFAULT_COLLECTION = COLLECTION_PREFIX + ScoapConfiguration.getProperty("staging", "sword.default.collection");
}
