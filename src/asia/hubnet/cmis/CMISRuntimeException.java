/**
 * 
 */
package asia.hubnet.cmis;

/**
 * A {@link RuntimeException} for CMIS errors.
 * 
 * @author Jevon
 *
 */
public class CMISRuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public CMISRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CMISRuntimeException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
	
	public CMISRuntimeException(String message) {
		super(message);
	}
	
}
