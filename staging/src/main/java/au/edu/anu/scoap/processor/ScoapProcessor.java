package au.edu.anu.scoap.processor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swordapp.client.ClientConfiguration;
import org.swordapp.client.SWORDClient;

import au.edu.anu.scoap.ScoapQuery;
import au.edu.anu.scoap.dspace.DSpaceObject;
import au.edu.anu.scoap.dspace.QueryDspace;
import au.edu.anu.scoap.dspace.annotation.DSpaceObjectParser;
import au.edu.anu.scoap.sword.SwordProcessor;
import au.edu.anu.scoap.sword.SwordServerInfo;
import au.edu.anu.scoap.sword.data.BitstreamInfo;
import au.edu.anu.scoap.sword.data.FileBitstreamInfo;
import au.edu.anu.scoap.sword.data.StagingSwordRequestData;
import au.edu.anu.scoap.sword.data.SwordRequestData;
import au.edu.anu.scoap.sword.data.SwordRequestDataProvider;
import au.edu.anu.scoap.sword.exception.WorkflowException;
import au.edu.anu.scoap.util.ScoapConfiguration;
import au.edu.anu.scoap.xml.Record;

/**
 * Process Scoap3 records
 * 
 * @author Genevieve Turner
 */
public class ScoapProcessor {
	static final Logger LOGGER = LoggerFactory.getLogger(ScoapProcessor.class);
	
	private String earliestDate;
	private String latestDate;
	private String collectionName;
	
	/**
	 * Constructor 
	 * 
	 * @param collectionName The name of the collection to send records to
	 * @param earliestDate The earliest date to retrieve records from
	 */
	public ScoapProcessor(String collectionName, String earliestDate, String latestDate) {
		this.collectionName = collectionName;
		this.earliestDate = earliestDate;
		this.latestDate = latestDate;
	}
	
	/**
	 * Get the records
	 * 
	 * @return THe records
	 * @throws JAXBException
	 */
	public List<Record> getRecords() throws JAXBException {
		ScoapQuery query = new ScoapQuery();
		List<Record> records = query.getAllRecords(earliestDate, latestDate);
		return records;
	}
	
	/**
	 * Generate a list of sword requests
	 * 
	 * @param records The records
	 * @return The sword data provider
	 */
	public SwordRequestDataProvider generateSwordRequests(List<Record> records) {
		SwordRequestDataProvider dataProvider = new StagingSwordRequestData();
		
		QueryDspace queryDspace = new QueryDspace();
		DSpaceObjectParser parser = new DSpaceObjectParser();
		
		for (Record record : records) {
			boolean match = queryDspace.checkForMatch(record);
			DSpaceObject dspaceObject = new DSpaceObject(record);
			if (match) {
				LOGGER.debug("Potential Match found for: {}", dspaceObject.getTitles());
			}
			else {
				LOGGER.debug("No match found for: {}", dspaceObject.getTitles());
				try {
					Map<String, Set<String>> dspaceValues = parser.getDSpaceValues(dspaceObject);
					Set<BitstreamInfo> bitstreams = getBitstreams(dspaceObject);
					SwordRequestData data = new SwordRequestData(collectionName, dspaceValues, bitstreams, false);
					dataProvider.getSwordRequests().add(data);
				}
				catch (IllegalAccessException | InvocationTargetException e) {
					LOGGER.error("Exception processing dspace values", e);
				}
			}
		}
		return dataProvider;
	}
	
	/**
	 * Process the data provider
	 * 
	 * @param dataProvider The data provider
	 */
	public void processSword(SwordRequestDataProvider dataProvider) {
		String serverUrl = ScoapConfiguration.getProperty("staging", "sword.server");
		String username = ScoapConfiguration.getProperty("staging", "sword.username");
		String password = ScoapConfiguration.getProperty("staging", "sword.password");
		
		String serviceDocumentUrl = serverUrl + "/servicedocument";
		
		ClientConfiguration config = new ClientConfiguration();
		SWORDClient swordClient = new SWORDClient(config);
		SwordServerInfo info = new SwordServerInfo(serviceDocumentUrl, username, password);
		SwordProcessor processor = new SwordProcessor(swordClient, info, dataProvider);
		try {
			processor.process();
		}
		catch (WorkflowException e) {
			LOGGER.error("Exception processing sword records", e);
		}
	}
	
