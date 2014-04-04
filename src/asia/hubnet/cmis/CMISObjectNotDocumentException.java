/**
 * 
 */
package asia.hubnet.cmis;

public class CMISObjectNotDocumentException extends CMISException {

	private static final long serialVersionUID = 1L;
	
	public CMISObjectNotDocumentException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CMISObjectNotDocumentException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
	
	public CMISObjectNotDocumentException(String message) {
		super(message);
	}
	
}