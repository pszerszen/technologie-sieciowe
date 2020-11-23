package sieci.tictactoe;

/**
 * Exception called when the duel is to be end.
 * 
 * @author Piotr Szerszeń
 * 
 */
public class TTTException extends Exception {
	private static final long serialVersionUID = -3595326091706434712L;

	private String message;

	public TTTException() {
		super();
		message = "lost signal...";
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}

}
