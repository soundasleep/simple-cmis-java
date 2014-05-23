/**
 * 
 */
package asia.hubnet.cmis;


/**
 * Thrown when trying to set a property on an object which will most definitely
 * not have the required aspect.
 * 
 * @author jevon
 *
 */
public class CMISMissingAspectException extends CMISRuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public CMISMissingAspectException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CMISMissingAspectException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
	
	public CMISMissingAspectException(String message) {
		super(message);
	}
	
}
