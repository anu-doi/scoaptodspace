package au.edu.anu.scoap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.scoap.util.ScoapConfiguration;
import au.edu.anu.scoap.xml.Record;

/**
 * Query Scoap3 for 
 * 
 * @author Genevieve Turner
 *
 */
public class ScoapQuery {
	static final Logger LOGGER = LoggerFactory.getLogger(ScoapQuery.class);
	
	private Integer numFound;
	
	/**
	 * Get all records
	 * 
	 * @return The records
	 * @throws JAXBException
	 */
	public List<Record> getAllRecords() throws JAXBException {
		return getAllRecords(null, null, 10);
	}
	
	/**
	 * Get all the records since the modified date
	 * 
	 * @param dateModified The last modified date
	 * @return The records
	 * @throws JAXBException
	 */
	public List<Record> getAllRecords(String earliestDate, String latestDate) throws JAXBException {
		return getAllRecords(earliestDate, latestDate, 10);
	}
	
	/**
	 * Get all the records from the modified date
	 * 
	 * @param dateModified THe earliest modified date
	 * @param rows The number of rows in a query
	 * @return The records
	 * @throws JAXBException
	 */
	public List<Record> getAllRecords(String earliestDate, String latestDate, Integer rows) throws JAXBException {
		Integer start = 1;
		List<Record> records = new ArrayList<Record>();
		do {
			records.addAll(query(start, rows, earliestDate, latestDate));
			start = start + rows;
		}
		while (start < numFound);
		return records;
	}
	
	/**
	 * Query Scoap3 for the records
	 * 
	 * @param dateModified The earliest modified date
	 * @return The list of records
	 * @throws JAXBException
	 */
	public List<Record> query(String earliestDate, String latestDate) throws JAXBException {
		return query(0,10,earliestDate, latestDate);
	}
	
	/**
	 * Query Scoap3 for the records
	 * 
	 * @param start The record to return the records from
	 * @param rows The number of rows
	 * @param dateModified The earliest modified date
	 * @return The list of records
	 * @throws JAXBException
	 */
	public List<Record> query(Integer start, Integer rows, String earliestDate, String latestDate) throws JAXBException {
		Client client = ClientBuilder.newClient();
		String scoapUrl = ScoapConfiguration.getProperty("scoap", "scoap.url");
		WebTarget target = client.target(scoapUrl);
		String dateModifiedStr = "";
		if (earliestDate != null || latestDate != null) {
			if (earliestDate == null) {
				earliestDate = "0000-00-00";
			}
			if (latestDate == null) {
				latestDate = "9999-01-01";
			}
			dateModifiedStr = " and datemodified:"+earliestDate+"->"+latestDate;
		}
		String defaultQuery = ScoapConfiguration.getProperty("scoap", "scoap.query");
		String queryString = defaultQuery + dateModifiedStr;
		target = target.queryParam("p", queryString);
		target = target.queryParam("of", "xm");
		target = target.queryParam("jrec", start);
		target = target.queryParam("rg", rows);
		
		Response response = target.request(MediaType.APPLICATION_XML).get();
		String scoapStream = response.readEntity(String.class);
		
		Pattern phrase = Pattern.compile("Search-Engine-Total-Number-Of-Results: (\\d+)");
		Matcher match = phrase.matcher(scoapStream);
		boolean found = match.find();
		if (found) {
			match.group(0);
			Integer numFound = new Integer(match.group(1));
			this.numFound = numFound;
		}
		
		MARCParser parser = new MARCParser();
		List<Record> records = parser.getRecords(scoapStream);
		return records;
	}
}
