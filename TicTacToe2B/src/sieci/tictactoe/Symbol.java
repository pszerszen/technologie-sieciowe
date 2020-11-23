package sieci.tictactoe;

/**
 * An util class that contains info. about coordinates and remaining time of a
 * symbol.
 * 
 * @author Adam Szerszeń
 * 
 */
public class Symbol {
	/**
	 * The symbol itself.
	 */
	private String symbol;
	/**
	 * Reaminning time.
	 */
	private int t;
	/**
	 * A symbol coordinate.
	 */
	@SuppressWarnings("unused")
	private int x, y;

	public Symbol(int x, int y) {
		this.x = x;
		this.y = y;
		symbol = " ";
		t = 0;
	}

	/**
	 * Gives the value of symbol object.
	 * 
	 * @return The symbol.
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Gives the value of t object.
	 * 
	 * @return The t.
	 */
	public int getT() {
		return t;
	}

	/**
	 * Transforms the symbol to lower case.
	 */
	public void lowerSymbol() {
		symbol = symbol.toLowerCase();
	}

	/**
	 * Decrements t value. When it reaches 0, turns symbol into " ".
	 */
	public void lowerT() {
		if (t == 0) {
			symbol = " ";
			return;
		}
		t--;
	}

	/**
	 * Sets the new value of symbol specified with parameter.
	 * 
	 * @param symbol
	 *            The symbol to set.
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Sets the new value of t specified with parameter.
	 * 
	 * @param t
	 *            The t to set.
	 */
	public void setT(int t) {
		this.t = t;
	}
}
