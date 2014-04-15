/**
 * 
 */
package asia.hubnet.cmis;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;


/**
 * @author jevon
 *
 */
public class CMISInterface {

	private CMISConnectionInformation config;
	private Session session = null;
	
	public CMISInterface(CMISConnectionInformation config) {
		this.config = config;
	}

	/**
	 * @param folderPath
	 * @return
	 * @throws CMISObjectNotFoundException 
	 */
	public CMISFolder getFolderByPath(String folderPath) throws CMISObjectNotFoundException {
		connectSession();
		
		try {
			CmisObject object = session.getObjectByPath(folderPath);
			Folder folder = (Folder) object;
	
			return new CMISFolder(this, folder);
		} catch (CmisRuntimeException e) {
			AlfrescoCMISRuntimeException.checkCmisException(e);
			throw e;		// or throw as normal
		} catch (CmisObjectNotFoundException e) {
			throw new CMISObjectNotFoundException(e);
		}
	}

	/**
	 * @param folderPath
	 * @return
	 */
	public boolean hasFolderByPath(String folderPath) {
		try {
			getFolderByPath(folderPath);
			return true;
		} catch (CmisRuntimeException e) {
			AlfrescoCMISRuntimeException.checkCmisException(e);
			throw e;		// or throw as normal
		} catch (CMISObjectNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Does not reconnect the session if it is already connected.
	 * @see #resetSession()
	 */
	public void connectSession() {
		try {
			
			if (session != null)
				return;
			
			Map<String, String> parameter = new HashMap<String, String>();
	
			// Set the user credentials
			parameter.put(SessionParameter.USER, config.getUser());
			parameter.put(SessionParameter.PASSWORD, config.getPassword());
	
			// Specify the connection settings
			parameter.put(SessionParameter.ATOMPUB_URL, config.getURL());
			parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
	
			// Set the alfresco object factory
			// used to add Aspect support to CMIS objects
			parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
			
			// Create a session
			SessionFactory factory = SessionFactoryImpl.newInstance();
			session = factory.getRepositories(parameter).get(0).createSession();

		} catch (CmisRuntimeException e) {
			AlfrescoCMISRuntimeException.checkCmisException(e);
			throw e;		// or throw as normal
		}
			
	}
	
	public void resetSession() {
		session = null;
	}

	/**
	 * @see #connectSession()
	 * @see #resetSession()
	 * @return null if the session has not been connected yet ({@link #connectSession()}).
	 */
	public Session getSession() {
		return session;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CMISInterface [config=" + config + "]";
	}

	/**
	 * @param string the object reference to load as a folder
	 * @return
	 * @throws CMISObjectNotFoundException 
	 */
	public CMISFolder getFolderByReference(String ref) throws CMISObjectNotFoundException {
		connectSession();
		
		try {
			CmisObject object = session.getObject(ref);
			Folder folder = (Folder) object;
	
			return new CMISFolder(this, folder);
		} catch (CmisRuntimeException e) {
			AlfrescoCMISRuntimeException.checkCmisException(e);
			throw e;		// or throw as normal
		} catch (CmisObjectNotFoundException e) {
			throw new CMISObjectNotFoundException(e);
		}
	}
	
	/**
	 * Perform a raw CMIS query, e.g. 'SELECT * FROM cmis:document'.
	 * 
	 * @param query e.g. 'SELECT * FROM cmis:document'.
	 * @return
	 */
	public ItemIterable<QueryResult> query(String query) {
		connectSession();
		
		return session.query(query, false /* searchAllVersions */);
	}
	
}