	/**
	 * Get the bitstreams for an item
	 * 
	 * @param dspaceObject The dspace object
	 * @return The bitstreams
	 */
	private Set<BitstreamInfo> getBitstreams(DSpaceObject dspaceObject) {
		List<String> sourceUris = dspaceObject.getSourceUris();
		String pdf = null;
		for (String sourceUri : sourceUris) {
			if (sourceUri.endsWith(".pdf?subformat=pdfa")) {
				pdf = sourceUri;
			}
			else if (pdf == null && sourceUri.endsWith(".pdf")) {
				pdf = sourceUri;
			}
		}
		String depositFilename = generateFilename(dspaceObject, 1, "pdf");
		Set<BitstreamInfo> bitstreams = new LinkedHashSet<BitstreamInfo>();
		if (pdf != null) {
			try {
				BitstreamInfo info = getBitstream(dspaceObject, pdf, depositFilename);
				bitstreams.add(info);
			} 
			catch (IOException e) {
				LOGGER.error("Unable to create a file for {}", pdf, e);
			}
		}
		if (bitstreams.size() > 0) {
			return bitstreams;
		}
		return null;
	}
	
	/**
	 * Get the bitstream for a url
	 * 
	 * @param dspaceObject The dspace object
	 * @param urlPath The url of the bitstream to get
	 * @param depositFilename The name of the bitstream to be saved to DSpace
	 * @return The bitstream inforamtion
	 * @throws IOException
	 */
	private BitstreamInfo getBitstream(DSpaceObject dspaceObject, String urlPath, String depositFilename) throws IOException {
		URL url = new URL(urlPath);
		// We need to convert this to a https connection from http, otherwise the copy url to file method fails as it returns a 302 response
		URL secureUrl = new URL("https",url.getHost(), url.getPort(), url.getFile());
		
		String randomname = UUID.randomUUID().toString();
		
		File tempFile = File.createTempFile(randomname, ".pdf");
		tempFile.deleteOnExit();
		
		FileUtils.copyURLToFile(secureUrl, tempFile);
		String path = tempFile.getAbsolutePath();
		LOGGER.debug("Temp file location: {}", path);
		
		BitstreamInfo info = new FileBitstreamInfo(path, depositFilename);
		
		return info;
	}
	
	/**
	 * Generate a file name for a bitstream from the dspace object
	 * 
	 * @param dspaceObject The dspace object
	 * @param counter The counter
	 * @param fileExtension The file extension
	 * @return The filename
	 */
	private String generateFilename(DSpaceObject dspaceObject, int counter, String fileExtension) {
		List<String> authors = dspaceObject.getAuthors();
		String firstAuthor = "";
		if (authors.size() > 0) {
			firstAuthor = authors.get(0);
			int indexOfComma = firstAuthor.indexOf(",");
			if (indexOfComma > 0) {
				firstAuthor = firstAuthor.substring(0, indexOfComma);
			}
		}
		
		List<String>  titles = dspaceObject.getTitles();
		String title = "";
		if (titles.size() > 0) {
			title = titles.get(0);
		}
		
		if (title.length() >= 31) {
			title = title.substring(0, 31);
			title = title.substring(0, title.lastIndexOf(' '));
		}
		title = title.replaceAll(" ", "_");
		
		List<String> dateIssueds = dspaceObject.getDateIssued();
		String year = "";
		if (dateIssueds.size() > 0) {
			String dateIssued = dateIssueds.get(0);
			int indexOfDash = dateIssued.indexOf("-");
			if (indexOfDash > 0) {
				year = dateIssued.substring(0, indexOfDash);
			}
			else {
				year = dateIssued;
			}
		}

		String filename = String.format("%02d_%s_%s_%s.%s", counter, firstAuthor, title, year, fileExtension);
		return filename;
	}
}
