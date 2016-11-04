package au.edu.anu.scoap.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test the number of MARC21 transformations
 * 
 * @author Genevieve Turner
 */
public class MARCCollectionTest {
	static final Logger LOGGER = LoggerFactory.getLogger(MARCCollectionTest.class);
	
	@Test
	public void test() {
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
		assertNotNull("No records found", collection.getRecords());
		assertEquals("Unexpected number of records found", 6, collection.getRecords().size());
		Record record1 = collection.getRecords().get(0);
		assertNotNull("No control fields found", record1.getControlFields());
		assertEquals("Incorrect number of control fields", 2, record1.getControlFields().size());
		assertNotNull("No data fields found", record1.getDataFields());
		assertEquals("Incorrect number of data fields",14,record1.getDataFields().size());
		
		ControlField controlField = record1.getControlFields().get(0);
		assertEquals("Unexpected control field tag","001",controlField.getTag());
		assertEquals("Unexpected control field value","17310",controlField.getText());
		controlField = record1.getControlFields().get(1);
		assertEquals("Unexpected control field tag","005",controlField.getTag());
		assertEquals("Unexpected control field value","20161003221603.0",controlField.getText());
		
		DataField dataField = record1.getDataFields().get(0);
		assertEquals("Unexpected data field tag", "022", dataField.getTag());
		assertEquals("Unexpected data field indicator 1", " ", dataField.getIndicator1());
		assertEquals("Unexpected data field indicator 2", " ", dataField.getIndicator2());
		assertNotNull("No subfields found", dataField.getSubFields());
		assertEquals("Unexpected number of sub fields", 1, dataField.getSubFields().size());
		SubField subField = dataField.getSubFields().get(0);
		assertEquals("Unexpected code value", "a",subField.getCode());
		assertEquals("Unexpected text value", "0550-3213", subField.getText());
		
		dataField = record1.getDataFields().get(12);
		assertEquals("Unexpected data field tag", "856", dataField.getTag());
		assertEquals("Unexpected data field indicator 1", "4", dataField.getIndicator1());
		assertEquals("Unexpected data field indicator 2", " ", dataField.getIndicator2());
		assertNotNull("No subfields found", dataField.getSubFields());
		assertEquals("Unexpected number of sub fields", 2, dataField.getSubFields().size());
		subField = dataField.getSubFields().get(0);
		assertEquals("Unexpected code value", "s",subField.getCode());
		assertEquals("Unexpected text value", "492185", subField.getText());
		subField = dataField.getSubFields().get(1);
		assertEquals("Unexpected code value", "u",subField.getCode());
		assertEquals("Unexpected text value", "http://repo.scoap3.org/record/17310/files/main.pdf", subField.getText());
	}
}
