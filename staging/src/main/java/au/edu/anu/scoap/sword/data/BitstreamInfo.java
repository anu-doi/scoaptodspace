/**
 * 
 */
package au.edu.anu.scoap.sword.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Genevieve Turner
 *
 */
public interface BitstreamInfo {
	
	public String getFilename();
	public String getFilepath();
	
	public InputStream getFile() throws IOException;
	public long getSize();
	public String getFilenameToDeposit() throws UnsupportedEncodingException;
	
	public String getMimeType() throws IOException;
}
