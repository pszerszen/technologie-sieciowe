package sieci.tictactoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Contains and provides player's writers, readers and symbol. It makes
 * connection between clients in Servers easier. Sockets, readers and writers
 * are split for those describing moves and those informing about game's over
 * (fields containing "check" in name).
 * 
 * @author Piotr Szerszeń
 * 
 */
public class Player {
	/**
	 * Reads messages about quitting the game.
	 */
	private BufferedReader checkReader;
	/**
	 * Provides connection for the quitting game messages.
	 */
	private Socket checkSocket;
	/**
	 * To send a message about end of the game.
	 */
	private PrintWriter checkWriter;
	/**
	 * To read players move.
	 */
	private BufferedReader playerReader;
	/**
	 * Provides connection for swapping messages about game.
	 */
	private Socket playerSocket;
	/**
	 * To send a message about the next move.
	 */
	private PrintWriter playerWriter;
	/**
	 * The player's symbol;
	 */
	private String symbol;

	/**
	 * Initializes all fields and sets sockets timeout to 10s.
	 * 
	 * @param playerSocket
	 * @param checkSocket
	 * @param symbol
	 */
	public Player(Socket playerSocket, Socket checkSocket, String symbol) {
		this.playerSocket = playerSocket;
		this.checkSocket = checkSocket;
		this.symbol = symbol;

		try {
			this.playerSocket.setSoTimeout(10000);
			this.checkSocket.setSoTimeout(10000);
		} catch (SocketException ex) {
		}

		try {
			playerWriter = new PrintWriter(this.playerSocket.getOutputStream(),
					true);
			checkWriter = new PrintWriter(this.checkSocket.getOutputStream(),
					true);
			playerReader = new BufferedReader(new InputStreamReader(
					this.playerSocket.getInputStream()));
			checkReader = new BufferedReader(new InputStreamReader(
					this.checkSocket.getInputStream()));
		} catch (IOException e) {
		}
	}

	/**
	 * Closes {@link BufferedReader}, {@link BufferedWriter} and {@link Socket}.
	 * Called in case of lost the other player's signal.
	 * 
	 */
	public void closeAll() {
		try {
			checkReader.close();
			checkSocket.close();

			playerReader.close();
			playerSocket.close();
		} catch (IOException e) {
		}

		checkWriter.close();
		playerWriter.close();
	}

	/**
	 * Gives the value of checkReader object.
	 * 
	 * @return The checkReader.
	 */
	public BufferedReader getCheckReader() {
		return checkReader;
	}

	/**
	 * Gives the value of checkSocket object.
	 * 
	 * @return The checkSocket.
	 */
	public Socket getCheckSocket() {
		return checkSocket;
	}

	/**
	 * Gives the value of checkWriter object.
	 * 
	 * @return The checkWriter.
	 */
	public PrintWriter getCheckWriter() {
		return checkWriter;
	}

	/**
	 * Gives the value of playerReader object.
	 * 
	 * @return The playerReader.
	 */
	public BufferedReader getPlayerReader() {
		return playerReader;
	}

	/**
	 * Gives the value of playerSocket object.
	 * 
	 * @return The playerSocket.
	 */
	public Socket getPlayerSocket() {
		return playerSocket;
	}

	/**
	 * Gives the value of playerWriter object.
	 * 
	 * @return The playerWriter.
	 */
	public PrintWriter getPlayerWriter() {
		return playerWriter;
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
	 * Sets the new value of checkReader specified with parameter.
	 * 
	 * @param checkReader
	 *            The checkReader to set.
	 */
	public void setCheckReader(BufferedReader checkReader) {
		this.checkReader = checkReader;
	}

	/**
	 * Sets the new value of checkSocket specified with parameter.
	 * 
	 * @param checkSocket
	 *            The checkSocket to set.
	 */
	public void setCheckSocket(Socket checkSocket) {
		this.checkSocket = checkSocket;
	}

	/**
	 * Sets the new value of checkWriter specified with parameter.
	 * 
	 * @param checkWriter
	 *            The checkWriter to set.
	 */
	public void setCheckWriter(PrintWriter checkWriter) {
		this.checkWriter = checkWriter;
	}

	/**
	 * Sets the new value of playerReader specified with parameter.
	 * 
	 * @param playerReader
	 *            The playerReader to set.
	 */
	public void setPlayerReader(BufferedReader playerReader) {
		this.playerReader = playerReader;
	}

	/**
	 * Sets the new value of playerSocket specified with parameter.
	 * 
	 * @param playerSocket
	 *            The playerSocket to set.
	 */
	public void setPlayerSocket(Socket playerSocket) {
		this.playerSocket = playerSocket;
	}

	/**
	 * Sets the new value of playerWriter specified with parameter.
	 * 
	 * @param playerWriter
	 *            The playerWriter to set.
	 */
	public void setPlayerWriter(PrintWriter playerWriter) {
		this.playerWriter = playerWriter;
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

}
