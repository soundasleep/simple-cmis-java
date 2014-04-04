/**
 * 
 */
package asia.hubnet.cmis;

import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;

/**
 * Rather than just throwing a {@link CmisInvalidArgumentException} 
 * (which is an unchecked {@link RuntimeException}), we want to explicitly
 * catch invalid content types.
 * 
 * @author jevon
 *
 */
public class CMISInvalidContentTypeException extends CMISException {
	
	private static final long serialVersionUID = 1L;
	
	public CMISInvalidContentTypeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public CMISInvalidContentTypeException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
	
	public CMISInvalidContentTypeException(String message) {
		super(message);
	}
	
}
