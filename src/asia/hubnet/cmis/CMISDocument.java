/**
 * 
 */
package asia.hubnet.cmis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;


/**
 * Wraps the Apache {@link Document} into a simpler form.
 * 
 * @author jevon
 *
 */
public class CMISDocument extends CMISObject {

	public CMISDocument(CMISFolder folder, Document newDoc) {
		super(folder, newDoc);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CMISDocument [" + super.toString() + "]";
	}

	/**
	 * Replace/overwrite the contents of the given document with the new content.
	 * 
	 * @param content e.g. "Hello".getBytes("UTF-8")
	 * @param contentType e.g. "text/plain; charset=UTF-8"
	 * @throws IOException 
	 * @throws CMISInvalidContentTypeException if contentType is {@code null}
	 */
	public void replaceContents(byte[] content, String contentType) throws IOException, CMISInvalidContentTypeException {
		InputStream stream = null;
		try {
			stream = new ByteArrayInputStream(content);
			replaceContents(stream, content.length, contentType);
		} finally {
			if (stream != null) stream.close();
		}
	}
	
	/**
	 * Replace/overwrite the contents of the given document with the new content.
	 * 
	 * The content type is assumed to be plain text, and the character set UTF-8.
	 * 
	 * @param content e.g. "Hello", assumed to be in UTF-8
	 * 
	 * @throws UnsupportedEncodingException if UTF-8 is not a valid encoding
	 * @throws IOException 
	 */
	public void replaceContents(String content) throws UnsupportedEncodingException, IOException {
		try {
			replaceContents(content.getBytes("UTF-8"), "text/plain; charset=UTF-8");
		} catch (CMISInvalidContentTypeException e) {
			// this should never happen; contentType is not null
			throw new AssertionError2("Content-type can never be null", e);
		}
	}

	/**
	 * Replace/overwrite the contents of the given document with the new content 
	 * within the given stream.
	 * Does not close the stream.
	 * 
	 * @param stream
	 * @param length
	 * @param contentType
	 * @throws CMISInvalidContentTypeException if contentType is {@code null}
	 */
	public void replaceContents(InputStream stream, long length,
			String contentType) throws CMISInvalidContentTypeException {
		
		if (contentType == null)
			throw new CMISInvalidContentTypeException("Content-Type cannot be null");
		
		Document doc = (Document) getCmisObject();
		ContentStream contentStream = new ContentStreamImpl(getName(), BigInteger.valueOf(length), contentType, stream);
		doc.setContentStream(contentStream, true);
		
	}

	/**
	 * Move this given document into the given folder. Does not apply to
	 * {@link CMISObject}s because {@link CmisObject} does not define a move method.
	 * May invalidate previously held object IDs, so returns the new CMISDocument.
	 * 
	 * @param dest
	 * @return 
	 * @throws CMISObjectNotDocumentException 
	 * @throws CMISObjectNotFoundException if the moved file could not be found
	 */
	public CMISDocument move(CMISFolder dest) throws CMISObjectNotFoundException, CMISObjectNotDocumentException {
		Document doc = (Document) getCmisObject();
		doc.move(getParentFolder().getFolder(), dest.getFolder());
		
		return dest.getDocument(doc.getName());
	}

	/**
	 * Rename this given document to this new name.
	 * May invalidate previously held object IDs, so returns the new CMISDocument.
	 * 
	 * @param newName
	 * @return 
	 * @throws CMISObjectNotDocumentException if the renamed file was not a document
	 * @throws CMISObjectNotFoundException if the renamed file could not be found
	 */
	public CMISDocument rename(String newName) throws CMISObjectNotFoundException, CMISObjectNotDocumentException {
		Map<String, String> newProps = new HashMap<String, String>();
		newProps.put(PropertyIds.NAME, newName);
		getCmisObject().updateProperties(newProps, true);
		
		return getParentFolder().getDocument(newName);
	}

}
