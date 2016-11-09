package au.edu.anu.scoap.commands;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.scoap.processor.ScoapProcessor;
import au.edu.anu.scoap.sword.data.SwordRequestDataProvider;
import au.edu.anu.scoap.util.ScoapConfiguration;
import au.edu.anu.scoap.xml.Record;

/**
 * Command for processing Scoap3
 * 
 * @author Genevieve Turner
 */
public class ScoapCommand {
	static final Logger LOGGER = LoggerFactory.getLogger(ScoapCommand.class);
	
	@Option(name="-h", aliases="--help", usage="Display this")
	private boolean help = false;
	
	@Option(name="-s", aliases="--earliest-date", required=false, usage="Date to find records from (format yyyy-MM-dd)")
	private String earliestDate = null;
	
	@Option(name="-e", aliases="--latest-date", required=false, usage="Date to find records to (format yyyy-MM-dd)")
	private String latestDate = null;
	
	@Option(name="-c", aliases="--collection-name",required=false,usage="For new items submit to a collection other than the default")
	private String collectionName;
	
	/**
	 * Run the command based on the properties.
	 * 
	 * @throws ScoapCommandException
	 */
	public void run() throws ScoapCommandException {
		if (help) {
			CommandUtil.printUsage(this.getClass());
			return;
		}
		
		//Check that the date is in a usable format
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (earliestDate != null) {
				sdf.parse(earliestDate);
			}
			if (latestDate != null) {
				sdf.parse(latestDate);
			}
		}
		catch (ParseException e) {
			throw new ScoapCommandException("Incorrect date format");
		}
		
		if (collectionName == null) {
			collectionName = ScoapConfiguration.getProperty("staging", "sword.default.collection");
		}
		ScoapProcessor processor = new ScoapProcessor(collectionName, earliestDate, latestDate);
		try {
			List<Record> records = processor.getRecords();
			SwordRequestDataProvider dataProvider = processor.generateSwordRequests(records);
			processor.processSword(dataProvider);
		}
		catch (JAXBException e) {
			LOGGER.error("Exception processing records xml", e);
		}
	}
}
