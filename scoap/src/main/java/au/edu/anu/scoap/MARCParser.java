package au.edu.anu.scoap;

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.scoap.xml.MARCCollection;
import au.edu.anu.scoap.xml.Record;

/**
 * Transforms an XML document to a marc 21 java object representation
 * 
 * @author Genevieve Turner
 *
 */
public class MARCParser {
	static final Logger LOGGER = LoggerFactory.getLogger(MARCParser.class);
	
	private static JAXBContext jaxbContext;
	
	/**
	 * Get the JAXBContext
	 * 
	 * @return The context
	 * @throws JAXBException
	 */
	private JAXBContext getJAXBContext()  throws JAXBException {
		if (jaxbContext == null) {
			jaxbContext = JAXBContext.newInstance(MARCCollection.class);
		}
		return jaxbContext;
	}
	
	/**
	 * Get a list of MARC21 records from a xml document in a string representation
	 * 
	 * @param xml The string of xml
	 * @return A list of records
	 * @throws JAXBException
	 */
	public List<Record> getRecords(String xml) throws JAXBException {
		JAXBContext jaxbContext = getJAXBContext();
		
		StringReader reader = new StringReader(xml);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		MARCCollection collection = (MARCCollection)unmarshaller.unmarshal(reader);
		return collection.getRecords();
	}
	
	/**
	 * Get a list of MARC21 recoreds from an xml document in an input stream format
	 * 
	 * @param is The xml input stream
	 * @return A list of records
	 * @throws JAXBException
	 * @throws XMLStreamException
	 */
	public List<Record> getRecords(InputStream is) throws JAXBException, XMLStreamException {
		JAXBContext jaxbContext = getJAXBContext();
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		MARCCollection collection = (MARCCollection)unmarshaller.unmarshal(is);
		
		return collection.getRecords();
	}
}
