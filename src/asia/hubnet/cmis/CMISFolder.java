/**
 * 
 */
package asia.hubnet.cmis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;

/**
 * Represents a CMIS folder.
 * Wraps the Apache {@link Folder} into a simpler form.
 * 
 * @author jevon
 * @see #getFolder()
 * 
 */
public class CMISFolder {

	private CMISInterface cmis;
	private Folder folder;

	/**
	 * @param cmis 
	 * @param folder
	 */
	public CMISFolder(CMISInterface cmis, Folder folder) {
		this.cmis = cmis;
		this.folder = folder;
	}
	
	/**
	 * Returns the underlying Apache {@link Folder} represented by this object.
	 */
	public Folder getFolder() {
		return folder;
	}

	/**
	 * List all of the children of this folder.
	 * Does not include subfolders: see {@link #getSubfolders()}.
	 * 
	 * @return
	 */
	public Iterable<CMISObject> getChildren(int maxItemsPerPage, int skipCount) {
		OperationContext operationContext = cmis.getSession().createOperationContext();
		operationContext.setMaxItemsPerPage(maxItemsPerPage);
		
		ItemIterable<CmisObject> children = folder.getChildren(operationContext);
		ItemIterable<CmisObject> page = children.skipTo(skipCount).getPage();

		final Iterator<CmisObject> pageItems = page.iterator();
		return new Iterable<CMISObject>() {

			@Override
			public Iterator<CMISObject> iterator() {
				return new Iterator<CMISObject>() {

					@Override
					public boolean hasNext() {
						return pageItems.hasNext();
					}

					@Override
					public CMISObject next() {
						return new CMISObject(CMISFolder.this, pageItems.next());
					}

					@Override
					public void remove() {
						pageItems.remove();
					}
					
				};
			}
			
		};
	}
	
	private final int DEFAULT_PAGE_SIZE = 16;
	
	/**
	 * List all of the children of this folder.
	 * Does not include subfolders: see {@link #getSubfolders()}.
	 * 
	 * @return
	 */
	public Iterable<CMISObject> getChildren() {
		return getChildren(DEFAULT_PAGE_SIZE, 0);
	}

	/**
	 * List all of the direct subfolders of this folder.
	 *
	 * @see #getChildren()
	 * @return
	 */
	public Iterable<CMISFolder> getSubfolders(int maxItemsPerPage) {
		OperationContext operationContext = cmis.getSession().createOperationContext();
		operationContext.setMaxItemsPerPage(maxItemsPerPage);
		
		List<Tree<FileableCmisObject>> children = folder.getDescendants(1 /* subfolder depth */, operationContext);
		
		List<CMISFolder> result = new ArrayList<CMISFolder>();
		for (Tree<FileableCmisObject> t : children) {
			if (t.getItem() instanceof Folder) {
				result.add(new CMISFolder(cmis, (Folder) t.getItem()));
			}
		}
		
		return result;
	}
	
	/**
	 * List all of the direct subfolders of this folder.
	 * 
	 * @return
	 */
	public Iterable<CMISFolder> getSubfolders() {
		return getSubfolders(DEFAULT_PAGE_SIZE);
	}

	public String getName() {
		return folder.getName();
	}

	/**
	 * Create a new {@link CMISDocument} with the given name, content, content type
	 * and description (for Alfresco repositories).
	 * 
	 * @param name filename
	 * @param content e.g. "Hello".getBytes("UTF-8")
	 * @param contentType e.g. "text/plain; charset=UTF-8"
	 * @param description optional document description; may be {@code null}
	 * @return the created {@link CMISDocument}
	 * @throws CMISInvalidContentTypeException if contentType is {@code null}
	 * @throws IOException 
	 */
	public CMISDocument upload(String name, byte[] content, String contentType,
			String description) throws IOException, CMISInvalidContentTypeException {

		InputStream stream = null;
		try {
			stream = new ByteArrayInputStream(content);
			return upload(name, stream, content.length, contentType, description);
		} finally {
			if (stream != null) stream.close();
		}		
	}
	
