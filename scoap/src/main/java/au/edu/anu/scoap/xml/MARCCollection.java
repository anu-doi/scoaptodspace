package au.edu.anu.scoap.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Scoap Collection wrapper of MARC21 records
 * 
 * @author Genevieve Turner
 */
@XmlRootElement(name="collection",namespace=MARCConstants.NS)
public class MARCCollection {
	List<Record> records;

	@XmlElement(name="record",namespace=MARCConstants.NS)
	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}
}
