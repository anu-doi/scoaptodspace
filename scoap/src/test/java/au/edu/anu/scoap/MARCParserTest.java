package au.edu.anu.scoap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.scoap.xml.Record;

/**
 * Test for checking if the parsing of marc 21 records will occur
 * 
 * @author Genevieve Turner
 */
public class MARCParserTest {
	static final Logger LOGGER = LoggerFactory.getLogger(MARCParserTest.class);
	@Ignore
	@Test
	public  void test() {
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
		assertEquals("Unexpeted number of records", 6, records.size());
	}
	
	@Test
	public  void testString() {
		InputStream inputStream = this.getClass().getResourceAsStream("/scoap.xml");
		
		Scanner s = new Scanner(inputStream).useDelimiter("\\A");
		String result = s.hasNext() ? s.next() : "";
		s.close();
		try {
			inputStream.close();
		}
		catch (IOException e) {
			LOGGER.error("Exception closing input stream", e);
		}
		MARCParser parser = new MARCParser();
		List<Record> records = null;
		try {
			records = parser.getRecords(result);
		}
		catch (Exception e) {
			LOGGER.error("Exception processing records", e);
			fail("Exception processing records");
		}
		assertNotNull("No records found", records);
		assertEquals("Unexpected number of records", 6, records.size());
	}
}
