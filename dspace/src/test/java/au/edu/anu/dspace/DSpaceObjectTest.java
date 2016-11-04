package au.edu.anu.dspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.scoap.MARCParser;
import au.edu.anu.scoap.dspace.DSpaceObject;
import au.edu.anu.scoap.util.ScoapConfiguration;
import au.edu.anu.scoap.xml.Record;

/**
 * Tests the transformation process for a MARC21 record to a dspace object 
 * 
 * @author Genevieve Turner
 */
public class DSpaceObjectTest {
	static final Logger LOGGER = LoggerFactory.getLogger(DSpaceObjectTest.class);
	
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
		assertNotNull("No titles found", dspaceObject.getTitles());
		assertEquals("Unexpected number of titles", 1, dspaceObject.getTitles().size());
		
		assertNotNull("No issns found", dspaceObject.getISSNs());
		assertEquals("Unexpected number of ISSNs", 1, dspaceObject.getISSNs().size());
		assertEquals("Unexpected ISSN value", "0550-3213", dspaceObject.getISSNs().get(0));
		
		assertNotNull("No isbns found", dspaceObject.getISBNs());
		assertEquals("Unexpected number of ISBNs", 0, dspaceObject.getISBNs().size());
		
		assertNotNull("No authors found", dspaceObject.getAuthors());
		assertEquals("Unexpected number of authors", 4, dspaceObject.getAuthors().size());
		assertEquals("Unexpected author value", "Kuniba, A.", dspaceObject.getAuthors().get(0));
		assertEquals("Unexpected author value", "Mangazeev, V.V.", dspaceObject.getAuthors().get(1));
		assertEquals("Unexpected author value", "Maruyama, S.", dspaceObject.getAuthors().get(2));
		assertEquals("Unexpected author value", "Okado, M.", dspaceObject.getAuthors().get(3));
		
		assertNotNull("No author affiliations found", dspaceObject.getAffiliations());
		assertEquals("Unexpected number of author affiliations", 4, dspaceObject.getAffiliations().size());
		assertEquals("Unexpected author affiliation value", "Kuniba, A., Institute of Physics, University of Tokyo, Komaba, Tokyo, 153-8902, Japan", dspaceObject.getAffiliations().get(0));
		assertEquals("Unexpected author affiliation value", "Mangazeev, V.V., Department of Theoretical Physics, Research School of Physics and Engineering, Australian National University, ACT, Canberra, 0200, Australia", dspaceObject.getAffiliations().get(1));
		assertEquals("Unexpected author affiliation value", "Maruyama, S., Institute of Physics, University of Tokyo, Komaba, Tokyo, 153-8902, Japan", dspaceObject.getAffiliations().get(2));
		assertEquals("Unexpected author affiliation value", "Okado, M., Department of Mathematics, Osaka City University, 3-3-138, Sugimoto, Sumiyoshi-ku, Osaka, 558-8585, Japan", dspaceObject.getAffiliations().get(3));
		
		assertNotNull("No author emails found", dspaceObject.getAuthorEmails());
		assertEquals("Unexpected number of author emails", 1, dspaceObject.getAuthorEmails().size());
		assertEquals("Unexpected author email value","vladimir.mangazeev@anu.edu.au",dspaceObject.getAuthorEmails().get(0));
		
		assertNotNull("No publishers found", dspaceObject.getPublishers());
		assertEquals("Unexpected number of publishers", 1, dspaceObject.getPublishers().size());
		assertEquals("Unexpected publisher value", "Elsevier", dspaceObject.getPublishers().get(0));
		
		assertNotNull("No date issueds found", dspaceObject.getDateIssued());
		assertEquals("Unexpected number of date issueds", 1, dspaceObject.getDateIssued().size());
		assertEquals("Unexpected date issued","2016-10-03", dspaceObject.getDateIssued().get(0));
		
		assertNotNull("No sources found", dspaceObject.getSources());
		assertEquals("Unexpected number of sources", 1, dspaceObject.getSources().size());
		assertEquals("Unexpected source value", "Nuclear Physics B", dspaceObject.getSources().get(0));
		
