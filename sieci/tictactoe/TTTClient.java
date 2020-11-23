package sieci.tictactoe;

import java.io.IOException;
import java.net.Socket;

/**
 * The class that starts the application as Client
 * 
 * @author Adam Szerszeń
 */
public class TTTClient {
	private Socket clientSocket, checkSocket;
	private TTTGame game;

	public TTTClient(String[] args) {
		clientSocket = checkSocket = null;

		try {
			clientSocket = new Socket(args[0], Integer.parseInt(args[1]));
			checkSocket = new Socket(args[0], Integer.parseInt(args[1]));
		} catch (IOException ex) {
			System.out.println("Nie znaleziono serwera");
			System.exit(-1);
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			System.out.println("Podales zle argumenty");
			System.exit(-1);
		}
		game = new TTTGame(clientSocket, checkSocket);
		game.initAll();
	}

	public static void main(String[] args) throws IOException {
		new TTTClient(args);
	}
}
