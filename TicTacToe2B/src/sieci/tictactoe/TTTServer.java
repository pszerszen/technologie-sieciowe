package sieci.tictactoe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Server of a game. Starts a duel and accepts new players.
 * 
 * @author Adam Szerszeń
 * 
 */
public class TTTServer {
	/**
	 * The instance of TTTGame in the class.
	 */
	private TTTGame game;
	/**
	 * The Socket instance used for adding next players.
	 */
	private Socket newPlayer;
	/**
	 * The ServerSocket instance of this server.
	 */
	private ServerSocket server;

	/**
	 * Constructor that sets up the game and accepts new players.
	 * 
	 * @param port
	 *            The port the game is running on.
	 * @param size
	 *            The size of the area.
	 * @param goal
	 *            Minimal amount of symbols required for scoring a point.
	 * @param time
	 *            Each symbol lifetime.
	 * @throws IOException
	 */
	public TTTServer(int port, int size, int goal, int time) throws IOException {
		game = new TTTGame(size, goal, time);
		server = new ServerSocket(port);
		newPlayer = server.accept();
		new Player(game, newPlayer);

		while (true) {
			newPlayer = server.accept();
			new Player(game, newPlayer);
		}
	}
}
