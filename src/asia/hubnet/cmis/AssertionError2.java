/**
 * 
 */
package asia.hubnet.cmis;


/**
 * Implements {@link AssertionError#AssertionError(String, Throwable)} - implemented
 * in Java 7. Allows an {@link AssertionError} to have a root cause.
 * 
 */
public class AssertionError2 extends AssertionError {

	private static final long serialVersionUID = 1L;

	private Throwable cause;

	/**
	 * @param string
	 * @param e
	 */
	public AssertionError2(String string, Throwable e) {
		super(string);
		this.cause = e;
	}

	@Override
	public Throwable getCause() {
		return cause;
	}
	
}
