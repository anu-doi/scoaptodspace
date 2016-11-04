package au.edu.anu.scoap.sword.exception;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class WorkflowException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorkflowException() {
	}

	public WorkflowException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkflowException(String message) {
		super(message);
	}

	public WorkflowException(Throwable cause) {
		super(cause);
	}
}