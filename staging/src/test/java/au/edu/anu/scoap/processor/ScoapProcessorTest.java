package au.edu.anu.scoap.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.scoap.sword.data.BitstreamInfo;
import au.edu.anu.scoap.sword.data.SwordRequestData;
import au.edu.anu.scoap.sword.data.SwordRequestDataProvider;
import au.edu.anu.scoap.xml.MARCCollection;
import au.edu.anu.scoap.xml.Record;

/**
 * Tests the processing of some ScoapProcessor methods.
 * 
 * @author Genevieve Turner
 */
public class ScoapProcessorTest {
	static final Logger LOGGER = LoggerFactory.getLogger(ScoapProcessorTest.class);
	
	/**
	 * Tests whether there is request contents after the data provider has been run
	 */
	@Ignore
	@Test
	public void testDataProviderGeneration() {
		InputStream inputStream = this.getClass().getResourceAsStream("/scoap.xml");
		MARCCollection collection = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(MARCCollection.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			collection = (MARCCollection)unmarshaller.unmarshal(inputStream);
		}
		catch (JAXBException e) {
			LOGGER.error("Exception unmarshalling document", e);
			fail("Unable to unmarshal document");
		}
		List<Record> records = collection.getRecords();
		assertNotNull("No records found", records);
		assertEquals("Unexpected number of records found", 6, records.size());
		
		ScoapProcessor scoapProcessor = new ScoapProcessor("Test Collection 3", null, null);
		
		SwordRequestDataProvider dataProvider = scoapProcessor.generateSwordRequests(records);
		assertNotNull("No data provider generated", dataProvider);
		
		List<SwordRequestData> requestData = dataProvider.getSwordRequests();
		assertNotNull("No data found", requestData);
		assertEquals("Unexpected number of request data", 4, requestData.size());
		
		SwordRequestData request = requestData.get(0);
		Map<String, Set<String>> metadata = request.getMetadata();
		assertNotNull("No metadata found", metadata);
		LOGGER.info("Metadata keys: {}",metadata.keySet().toString());
		Set<String> values = metadata.get("dc-title");
		assertNotNull("No titles found", values);
		assertEquals("Unexpected number of titles", 1, values.size());
		assertEquals("Unexpected title", "The super-Virasoro singular vectors and Jack superpolynomials relationship revisited", values.iterator().next());
		
		Set<BitstreamInfo> bitstreams = request.getBitstreams();
		assertNotNull("No bitstreams found", bitstreams);
		assertEquals("Unexpected nubmer of bitstreams", 1, bitstreams.size());
		BitstreamInfo info = bitstreams.iterator().next();
		assertNotNull("No filename found", info.getFilename());
		assertTrue(info.getFilename().length() > 0);
		try {
			assertEquals("Unexpected deposit file name", "01_Blondeau-Fournier_The_super-Virasoro_singular_2016.pdf", info.getFilenameToDeposit());
		}
		catch (UnsupportedEncodingException e) {
			LOGGER.error("Exception retrieving deposit filename", e);
			fail("Exception retrieving deposit filename");
		}
	}
}
