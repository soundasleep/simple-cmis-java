/**
 * 
 */
package asia.hubnet.cmis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tries to extract error messages from HTML error pages.
 * Not strictly necessary to always work, but it should help 
 * administrators (so they don't need to watch both the server-side
 * and client-side logs).
 * 
 * @author Jevon
 *
 */
public class AlfrescoErrorExtractor {

	/**
	 * Try to parse out the server error message from the HTML error content.
	 * This doesn't need to work perfectly all of the time, it's just to help
	 * administrators in the most common scenarios.
	 * 
	 * @param errorContent
	 * @return the found message, or {@code null} if none could be found
	 */
	public static String extractMessage(String errorContent) {
		// we're looking for the following line:
        //      <tr><td><b>Message:</b></td><td>03230001 Wrapped Exception (with status template): 03230033 Failed to execute script 'classpath*:alfresco/templates/webscripts/org/alfre
		// sco/cmis/children.post.atom.js': 03230032 Access Denied.  You do not have the appropriate permissions to perform this operation.</td></tr>
		
		Pattern pattern = Pattern.compile("Message:(<[^>]+>)+([^<]+)<");
		Matcher matcher = pattern.matcher(errorContent);
		if (matcher.find()) {
			return matcher.group(2);
		}
		
		return null;
	}
	
}
