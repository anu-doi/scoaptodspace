package au.edu.anu.scoap.dspace;

import java.util.ArrayList;
import java.util.List;

import au.edu.anu.scoap.dspace.annotation.DSpaceField;
import au.edu.anu.scoap.util.ScoapConfiguration;
import au.edu.anu.scoap.xml.DataField;
import au.edu.anu.scoap.xml.Record;

/**
 * DSpace object that maps a Marc21 record to dspace fields
 * 
 * @author Genevieve Turner
 *
 */
public class DSpaceObject {
	
	Record record;
	
	public DSpaceObject(Record record) {
		this.record = record;
	}
	
	@DSpaceField("dc-title")
	public List<String> getTitles() {
		return record.getDataFieldValues("245", "a");
	}
	
	@DSpaceField("dc-identifier-issn")
	public List<String> getISSNs() {
		return record.getDataFieldValues("022", "a");
	}
	
	@DSpaceField("dc-identifier-isbn")
	public List<String> getISBNs() {
		return record.getDataFieldValues("020", "a");
	}
	
	@DSpaceField("dc-contributor-author")
	public List<String> getAuthors() {
		List<String> values = new ArrayList<String>();
		
		values.addAll(record.getDataFieldValues("100", "a"));
		values.addAll(record.getDataFieldValues("700", "a"));
		
		return values;
	}
	
	@DSpaceField("local-contributor-affiliation")
	public List<String> getAffiliations() {
		List<String> values = new ArrayList<String>();
		
		List<DataField> authorFields = record.getDataFields("100", " ", " ");
		for (DataField field : authorFields) {
			String affiliation = field.getSubfieldValue("v");
			if (affiliation != null) {
				String author = field.getSubfieldValue("a");
				values.add(author + ", " + affiliation);
			}
		}
		
		authorFields = record.getDataFields("700", " ", " ");
		for (DataField field : authorFields) {
			String affiliation = field.getSubfieldValue("v");
			if (affiliation != null) {
				String author = field.getSubfieldValue("a");
				values.add(author + ", " + affiliation);
			}
		}
		
		return values;
	}
	
	/**
	 * Get the author emails, filters out those specific to ANU
	 * 
	 * @return A list of ANU emails
	 */
	@DSpaceField("local-contributor-authoremail")
	public List<String> getAuthorEmails() {
		List<String> values = new ArrayList<String>();
		
		List<String> emails = record.getDataFieldValues("100", "m");
		for (String email : emails) {
			if (email.contains("anu.edu.au")) {
				values.add(email);
			}
		}
		
		emails = record.getDataFieldValues("700", "m");
		for (String email : emails) {
			if (email.contains("anu.edu.au")) {
				values.add(email);
			}
		}
		
		return values;
	}
	
	@DSpaceField("dc-publisher")
	public List<String> getPublishers() {
		return record.getDataFieldValues("260", "b");
	}
	
	@DSpaceField("dc-date-issued")
	public List<String> getDateIssued() {
		return record.getDataFieldValues("260", "c");
	}
	
	@DSpaceField("dc-source")
	public List<String> getSources() {
		return record.getDataFieldValues("773", "p");
	}
	
	@DSpaceField("dc-source-uri")
	public List<String> getSourceUris() {
		return record.getDataFieldValues("856", "u", "4", " ");
	}
	
	@DSpaceField("dc-description-abstract")
	public List<String> getAbstracts() {
		return record.getDataFieldValues("520", "a");
	}
	
	@DSpaceField("local-identifier-doi")
	public List<String> getDOIs() {
		List<String> values = new ArrayList<String>();
		List<DataField> dataFields = record.getDataFields("024", "7", " ");
		for (DataField field : dataFields) {
			String type = field.getSubfieldValue("2");
			if ("DOI".equals(type)) {
				String doi = field.getSubfieldValue("a");
				values.add(doi);
			}
		}
		return values;
	}
	
	@DSpaceField("dc-identifier")
	public List<String> getIdentifiers() {
		return record.getControlField("001");
	}
	
	@DSpaceField("dc-rights")
	public List<String> getRights() {
		return record.getDataFieldValues("540", "a");
	}
	
	@DSpaceField("dc-rights-uri")
	public List<String> getRightsUris() {
		return record.getDataFieldValues("540", "u");
	}
	
	@DSpaceField("dc-subject")
	public List<String> getSubjects() {
		return record.getDataFieldValues("653", "a", "1", " ");
	}

	@DSpaceField("local-bibliographicCitation-startpage")
	public List<String> getStartPages() {
		List<DataField> dataFields = record.getDataFields("773", " ", " ");
		List<String> values = new ArrayList<String>();
		for (DataField dataField : dataFields) {
			String page = dataField.getSubfieldValue("c");
			if (page != null) {
				String[] splits = page.split("-");
				if (splits.length > 0) {
					values.add(splits[0]);
				}
			}
		}
		
		return values;
	}

	@DSpaceField("local-bibliographicCitation-lastpage")
	public List<String> getLastPages() {
		List<DataField> dataFields = record.getDataFields("773", " ", " ");
		List<String> values = new ArrayList<String>();
		for (DataField dataField : dataFields) {
			String page = dataField.getSubfieldValue("c");
			if (page != null) {
				String[] splits = page.split("-");
				if (splits.length > 1) {
					values.add(splits[1]);
				}
			}
		}
		
		return values;
	}

	@DSpaceField("local-identifier-citationvolume")
	public List<String> getCitationVolumes() {
		return record.getDataFieldValues("773", "v");
	}

	@DSpaceField("local-bibliographicCitation-issue")
	public List<String> getIssueNumbers() {
		return record.getDataFieldValues("773", "n");
	}
	
	@DSpaceField("local-identifier-uidSubmittedBy")
	public List<String> getSubmitterUid() {
		String swordUsername = ScoapConfiguration.getProperty("staging", "sword.username");
		List<String> values = new ArrayList<String>();
		if (swordUsername != null && swordUsername.length() > 0) {
			values.add(swordUsername);
		}
		
		return values;
	}
}
