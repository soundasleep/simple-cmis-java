/**
 * 
 */
package asia.hubnet.cmis;

public class CMISConstraintException extends CMISException {

	private static final long serialVersionUID = 1L;
	
	public CMISConstraintException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CMISConstraintException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
	
	public CMISConstraintException(String message) {
		super(message);
	}
	
}