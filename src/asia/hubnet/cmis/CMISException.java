/**
 * 
 */
package asia.hubnet.cmis;

/**
 * Rather than throwing {@link RuntimeException}s when files aren't found,
 * it is a much better idea to have the exceptions explicitly. 
 * @author jevon
 *
 */
public class CMISException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public CMISException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CMISException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
	
	public CMISException(String message) {
		super(message);
	}
	
}