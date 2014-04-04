/**
 * 
 */
package asia.hubnet.cmis;


/**
 * Contains the configuration information for a CMIS connection
 * and the watching configuration.
 * 
 * @author jevon
 *
 */
public class CMISConfiguration implements CMISConnectionInformation {

	private String user;
	private String password;
	private String url;
	
	@Override
	public String getUser() {
		return user;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String getURL() {
		return url;
	}

	public void setUser(String string) {
		user = string;
	}
	public void setPassword(String string) {
		password = string;
	}
	public void setURL(String string) {
		url = string;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CMISConfiguration [user=" + user + ", password=" + password
				+ ", url=" + url + "]";
	}
	
}
