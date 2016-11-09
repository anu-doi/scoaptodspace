package au.edu.anu.scoap.dspace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.simmetrics.StringMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.scoap.util.ScoapConfiguration;
import au.edu.anu.scoap.xml.Record;

/**
 * Query dspace checking for matches on the records
 * 
 * @author Genevieve Turner
 *
 */
public class QueryDspace {
	static final Logger LOGGER = LoggerFactory.getLogger(QueryDspace.class);
	private static final double METRIC_MEASURE = 0.8;
	
	private List<String[]> matches = new ArrayList<String[]>();
	
	/**
	 * Check how similar two Strings are via the Simon White method.
	 * 
	 * @param title1 Title one
	 * @param title2 Title 2
	 * @return Whether there is a high confidence that the titles are the same.
	 */
	private boolean performMetrics(String value1, String value2) {
		float metric = StringMetrics.simonWhite().compare(value1, value2);
		if (metric > METRIC_MEASURE) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check for matches in DSpace.
	 * 
	 * @param record The record to check
	 * @return Whether a match was found
	 */
	public boolean checkForMatch(Record record) {
		DSpaceObject dspaceObject = new DSpaceObject(record);
		List<String> identifiers = dspaceObject.getIdentifiers();
		String id = identifiers.get(0);
		
		matches.clear();
		
		SolrQuery solrQuery = new SolrQuery();
		
		List<String> titles = dspaceObject.getTitles();
		String title = titles.get(0);

		solrQuery.setFields("dc.title","handle","search.resourceid","dc.contributor.author", "score");
		solrQuery.setQuery("dc.title:("+ClientUtils.escapeQueryChars(title)+")");
		solrQuery.addFilterQuery("search.resourcetype:2 AND withdrawn:false");
		
		List<String> dateIssued = dspaceObject.getDateIssued();
		if (dateIssued.size() > 0) {
			String date = dateIssued.get(0);
			String year = null;
			int indexOfDash = date.indexOf("-");
			if (indexOfDash > 0) {
				year = date.substring(0, indexOfDash);
			}
			else {
				year = date;
			}
			solrQuery.addFilterQuery("dateIssued.year:" + year);
		}
		
		List<String> authors = dspaceObject.getAuthors();
		StringBuilder authorMatch = new StringBuilder();
		boolean isFirst = true;
		for (String author : authors) {
			int commaIndex = author.indexOf(",");
			if (commaIndex > 0) {
				author = author.substring(0, commaIndex);
			}
			if (!isFirst) {
				authorMatch.append(" AND ");
			}
			else {
				isFirst = false;
			}
			authorMatch.append("dc.contributor.author:(");
			authorMatch.append(ClientUtils.escapeQueryChars(author));
			authorMatch.append(")");
		}
		if (authorMatch.length() > 0) {
			solrQuery.addFilterQuery(authorMatch.toString());
		}
		
		LOGGER.info("Searching solr: {}", solrQuery.toString());

		boolean matchFound = false;
		
		String solrServerLocation = ScoapConfiguration.getProperty("dspace", "solr.server");
		SolrServer solrServer = new HttpSolrServer(solrServerLocation);
		try {
			QueryResponse response = solrServer.query(solrQuery);
			SolrDocumentList list = response.getResults();
			Iterator<SolrDocument> it = list.iterator();
			matches.clear();
			while (it.hasNext()) {
				SolrDocument doc = it.next();
				String handle = (String) doc.get("handle");
				@SuppressWarnings("unchecked")
				List<String> idtitles = (List<String>) doc.get("dc.title");
				if (idtitles != null && idtitles.size() > 0) {
					if (performMetrics(title, idtitles.get(0))) {
						LOGGER.debug("Potential match found with the handle {}. Comparison of \"{}\"  to \"{}\"", handle, title, idtitles.get(0));
						matchFound = true;
						matches.add(new String[]{handle,id});
					}
				}
			}
		}
		catch (SolrServerException e) {
			LOGGER.error("Exception querying Solr", e);
		}
		
		return matchFound;
	}
	
	public List<String[]> getMatches() {
		return matches;
	}
}
