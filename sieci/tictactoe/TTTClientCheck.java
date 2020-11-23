package sieci.tictactoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The Client thread. Mostly used for checking if game is still on.
 * 
 * @author Adam Szerszeń
 */
public class TTTClientCheck extends Thread {
	/**
	 * Game where client is taking place in.
	 */
	private TTTGame game;
	/**
	 * For reading messages from the opponent.
	 */
	private BufferedReader reader;
	/**
	 * Server's socket where client is playing on.
	 */
	private Socket server;
	/**
	 * Says if game is on.
	 */
	private boolean status;
	/**
	 * For sending messages.
	 */
	private PrintWriter writer;

	public TTTClientCheck(Socket server, TTTGame game) {
		this.game = game;
		this.server = server;
		try {
			reader = new BufferedReader(new InputStreamReader(
					this.server.getInputStream()));
			writer = new PrintWriter(this.server.getOutputStream(), true);
		} catch (IOException e) {
		}
		status = true;
	}

	/**
	 * Closes {@link BufferedReader}, {@link BufferedWriter} and {@link Socket}.
	 * Called in case of lost the other player's signal.
	 */
	public void closeAll() {
		try {
			reader.close();
			server.close();
			writer.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Runs the thread that repeats until game's over or lost connection.
	 */
	@Override
	public void run() {
		while (status)
			try {
				String question = reader.readLine();
				if (question.equals("QUIT"))
					break;
				else
					writer.println("YES");
			} catch (IOException | NullPointerException e) {
				closeAll();
				game.closeAll(1);
			}
		closeAll();
	}
}
