/**
 * 
 */
package asia.hubnet.cmis;

import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

/**
 * If we can get some error message from Alfresco, then let's use that instead in the stack trace.
 * 
 * @see AlfrescoErrorExtractor
 * @author Jevon
 *
 */
public class AlfrescoCMISRuntimeException extends CmisRuntimeException {

	private static final long serialVersionUID = 1L;
	
	public AlfrescoCMISRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Check the given {@link CmisRuntimeException}, and if we can understand an Alfresco error
	 * from it, throw an {@link AlfrescoCMISRuntimeException} instead. Otherwise, do nothing.
	 * 
	 * @param e
	 * @throws AlfrescoCMISRuntimeException
	 */
	public static void checkCmisException(CmisRuntimeException e) throws AlfrescoCMISRuntimeException {
		if (e.getErrorContent() != null && AlfrescoErrorExtractor.extractMessage(e.getErrorContent()) != null) {
			throw new AlfrescoCMISRuntimeException(AlfrescoErrorExtractor.extractMessage(e.getErrorContent()), e);
		}

	}
	
}
