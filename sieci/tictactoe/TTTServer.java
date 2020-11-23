package sieci.tictactoe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The class that starts the application as the Server.
 * 
 * @author Piotr Szerszeń
 */
public class TTTServer {
	/**
	 * Instance of a {@link Player} for one of the player.
	 */
	private Player p1, p2;
	/**
	 * Instance of {@link Socket} for checking for one of the player.
	 */
	private Socket p1check, p2check;
	/**
	 * Instance of {@link Socket} for one of the player.
	 */
	private Socket player1, player2;
	/**
	 * The {@link ServerSocket} instance.
	 */
	private ServerSocket server;

	public TTTServer(String[] args) throws IOException {
		int port, size, goal;
		port = size = goal = 0;

		try {
			port = Integer.parseInt(args[0]);
			size = Integer.parseInt(args[1]);
			goal = Integer.parseInt(args[2]);
		} catch (Exception e) {
			System.out.println("Podales zle argumenty");
			System.exit(-1);
		}

		server = new ServerSocket(port);
		player1 = player2 = p1check = p2check = null;

		while (true) {
			player1 = server.accept();
			p1check = server.accept();
			p1 = new Player(player1, p1check, "X");
			p1.getPlayerWriter().println("WFO");

			player2 = server.accept();
			p2check = server.accept();
			p2 = new Player(player2, p2check, "O");
			p2.getPlayerWriter().println("GO");

			new TTTServerThread(p1, p2, size, goal).start();
		}
	}

	public static void main(String[] args) throws IOException {
		new TTTServer(args);
	}
}
