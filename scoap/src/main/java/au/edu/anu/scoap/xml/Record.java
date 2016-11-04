package au.edu.anu.scoap.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * MARC21 record
 * 
 * @author Genevieve Turner
 */
public class Record {
	List<ControlField> controlFields = new ArrayList<ControlField>();
	List<DataField> dataFields = new ArrayList<DataField>();
	
	@XmlElement(name="controlfield",namespace=MARCConstants.NS)
	public List<ControlField> getControlFields() {
		return controlFields;
	}
	
	public void setControlFields(List<ControlField> controlFields) {
		this.controlFields = controlFields;
	}

	@XmlElement(name="datafield",namespace=MARCConstants.NS)
	public List<DataField> getDataFields() {
		return dataFields;
	}
	
	public void setDataFields(List<DataField> dataFields) {
		this.dataFields = dataFields;
	}
	
	/**
	 * Get the control field with the specified tag
	 * 
	 * @param tag The tag
	 * @return The text values associated with the tag
	 */
	public List<String> getControlField(String tag) {
		List<String> values = new ArrayList<String>();
		for (ControlField field : controlFields) {
			if (tag.equals(field.getTag())) {
				values.add(field.getText());
			}
		}
		return values;
	}
	
	/**
	 * Get the data field value with the given tag and sub field code
	 * 
	 * @param tag The tag
	 * @param code The sub field code
	 * @return The text values
	 */
	public List<String> getDataFieldValues(String tag, String code) {
		return getDataFieldValues(tag, code, " ", " ");
	}

	/**
	 * Get the data field with the given tag, indicators, and sub field code
	 * 
	 * @param tag The tag
	 * @param code The sub field code
	 * @param indicator1 Indicator 1
	 * @param indicator2 Indicator 2
	 * @return The text values
	 */
	public List<String> getDataFieldValues(String tag, String code, String indicator1, String indicator2) {
		List<String> values = new ArrayList<String>();
		
		List<DataField> fields = getDataFields(tag, indicator1, indicator2);
		
		for (DataField field : fields) {
			for (SubField subField : field.getSubFields()) {
				if (code.equals(subField.getCode())) {
					values.add(subField.getText());
				}
			}
		}
		
		return values;
	}
	
	/**
	 * Get the data fields with the tag and indicators
	 * 
	 * @param tag The tag
	 * @param indicator1 Indicator 1
	 * @param indicator2 Indicator 2
	 * @return The data fields
	 */
	public List<DataField> getDataFields(String tag, String indicator1, String indicator2) {
		List<DataField> values = new ArrayList<DataField>();
		for (DataField field : dataFields) {
			if (tag.equals(field.getTag()) && indicator1.equals(field.getIndicator1()) 
					&& indicator2.equals(field.getIndicator2())) {
				values.add(field);
			}
		}
		return values;
	}
}
