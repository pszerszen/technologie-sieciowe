package sieci.tictactoe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The class representing the game from the perspective of a Client.
 * 
 * @author Piotr Szerszeń
 * 
 */
public class ClientGame extends Thread {
	/**
	 * For receiving messages.
	 */
	private ObjectInputStream inputStream;
	/**
	 * Map of the game area.
	 */
	private String[][] map;
	/**
	 * For sending messages.
	 */
	private ObjectOutputStream outputStream;
	/**
	 * Size of the game area.
	 */
	private int size;
	/**
	 * The instance of Socket in the class.
	 */
	private Socket socket;
	/**
	 * The parameter that defines when to stop trying receiving messages
	 * (received message objects are null).
	 */
	private boolean status;
	/**
	 * The symbol of players marks on the area.
	 */
	private String symbol;

	/**
	 * Initializes the game from the player's (Client's) perspective.
	 * 
	 * @param ip
	 *            IP address Client's Socket will be using.
	 * @param port
	 *            The port the game is running on.
	 * @param nick
	 *            Player's nick.
	 * @throws TTTException
	 */
	public ClientGame(String ip, int port, String nick) throws TTTException {
		try {
			socket = new Socket(ip, port);
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new TTTException("Nie mozna znalezc serwera");
		}

		status = true;

		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			outputStream.writeObject(nick);
		} catch (IOException ex) {}

		String stat = null;

		try {
			stat = (String) inputStream.readObject();
		} catch (ClassNotFoundException | IOException ex) {}

		System.out.println("Status: " + stat);

		if (stat.equals("OK")) {
			try {
				size = (int) inputStream.readObject();
				symbol = (String) inputStream.readObject();
			} catch (ClassNotFoundException | IOException ex) {
				System.out.println("Gotcha no params!");
			}

			map = new String[size][size];
			for (int i = 0; i < size; i++)
				for (int j = 0; j < size; j++)
					map[i][j] = " ";

		} else
			throw new TTTException("Nie mozna polaczyc do gry");

		TTTFrame.getInstance().disableInitPanel();
	}

	/**
	 * Closes {@link ObjectInputStream}, {@link ObjectInputStream} and
	 * {@link Socket}. Called in case of lost signal.
	 */
	private void closeAll() {
		try {
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException ex) {}
	}

	/**
	 * Visually updates game area from the map field.
	 */
	private void updadeMap() {
		TTTFrame.getInstance().getClientPanel().updadeMap(map);
	}

	/**
	 * Gives the value of size object.
	 * 
	 * @return The size.
	 */
	public int getSize() {
		return size;
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
	 * Sends coordinates of next move.
	 * 
	 * @param where
	 */
	public void makeMove(int[] where) {
		try {
			outputStream.writeObject(where);
		} catch (IOException ex) {}
	}

	/**
	 * Checks connection, sends QUIT message and disconnects from a game.
	 */
	public void quitGame() {
		if (!socket.isConnected()) {
			System.out.println("Brak polaczenia");
			return;
		}
		try {
			outputStream.writeObject("QUIT");
		} catch (IOException ex) {}

		closeAll();
	}

	/**
	 * Sends message that asks for score table.
	 */
	public void requestTable() {
		try {
			outputStream.writeObject("table");
		} catch (IOException ex) {}
	}

	/**
	 * Player's loop, trys to receive a message until {@link Player#reading}
	 * parameter is true.
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (status) {
			Object o = null;
			try {
				o = inputStream.readObject();
			} catch (IOException | ClassNotFoundException ex) {}

			if (o == null) {
				status = false;
				TTTFrame.getInstance().showErrorDialog(
						"Utracono polaczenie z serwerem!");
			} else if (o instanceof String[][]) {
				map = (String[][]) o;
				updadeMap();
			} else if (o instanceof String[])
				TTTFrame.getInstance().showScoreDialog((String[]) o);
			else if (o instanceof String) {
				String s = (String) o;
				TTTFrame.getInstance().getClientPanel().setCurrentPlayer(s);
			}
		}
		System.out.println("END");
		closeAll();
	}
}
