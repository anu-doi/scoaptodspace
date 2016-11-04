package au.edu.anu.scoap.sword.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlBitstreamInfo implements BitstreamInfo {
	static final Logger LOGGER = LoggerFactory.getLogger(UrlBitstreamInfo.class);
	
	private URL url;
	private String filenameToDeposit;
	private URLConnection connection;
	
	public UrlBitstreamInfo(String urlPath, String filenameToDeposit) {
		try {
			LOGGER.info("Url path: {}", urlPath);
			this.url = new URL(urlPath);
		
		}
		catch (MalformedURLException e) {
			LOGGER.error("Exception creating url", e);
			this.url = null;
		}
		this.filenameToDeposit = filenameToDeposit;
	}
	
	public UrlBitstreamInfo(URL url, String filenameToDeposit) {
		this.url = url;
		this.filenameToDeposit = filenameToDeposit;
	}
	
	public boolean hasMalformedURL() {
		if (url != null) {
			return false;
		}
		return true;
	}
	
	public String getFilename() {
		return url.toString();
	}
	
	public long getSize() {
		return -1;
	}

	@Override
	public String getFilepath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private URLConnection getURLConnection() throws IOException {
		if (connection == null) {
			connection = url.openConnection();
		}
		return connection;
	}

	@Override
	public InputStream getFile() throws IOException {
		try {
			URLConnection conn = getURLConnection();
			return conn.getInputStream();
		}
		catch (IOException e) {
			LOGGER.error("Unable to open connection", e);
		}
		return null;
	}

	@Override
	public String getFilenameToDeposit() throws UnsupportedEncodingException {
		String filename = filenameToDeposit;
		filename = URLEncoder.encode(filename, "UTF-8");
		return filename;
	}

	@Override
	public String getMimeType() throws IOException {
		URLConnection conn = getURLConnection();
		return conn.getContentType();
	}
}
