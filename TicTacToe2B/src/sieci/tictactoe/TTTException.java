package sieci.tictactoe;

/**
 * Exception called when the duel is to be end.
 * 
 * @author Adam Szerszeń
 */
public class TTTException extends Exception {
	private static final long serialVersionUID = -3595326091706434712L;

	/**
	 * Message of an error.
	 */
	private String message;

	/**
	 * Calls super creator and sets m as message text.
	 * 
	 * @param m
	 *            The message of an error.
	 */
	public TTTException(String m) {
		super(m);
		message = m;
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}

}