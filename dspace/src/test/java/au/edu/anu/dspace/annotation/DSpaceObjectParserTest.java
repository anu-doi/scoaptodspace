package au.edu.anu.dspace.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.scoap.MARCParser;
import au.edu.anu.scoap.dspace.DSpaceObject;
import au.edu.anu.scoap.dspace.annotation.DSpaceObjectParser;
import au.edu.anu.scoap.xml.Record;

/**
 * Test the DspaceObject parser
 * 
 * @author Genevieve Turner
 */
public class DSpaceObjectParserTest {
	static final Logger LOGGER = LoggerFactory.getLogger(DSpaceObjectParserTest.class);
	
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
		
		DSpaceObject dspaceObject = new DSpaceObject(records.get(0));
		
		DSpaceObjectParser dspaceParser = new DSpaceObjectParser();
		Map<String, Set<String>> valuesMap = null;
		try {
			valuesMap = dspaceParser.getDSpaceValues(dspaceObject);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error("Exception parsing dspace object", e);
			fail("Exception parsing dpsace object");
		}
		
		assertNotNull("No DSpace values found", valuesMap);
		
		Set<String> values = valuesMap.get("dc-identifier");
		assertNotNull("No identifiers found", values);
		assertEquals("Unexpected number of identifiers", 1, values.size());
		Iterator<String> it = values.iterator();
		assertEquals("Unexpected identifier value", "17310", it.next());
		
		values = valuesMap.get("dc-identifier-issn");
		assertNotNull("No issn found", values);
		assertEquals("Unexpected number of issns", 1, values.size());
		it = values.iterator();
		assertEquals("Unexpected identifier value", "0550-3213", it.next());
		
		values = valuesMap.get("local-identifier-doi");
		assertNotNull("No doi found", values);
		assertEquals("Unexpected number of dois", 1, values.size());
		it = values.iterator();
		assertEquals("Unexpected identifier value", "10.1016/j.nuclphysb.2016.09.016", it.next());
		
		values = valuesMap.get("dc-contributor-author");
		assertNotNull("No authors found", values);
		assertEquals("Unexpected number of authors", 4, values.size());
		it = values.iterator();
		assertEquals("Unexpected author value", "Kuniba, A.", it.next());
		assertEquals("Unexpected author value", "Mangazeev, V.V.", it.next());
		assertEquals("Unexpected author value", "Maruyama, S.", it.next());
		assertEquals("Unexpected author value", "Okado, M.", it.next());
		
		values = valuesMap.get("local-contributor-affiliation");
		assertNotNull("No affiliations found", values);
		assertEquals("Unexpected number of affiliations", 4, values.size());
		it = values.iterator();
		assertEquals("Unexpected affiliation value", "Kuniba, A., Institute of Physics, University of Tokyo, Komaba, Tokyo, 153-8902, Japan", it.next());
		assertEquals("Unexpected author value", "Mangazeev, V.V., Department of Theoretical Physics, Research School of Physics and Engineering, Australian National University, ACT, Canberra, 0200, Australia", it.next());
		assertEquals("Unexpected author value", "Maruyama, S., Institute of Physics, University of Tokyo, Komaba, Tokyo, 153-8902, Japan", it.next());
		assertEquals("Unexpected author value", "Okado, M., Department of Mathematics, Osaka City University, 3-3-138, Sugimoto, Sumiyoshi-ku, Osaka, 558-8585, Japan", it.next());
		
		values = valuesMap.get("local-contributor-authoremail");
		assertNotNull("No author emails found", values);
		assertEquals("Unexpected number of author emails", 1, values.size());
		it = values.iterator();
		assertEquals("Unexpected author email value", "vladimir.mangazeev@anu.edu.au", it.next());
		
		values = valuesMap.get("dc-title");
		assertNotNull("No titles found", values);
		assertEquals("Unexpected number of titles", 1, values.size());
		it = values.iterator();
		assertEquals("Unexpected title value", "Stochastic R matrix for Uq(An(1))", it.next());
		
		values = valuesMap.get("dc-date-issued");
		assertNotNull("No issued dates found", values);
		assertEquals("Unexpected number of issued dates", 1, values.size());
		it = values.iterator();
		assertEquals("Unexpected issued date value", "2016-10-03", it.next());
		
		values = valuesMap.get("dc-publisher");
		assertNotNull("No publishers found", values);
		assertEquals("Unexpected number of publishers", 1, values.size());
		it = values.iterator();
		assertEquals("Unexpected publishers value", "Elsevier", it.next());
		
		values = valuesMap.get("dc-description-abstract");
		assertNotNull("No abstracts found", values);
		assertEquals("Unexpected number of abstracts", 1, values.size());
		it = values.iterator();
		assertEquals("Unexpected abstracts value", "We show that the quantum R matrix for symmetric tensor representations of Uq(An(1)) satisfies the sum rule required for its stochastic interpretation under a suitable gauge. Its matrix elements at a special point of the spectral parameter are found to factorize into the form that naturally extends Povolotsky's local transition rate in the q -Hahn process for n=1 . Based on these results we formulate new discrete and continuous time integrable Markov processes on a one-dimensional chain in terms of n species of particles obeying asymmetric stochastic dynamics. Bethe ansatz eigenvalues of the Markov matrices are also given.", it.next());
		
		values = valuesMap.get("dc-rights");
		assertNotNull("No rights found", values);
		assertEquals("Unexpected number of rights", 1, values.size());
		it = values.iterator();
		assertEquals("Unexpected rights value", "CC-BY-3.0", it.next());
		
		values = valuesMap.get("dc-rights-uri");
		assertNotNull("No rights uris found", values);
		assertEquals("Unexpected number of rights uris", 1, values.size());
		it = values.iterator();
		assertEquals("Unexpected rights uri value", "http://creativecommons.org/licenses/by/3.0/", it.next());
		
		values = valuesMap.get("dc-source");
		assertNotNull("No sources found", values);
		assertEquals("Unexpected number of sources", 1, values.size());
		it = values.iterator();
		assertEquals("Unexpected sources value", "Nuclear Physics B", it.next());
		
		values = valuesMap.get("dc-source-uri");
		assertNotNull("No source uris found", values);
		assertEquals("Unexpected number of source uris", 2, values.size());
		it = values.iterator();
		assertEquals("Unexpected source uri value", "http://repo.scoap3.org/record/17310/files/main.pdf", it.next());
		assertEquals("Unexpected source uri value", "http://repo.scoap3.org/record/17310/files/main.xml", it.next());
		
	}
}
