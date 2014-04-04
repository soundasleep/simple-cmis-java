/**
 * 
 */
package asia.hubnet.cmis.tests;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import asia.hubnet.cmis.ContentTypeGuesser;

/**
 * Tests the content type guessing framework.
 * 
 * @author jevon
 *
 */
public class TestContentTypes {

	@Test
	public void testCSV() {
		assertEquals("text/csv", ContentTypeGuesser.guessContentType(new File("test.csv")));
	}
	
	@Test
	public void testPlaintext() {
		assertEquals("text/plain", ContentTypeGuesser.guessContentType(new File("test.txt")));
	}
	
	@Test
	public void testPNG() {
		assertEquals("image/png", ContentTypeGuesser.guessContentType(new File("test.png")));
	}
	
	@Test
	public void testHTML() {
		assertEquals("text/html", ContentTypeGuesser.guessContentType(new File("test.html")));
	}
	
	@Test
	public void testPDF() {
		assertEquals("application/pdf", ContentTypeGuesser.guessContentType(new File("test.pdf")));
	}
	
	@Test
	public void testDoesntExist() {
		assertNull(ContentTypeGuesser.guessContentType(new File("test.a-file-type-that-should-never-exist")));
	}
	
}