		assertNotNull("No source uris found", dspaceObject.getSourceUris());
		assertEquals("Unexpected number of source uris", 2, dspaceObject.getSourceUris().size());
		assertEquals("Unexpected source uri", "http://repo.scoap3.org/record/17310/files/main.pdf", dspaceObject.getSourceUris().get(0));
		assertEquals("Unexpected source uri", "http://repo.scoap3.org/record/17310/files/main.xml", dspaceObject.getSourceUris().get(1));
		
		assertNotNull("No abstracts found", dspaceObject.getAbstracts());
		assertEquals("Unexpected number of abstracts", 1, dspaceObject.getAbstracts().size());
		assertEquals("Unexpected abstract value", "We show that the quantum R matrix for symmetric tensor representations of Uq(An(1)) satisfies the sum rule required for its stochastic interpretation under a suitable gauge. Its matrix elements at a special point of the spectral parameter are found to factorize into the form that naturally extends Povolotsky's local transition rate in the q -Hahn process for n=1 . Based on these results we formulate new discrete and continuous time integrable Markov processes on a one-dimensional chain in terms of n species of particles obeying asymmetric stochastic dynamics. Bethe ansatz eigenvalues of the Markov matrices are also given.", dspaceObject.getAbstracts().get(0));
		
		assertNotNull("No dois found", dspaceObject.getDOIs());
		assertEquals("Unexpected number of dois", 1, dspaceObject.getDOIs().size());
		assertEquals("Unexpected DOI value", "10.1016/j.nuclphysb.2016.09.016", dspaceObject.getDOIs().get(0));
		
		assertNotNull("No isbns identifiers", dspaceObject.getIdentifiers());
		assertEquals("Unexpected number of identifiers", 1, dspaceObject.getIdentifiers().size());
		assertEquals("Unexpected identifier value", "17310", dspaceObject.getIdentifiers().get(0));
		
		assertNotNull("No isbns rights", dspaceObject.getRights());
		assertEquals("Unexpected number of rights", 1, dspaceObject.getRights().size());
		assertEquals("Unexpected rights value", "CC-BY-3.0", dspaceObject.getRights().get(0));
		
		assertNotNull("No rights uris found", dspaceObject.getRightsUris());
		assertEquals("Unexpected number of rights uris", 1, dspaceObject.getRightsUris().size());
		assertEquals("Unexpected rights uri value", "http://creativecommons.org/licenses/by/3.0/", dspaceObject.getRightsUris().get(0));
		
		assertNotNull("No subjects found", dspaceObject.getSubjects());
		assertEquals("Unexpected number of subjects", 3, dspaceObject.getSubjects().size());
		assertEquals("Unexpected subject value", "inflation", dspaceObject.getSubjects().get(0));
		assertEquals("Unexpected subject value", "string theory and cosmology", dspaceObject.getSubjects().get(1));
		assertEquals("Unexpected subject value", "supersymmetry and cosmology", dspaceObject.getSubjects().get(2));
		
		assertNotNull("No start pages found", dspaceObject.getStartPages());
		assertEquals("Unexpected number of start pages", 1, dspaceObject.getStartPages().size());
		assertEquals("Unexpected start page", "621", dspaceObject.getStartPages().get(0));
		
		assertNotNull("No last pages rights", dspaceObject.getLastPages());
		assertEquals("Unexpected number of last pages", 1, dspaceObject.getLastPages().size());
		assertEquals("Unexpected last page", "664", dspaceObject.getLastPages().get(0));
		
		assertNotNull("No citation volumes rights", dspaceObject.getCitationVolumes());
		assertEquals("Unexpected number of citation volumes", 1, dspaceObject.getCitationVolumes().size());
		assertEquals("Unexpected citation volume", "894", dspaceObject.getCitationVolumes().get(0));
		
		assertNotNull("No issue numbers found", dspaceObject.getIssueNumbers());
		assertEquals("Unexpected number of issue numbers", 1, dspaceObject.getIssueNumbers().size());
		assertEquals("Unexpected issue numbers", "05", dspaceObject.getIssueNumbers().get(0));
		
		String swordUsername = ScoapConfiguration.getProperty("staging", "sword.username");
		
		assertNotNull("No submitter uids found", dspaceObject.getSubmitterUid());
		assertEquals("Unexpected number of submitter uids numbers", 1, dspaceObject.getSubmitterUid().size());
		assertEquals("Unexpected submitter uid", swordUsername, dspaceObject.getSubmitterUid().get(0));
	}
}
