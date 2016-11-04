package au.edu.anu.scoap.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * MARC21 sub field
 * 
 * @author Genevieve Turner
 */
public class SubField {
	private String code;
	private String text;
	
	@XmlAttribute
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	@XmlValue
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
