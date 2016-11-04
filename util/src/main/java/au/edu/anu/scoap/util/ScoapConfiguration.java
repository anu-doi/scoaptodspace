package au.edu.anu.scoap.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to load configuration files
 * 
 * @author Genevieve Turner
 */
public class ScoapConfiguration {
	static final Logger LOGGER = LoggerFactory.getLogger(ScoapConfiguration.class);
	
	private static Map<String, Properties> propertiesMap = new HashMap<String, Properties>();
	
	/**
	 * Get the properties for the requested group
	 * 
	 * @param group The group name
	 * @return The properties
	 */
	private static Properties getProperties(String group) {
		Properties properties = propertiesMap.get(group);
		if (properties != null) {
			return properties;
		}
		String propertyFilename = group + ".properties";
		try {
			InputStream is = null;
			if (propertyFilename != null && propertyFilename.length() > 0) {
				is = ScoapConfiguration.class.getClassLoader().getResourceAsStream(propertyFilename);
			}
			else {
				is = ScoapConfiguration.class.getClassLoader().getResourceAsStream("config.properties");
			}
			properties = new Properties();
			properties.load(is);
			propertiesMap.put(group, properties);
		}
		catch (IOException e) {
			LOGGER.error("Error reading property file", e);
		}

		return properties;
	}
	
	/**
	 * Get the the property
	 * 
	 * @param group The group name
	 * @param name The name of the property
	 * @return The property value
	 */
	public static String getProperty(String group, String name) {
		Properties properties = getProperties(group);
		if (properties != null) {
			return properties.getProperty(name);
		}
		return null;
	}
}
