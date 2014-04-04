/**
 * 
 */
package asia.hubnet.cmis.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import asia.hubnet.cmis.AlfrescoErrorExtractor;

/**
 * Tests that we can extract error messages from HTML error pages.
 * Not strictly necessary to always work, but it should help 
 * administrators (so they don't need to watch both the server-side
 * and client-side logs).
 * 
 * @author jevon
 *
 */
public class TestExtractErrorMessage {

	@Test
	public void testExtract() throws IOException {
		File f = new File("src/asia/hubnet/cmis/tests/resources/errorContent.html");
		// read into string
		StringBuffer buf = new StringBuffer();
		FileReader fr = new FileReader(f);
		char[] b = new char[1024];
		int c;
		while ((c = fr.read(b)) != -1) {
			buf.append(b, 0, c);
		}
		fr.close();
		
		assertEquals("03230001 Wrapped Exception (with status template): 03230033 Failed to execute script 'classpath*:alfresco/templates/webscripts/org/alfresco/cmis/children.post.atom.js': 03230032 Access Denied.  You do not have the appropriate permissions to perform this operation.",
				AlfrescoErrorExtractor.extractMessage(buf.toString()));
	}
	
	@Test
	public void testExtractInvalid() throws IOException {
		String input = "Hello, world!";
		
		assertNull(AlfrescoErrorExtractor.extractMessage(input));
	}
	
}
