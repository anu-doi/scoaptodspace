package au.edu.anu.scoap.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * MARC21 Data Field
 * 
 * @author Genevieve Turner
 */
public class DataField {
	private String tag;
	private String indicator1;
	private String indicator2;
	private List<SubField> subFields;
	
	@XmlAttribute(name="tag")
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}

	@XmlAttribute(name="ind1")
	public String getIndicator1() {
		return indicator1;
	}

	public void setIndicator1(String indicator1) {
		this.indicator1 = indicator1;
	}

	@XmlAttribute(name="ind2")
	public String getIndicator2() {
		return indicator2;
	}

	public void setIndicator2(String indicator2) {
		this.indicator2 = indicator2;
	}

	@XmlElement(name="subfield",namespace=MARCConstants.NS)
	public List<SubField> getSubFields() {
		return subFields;
	}

	public void setSubFields(List<SubField> subFields) {
		this.subFields = subFields;
	}

//	@XmlTransient
	public String getSubfieldValue(String tag) {
		for (SubField field : subFields) {
			if (tag.equals(field.getCode())) {
				return field.getText();
			}
		}
		return null;
	}
}