	/**
	 * Create a new {@link CMISDocument} with the given name, content, content type
	 * and description (for Alfresco repositories).
	 * Does not close the given stream.
	 * 
	 * @param name filename
	 * @param stream the stream to load data from; this stream is not closed
	 * @param length the length of the stream
	 * @param contentType e.g. "text/plain; charset=UTF-8"
	 * @param description optional document description; may be {@code null}
	 * @return the created {@link CMISDocument}
	 * @throws CMISInvalidContentTypeException if contentType is {@code null}
	 * @throws IOException 
	 */
	public CMISDocument upload(String name, InputStream stream, long length, String contentType,
			String description) throws IOException, CMISInvalidContentTypeException {

		try {
		
			if (contentType == null)
				throw new CMISInvalidContentTypeException("Content-Type cannot be null");
	
			Map<String, Object> properties = new HashMap<String, Object>();
			if (description == null) {
				// TODO should the Object type ID be a parameter too?
				properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
				properties.put(PropertyIds.NAME, name);
			} else {
				properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:cm:titled");
				properties.put(PropertyIds.NAME, name);
				properties.put("cm:description", description); // P:cm:titled adds cm:description field
			}
	
			// content
			ContentStream contentStream = new ContentStreamImpl(name, BigInteger.valueOf(length), contentType, stream);
	
			// create a major version
			Document newDoc = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
			
			return new CMISDocument(this, newDoc);
			
		} catch (CmisRuntimeException e) {
			AlfrescoCMISRuntimeException.checkCmisException(e);
			throw e;		// or throw as normal
		}
		
	}
	/**
	 * Create a new {@link CMISDocument} with the given name, content, content type
	 * and description (for Alfresco repositories).
	 * The content type is assumed to be plain text, and the character set UTF-8.
	 * 
	 * @param name filename
	 * @param content e.g. "Hello", assumed to be in UTF-8
	 * @param description optional document description; may be {@code null}
	 * @return the created {@link CMISDocument}
	 * 
	 * @throws IOException 
	 * @throws UnsupportedEncodingException if UTF-8 is not a valid encoding
	 */
	public CMISDocument upload(String name, String content,
			String description) throws UnsupportedEncodingException, IOException {
		try {
			return upload(name, content.getBytes("UTF-8"), "text/plain; charset=UTF-8", description);
		} catch (CMISInvalidContentTypeException e) {
			// should never occur
			throw new AssertionError2("Content-type should never be null", e);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CMISFolder [" + getPath() + "]";
	}

	/**
	 * Return the full path of this folder including this folder name.
	 * 
	 * @return
	 */
	public String getPath() {
		return folder.getPath();
	}

	/**
	 * Does the given file exist in this folder?
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasObject(String name) {
		try {
			getObject(name);
			return true;
		} catch (CMISObjectNotFoundException e1) {
			return false;
		}
	}
	
	/**
	 * Get the given file in this folder, or throws a 
	 * {@link CMISObjectNotFoundException}.
	 * 
	 * @param name
	 * @return
	 * @throws CMISObjectNotFoundException 
	 */
	public CMISObject getObject(String name) throws CMISObjectNotFoundException {
		try {
			CmisObject obj = cmis.getSession().getObjectByPath(getPath() + "/" + name);
			return new CMISObject(this, obj);
		} catch (CmisObjectNotFoundException e) {
			throw new CMISObjectNotFoundException(e);
		}
	}
	
	/**
	 * Does the given document exist in this folder?
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasDocument(String name) {
		try {
			getDocument(name);
			return true;
		} catch (CMISObjectNotFoundException e1) {
			return false;
		} catch (CMISObjectNotDocumentException e) {
			return false;
		}
	}
	
	/**
	 * Get the given document in this folder, or throws a 
	 * {@link CMISObjectNotFoundException}.
	 * 
	 * @param name
	 * @return
	 * @throws CMISObjectNotFoundException 
	 * @throws CMISObjectNotDocumentException 
	 */
	public CMISDocument getDocument(String name) throws CMISObjectNotFoundException, CMISObjectNotDocumentException {
		try {
			CmisObject obj = cmis.getSession().getObjectByPath(folder.getPath() + "/" + name);
			if (obj instanceof Document) {
				return new CMISDocument(this, (Document) obj);
			} else {
				throw new CMISObjectNotDocumentException("Object '" + obj + "' was not a Document");
			}
		} catch (CmisObjectNotFoundException e) {
			throw new CMISObjectNotFoundException(e);
		}
	}

	/**
	 * One way that seems to work for refreshing is to upload a new document,
	 * and then delete it.
	 * 
	 * @throws IOException 
	 * @throws CMISConstraintException 
	 */
	public void forceRefresh() throws IOException, CMISConstraintException {
		String tempName = getTemporaryFile("forceRefresh");
		CMISDocument doc;
		try {
			doc = upload(tempName, new Date().toString(), null);
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e);
		}
		doc.delete();
	}
	
	/**
	 * Get a temporary filename that doesn't exist in this folder.
	 * 
	 * @return
	 */
	public String getTemporaryFile(String prefix) {
		for (long i = System.currentTimeMillis(); i >= 0; i--) {
			String name = "temp" + prefix + "." + i;
			if (!hasObject(name)) {
				return name;
			}
		}
		throw new AssertionError("Could not find a single filename that doesn't exist");
	}

	/**
	 * Get the given subfolder, or {@code null} if no such subfolder exists.
	 * 
	 * @param name
	 * @return
	 */
	public CMISFolder getSubfolder(String name) {
		for (CMISFolder folder : getSubfolders()) {
			if (folder.getName().equals(name)) {
				return folder;
			}
		}
		
		// no such subfolder
		return null;
	}

	/**
	 * Create the given subfolder, and return the new {@link CMISFolder}.
	 * 
	 * @param name
	 * @return
	 */
	public CMISFolder createSubfolder(String name) {
		
		try {
			
			Map<String, String> props = new HashMap<String, String>();
			props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
			props.put(PropertyIds.NAME, name);
			Folder newFolder = folder.createFolder(props);
			
			return new CMISFolder(cmis, newFolder);
			
		} catch (CmisRuntimeException e) {
			AlfrescoCMISRuntimeException.checkCmisException(e);
			throw e;		// or throw as normal
		}

	}

}
