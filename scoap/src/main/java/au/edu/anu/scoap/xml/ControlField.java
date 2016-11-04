package au.edu.anu.scoap.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * MARC21 Control Field
 * 
 * @author Genevieve Turner
 */
public class ControlField {
	private String tag;
	private String text;
	
	@XmlAttribute
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	@XmlValue
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
