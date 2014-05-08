/**
 * 
 */
package asia.hubnet.cmis;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

/**
 * Wraps the Apache {@link CmisObject} into a simpler form.
 * 
 * @author jevon
 *
 */
public class CMISObject {

	private CmisObject obj;
	private CMISFolder folder;

	/**
	 * @param folder the parent folder
	 * @param obj
	 */
	public CMISObject(CMISFolder folder, CmisObject obj) {
		this.folder = folder;
		this.obj = obj;
	}

	/**
	 * Returns the underlying Apache {@link CmisObject} represented by this object.
	 */
	public CmisObject getCmisObject() {
		return obj;
	}

	public CMISFolder getParentFolder() {
		return folder;
	}

	public String getName() {
		return obj.getName();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CMISObject [" + folder.getPath() + "/" + obj.getName() + "]";
	}
	
	public static final String PROPERTY_DESCRIPTION = "cm:description";

	/**
	 * Return the {@code cm:description} of the given object, or {@code null}
	 * if no such property is set.
	 * 
	 * @return
	 */
	public String getDescription() {
		Property<Object> result = obj.getProperty(PROPERTY_DESCRIPTION);
		if (result == null) return null;
		return result.getValueAsString();
	}
	
	/**
	 * Return the given property of the given object, or {@code null}
	 * if no such property is set.
	 * 
	 * @return
	 */
	public Object getProperty(String id) {
		Property<Object> result = obj.getProperty(id);
		if (result == null) return null;
		return result.getValue();
	}

	public void setDescription(String value) {
		setProperty(PROPERTY_DESCRIPTION, value);
	}

	/**
	 * Update a property on an object.
	 * 
	 * @param propertyId e.g. "cm:description", depends on the aspects applied to the object
	 * @param value
	 */
	public void setProperty(String propertyId, Object value) {

		try {
			Map<String, Object> update = new HashMap<String, Object>();
			update.put(propertyId, value);
			
			updateProperties(update);
			
		} catch (CmisRuntimeException e) {
			AlfrescoCMISRuntimeException.checkCmisException(e);
			throw e;		// or throw as normal
		}
		
	}

	/**
	 * Update a set of properties on an object. If the property change
	 * creates a new object on the repository, it is returned; otherwise, a copy
	 * of the current object is returned.
	 * 
	 * <p>
	 * (Note: As of writing, Apache's CmisObject does not redefine {@link Object#equals(Object)},
	 * so we can't really redefine equals in CMISObject either.)
	 */
	public CMISObject updateProperties(Map<String, Object> update) {
		// this method can return a new object; need to keep reference to it
		return new CMISObject(folder, obj.updateProperties(update));
	}

	public Date getLastModified() {
		return getCmisObject().getLastModificationDate().getTime();
	}

	public String getPath() {
		return getParentFolder().getPath() + "/" + getName();
	}

	/**
	 * Delete this object and all of its versions.
	 * If this object is a document, the whole version series is deleted.
	 * 
	 * @throws CMISConstraintException if the object could not be deleted due to a {@link CmisConstraintException}
	 */
	public void delete() throws CMISConstraintException {
		try {
			getCmisObject().delete();
		} catch (CmisConstraintException e) {
			throw new CMISConstraintException("Could not delete " + this + ": " + e.getMessage(), e);
		}
	}

}
