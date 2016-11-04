package au.edu.anu.dspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.scoap.MARCParser;
import au.edu.anu.scoap.dspace.QueryDspace;
import au.edu.anu.scoap.xml.Record;

/**
 * Tests the matching process
 * 
 * @author Genevieve Turner
 */
public class QueryDSpaceTest {
	static final Logger LOGGER = LoggerFactory.getLogger(QueryDSpaceTest.class);
	
	@Ignore
	@Test
	public void test() {
		InputStream inputStream = this.getClass().getResourceAsStream("/scoap.xml");
		MARCParser parser = new MARCParser();
		List<Record> records = null;
		try {
			records = parser.getRecords(inputStream);
			inputStream.close();
		}
		catch (Exception e) {
			LOGGER.error("Exception processing records", e);
			fail("Exception processing records");
		}
		assertNotNull("No records found", records);
		assertEquals("Unexpected number of records", 1, records.size());
		QueryDspace query = new QueryDspace();
		boolean found = query.checkForMatch(records.get(0));
		assertTrue("Unexpected found value", found);
		
	}
}
