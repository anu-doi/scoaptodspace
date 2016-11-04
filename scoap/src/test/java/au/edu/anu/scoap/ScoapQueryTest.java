package au.edu.anu.scoap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.scoap.xml.Record;

/**
 * This class tests that querying scoap works.
 * 
 * @author Genevieve Turner
 */
public class ScoapQueryTest {
	static final Logger LOGGER = LoggerFactory.getLogger(ScoapQuery.class);
	
	@Ignore
	@Test
	public void test() {
		ScoapQuery query = new ScoapQuery();
		List<Record> records = null;
		try {
			records = query.getAllRecords();
		}
		catch (JAXBException e) {
			LOGGER.error("Exception getting records", e);
			fail("Error getting records");
		}
		assertNotNull("No records found", records);
		assertEquals("Unexpected number of records", 22, records.size());
	}
	
	@Ignore
	@Test
	public void testEarliestDate() {
		ScoapQuery query = new ScoapQuery();
		List<Record> records = null;
		try {
			records = query.getAllRecords("2016-08-01","2016-10-01");
		}
		catch (JAXBException e) {
			LOGGER.error("Exception getting records", e);
			fail("Error getting records");
		}
		assertNotNull("No records found", records);
		assertEquals("Unexpected number of records", 3, records.size());
	}
}
