/**
 * 
 */
package asia.hubnet.cmis;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * Guesses content types based on their file extension.
 * 
 * @author Jevon
 *
 */
public class ContentTypeGuesser {

	/**
	 * Guess the content type of the given file, or {@code null}
	 * if none can be found.
	 * <p>
	 * Relies on {@link URLConnection#getFileNameMap()} and the 
	 * {@link FileNameMap}.
	 * 
	 * @param file
	 * @return
	 */
	public static String guessContentType(File file) {
		FileNameMap map = URLConnection.getFileNameMap();
		String contentType = map.getContentTypeFor(file.getName());
		
		if (contentType == null) {
			// manual fixes for some content types that aren't present in JRE7
			if (file.getName().toLowerCase().endsWith(".csv")) {
				return "text/csv";
			}
			if (file.getName().toLowerCase().endsWith(".log")) {
				return "text/plain";
			}
			
		}
		
		return contentType;
	}

}
