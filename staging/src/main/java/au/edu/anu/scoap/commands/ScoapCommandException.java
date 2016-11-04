package au.edu.anu.scoap.commands;

/**
 * Scoap3 command exception
 * 
 * @author Genevieve Turner
 */
public class ScoapCommandException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ScoapCommandException(String message) {
		super(message);
	}
}
