/**
 * 
 */
package asia.hubnet.cmis;

/**
 * Contains the configuration information for a CMIS connection.
 * 
 * @author jevon
 *
 */
public interface CMISConnectionInformation {

	public String getUser();
	
	public String getPassword();
	
	public String getURL();

}
