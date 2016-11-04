package au.edu.anu.scoap.sword.task;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.concurrent.Callable;
import org.apache.commons.io.IOUtils;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class Md5CalcTask implements Callable<byte[]> {
	private static final String MD5 = "MD5";
	
	private InputStream fileStream;

	public Md5CalcTask(InputStream fileStream) {
		this.fileStream = fileStream;
	}

	public byte[] call() throws Exception {
		MessageDigest digester = MessageDigest.getInstance(MD5);
		try {
			byte[] buffer = new byte[8192];
			for (int nBytes = this.fileStream.read(buffer); nBytes != -1; nBytes = this.fileStream.read(buffer)) {
				digester.update(buffer, 0, nBytes);
			}
		} finally {
			IOUtils.closeQuietly(this.fileStream);
		}
		return digester.digest();
	}
}
