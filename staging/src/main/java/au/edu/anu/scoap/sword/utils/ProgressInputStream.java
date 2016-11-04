package au.edu.anu.scoap.sword.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class ProgressInputStream extends FilterInputStream {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProgressInputStream.class);
	
	public static final String PERCENT_COMPLETE_PROPNAME = "percentComplete";
	
	private final PropertyChangeSupport propertyChangeSupport;
	private final long totalBytes;
	private volatile long totalBytesRead;

	public ProgressInputStream(InputStream in, long totalBytes) {
		super(in);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.totalBytes = totalBytes;
	}

	public long getTotalBytes() {
		return this.totalBytes;
	}

	public long getTotalBytesRead() {
		return this.totalBytesRead;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		this.propertyChangeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		this.propertyChangeSupport.removePropertyChangeListener(l);
	}

	public int read() throws IOException {
		int b = super.read();
		updateProgress(1L);
		return b;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return (int) updateProgress(super.read(b, off, len));
	}

	public long skip(long n) throws IOException {
		return updateProgress(super.skip(n));
	}

	public void mark(int readlimit) {
		throw new UnsupportedOperationException();
	}

	public void reset() throws IOException {
		throw new UnsupportedOperationException();
	}

	public boolean markSupported() {
		return false;
	}

	private long updateProgress(long numBytesRead) {
		if (numBytesRead > 0L) {
			long oldTotalNumBytesRead = this.totalBytesRead;
			this.totalBytesRead += numBytesRead;
			int oldPercentComplete = (int) (oldTotalNumBytesRead * 100L / this.totalBytes);
			int newPercentComplete = (int) (this.totalBytesRead * 100L / this.totalBytes);
			this.propertyChangeSupport.firePropertyChange("percentComplete", oldPercentComplete, newPercentComplete);
		}
		return numBytesRead;
	}
}