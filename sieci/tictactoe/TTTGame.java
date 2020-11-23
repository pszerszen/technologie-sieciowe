package sieci.tictactoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The single game class. Contains the game's arena, server and client's
 * instances
 * 
 * @author Adam Szerszeń
 */
public class TTTGame {

	/**
	 * arena represents games field
	 */
	private String[][] arena;
	/**
	 * Client's Thread instance
	 */
	private TTTClientCheck check;

	/**
	 * Player's enemy's symbol
	 */
	private String esymbol;

	/**
	 * One of the game basic parameter.
	 */
	private int goal, size;

	/**
	 * For the reading enemy's message.
	 */
	private BufferedReader reader;
	/**
	 * Socket instance
	 */
	private Socket server;

	/**
	 * Player's symbol
	 */
	private String symbol;

	/**
	 * For the reading what player has written in standard input.
	 */
	private BufferedReader systemreader;

	/**
	 * For writing the messages
	 */
	private PrintWriter writer;

	/**
	 * Initializes all the parameters needed for the game.
	 * 
	 * @param server
	 *            Socket where player will send messages.
	 * @param clientCheck
	 *            Client whom player will send messages to.
	 */
	public TTTGame(Socket server, Socket clientCheck) {
		this.server = server;
		try {
			reader = new BufferedReader(new InputStreamReader(
					this.server.getInputStream()));
			systemreader = new BufferedReader(new InputStreamReader(System.in));
			writer = new PrintWriter(this.server.getOutputStream(), true);
		} catch (IOException e) {
		}

		String firstMessage = read();
		if ("WFO".equals(firstMessage))
			System.out.println("Czekaj na przeciwnika");

		try {
			size = Integer.parseInt(reader.readLine());
			goal = Integer.parseInt(reader.readLine());
			symbol = reader.readLine();
			esymbol = reader.readLine();
		} catch (IOException e) {
		}

		System.out
				.println("Rozpoczynasz gre na planszy " + size + " x " + size);
		System.out.println("Twoj symbol to: " + symbol + ", musisz postawic "
				+ goal + " symboli w jednej lini, aby wygrac");

		arena = new String[size + 1][size + 1];

		for (int i = 0; i <= size; i++)
			for (int j = 0; j <= size; j++)
				arena[i][j] = " ";

		char A = 'A';
		for (int i = 1; i <= size; i++) {
			arena[0][i] = String.valueOf(A);
			arena[i][0] = String.valueOf(i);
			A++;
		}

		check = new TTTClientCheck(clientCheck, this);
	}

	/**
	 * Reads the message and depending on its matter interacts with the player.
	 */
	private void gameLoop() {
		boolean stillAlive = true;
		String message = "";
		while (stillAlive) {
			message = read();

			switch (message) {
			case "QUIT":
				System.out.println("Koniec gry. Przeciwnik zrezygnowal.");
				stillAlive = false;
				break;
			case "WAIT":
				System.out.println("Czekaj na ruch przeciwnika");
				break;
			case "LOSTCON":
				System.out.println("Utracono polaczenie z przeciwnikiem");
				stillAlive = false;
				break;
			case "WFS":
				String enemy = read();
				putON(enemy, esymbol);
				break;
			case "MOVE":
				boolean decision = true;
				writer.println("PLACE");
				while (decision) {
					printArena();
					System.out.println("Twoj ruch:");
					String move = "";
					while (true) {
						try {
							move = systemreader.readLine();
						} catch (IOException e) {
						}
						if (move.equals("QUIT")) {
							stillAlive = false;
							writer.println("QUIT");
							break;
						}
						if (move.equals("OK")) {
							System.out.println("Nieprawidlowy ruch\n"
									+ "wpisz QUIT aby zakonczyc grę.");
							continue;
						}
						writer.println(move);
						if ("OK".equals(read()))
							break;
						else
							System.out.println("Nieprawidlowy ruch\n"
									+ "wpisz QUIT aby zakonczyc gre.");
					}
					if (!stillAlive)
						break;
					putON(move, "?");
					printArena();
					System.out.println("Potwierdz ruch wpisujac OK");
					String fin = "";
					try {
						fin = systemreader.readLine();
					} catch (IOException e) {
					}
					if (fin.equals("OK")) {
						putON(move, symbol);
						printArena();
						writer.println("OK");
						decision = false;
					} else if (fin.equals("QUIT")) {
						stillAlive = false;
						writer.println("QUIT");
						break;
					} else
						putOFF(move);
				}
				break;
			case "LOST":
				putON(read(), esymbol);
				printArena();
				System.out.println("Przegrales");
				stillAlive = false;
				break;
			case "DRAW":
				printArena();
				System.out.println("Zremisowales");
				stillAlive = false;
				break;
			case "WON":
				System.out.println("Wygrales!");
				stillAlive = false;
				break;
			}
		}
		closeAll(0);
	}

	/**
	 * Prints the arena in the current state on the standard output.
	 */
	private void printArena() {
		for (int i = 0; i <= size; i++) {
			for (int j = 0; j <= size; j++)
				System.out.print(arena[i][j] + " ");
			System.out.println();
		}
	}

	/**
	 * Unsets the symbol on the position provided as where on the arena game.
	 * 
	 * @param where
	 *            For example A1
	 */
	private void putOFF(String where) {
		int second = (where.charAt(0)) - 64;
		int first = Integer.parseInt(String.valueOf(where.charAt(1)));

		arena[first][second] = " ";
	}

	/**
	 * Sets the symbol provided as what on the position provided as where on the
	 * arena game.
	 * 
	 * @param where
	 *            For example A1
	 * @param what
	 *            X or O
	 */
	private void putON(String where, String what) {
		int second = (where.charAt(0)) - 64;
		int first = Integer.parseInt(String.valueOf(where.charAt(1)));

		arena[first][second] = what;
	}

	/**
	 * Reads and returns the message from the other player.
	 * 
	 * @return Read message.
	 */
	private String read() {
		String message = "";
		try {
			message = reader.readLine();
		} catch (IOException e) {
			check.closeAll();
			closeAll(1);
		}
		if (message == null) {
			check.closeAll();
			closeAll(1);
		}
		return message;
	}

	/**
	 * Closes {@link BufferedReader}, {@link BufferedWriter} and {@link Socket}.
	 * Called in case of lost the other player's signal.
	 * 
	 * @param status
	 */
	public synchronized void closeAll(int status) {
		try {
			reader.close();
			writer.close();
			server.close();
		} catch (IOException e) {
		}
		if (status != 0) {
			System.out.println("Utracono polaczenie z serwerem!");
			System.exit(1);
		}
	}

	/**
	 * Starts the Client's Thread and runs the game loop.
	 */
	public void initAll() {
		check.start();
		gameLoop();
	}
}
