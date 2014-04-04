/**
 * 
 */
package asia.hubnet.cmis;

public class CMISObjectNotFoundException extends CMISException {

	private static final long serialVersionUID = 1L;
	
	public CMISObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CMISObjectNotFoundException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
	
	public CMISObjectNotFoundException(String message) {
		super(message);
	}
	
}