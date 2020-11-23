package sieci.tictactoe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The class used by a player for exchanging messages with Server.
 * 
 * @author Adam Szerszeń
 * 
 */
public class Player extends Thread {
	/**
	 * The instance of TTTGame in the class.
	 */
	private TTTGame game;
	/**
	 * For sending messages.
	 */
	private ObjectInputStream input;
	/**
	 * Player's alias.
	 */
	private String nick;
	/**
	 * For receiving messages.
	 */
	private ObjectOutputStream output;
	/**
	 * The parameter that defines when to stop trying receiving messages
	 * (received message objects are null).
	 */
	private boolean reading;
	/**
	 * The instance of Socket in the class.
	 */
	private Socket socket;
	/**
	 * The symbol of players marks on the area.
	 */
	private String symbol;

	/**
	 * Initializes everything required for exchanging data.
	 * 
	 * @param game
	 *            The duel, the player is taking part in.
	 * @param socket
	 *            Socket for getting I&O Streams.
	 * @throws IOException
	 */
	public Player(TTTGame game, Socket socket) throws IOException {
		this.game = game;
		reading = true;

		this.socket = socket;

		output = new ObjectOutputStream(this.socket.getOutputStream());
		input = new ObjectInputStream(this.socket.getInputStream());

		try {
			nick = (String) input.readObject();
		} catch (IOException | ClassNotFoundException e) {}

		if (this.game.addPlayer(this, nick))
			output.writeObject("OK");
		else {
			output.writeObject("NOTOK");
			return;
		}

		output.writeObject(this.game.getSize());
		output.writeObject(symbol);

		this.game.requestMap(this);
	}

	/**
	 * Closes {@link ObjectInputStream}, {@link ObjectInputStream} and
	 * {@link Socket}. Called in case of lost signal.
	 */
	private void closeAll() {
		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {}
	}

	/**
	 * Gives the value of nick object.
	 * 
	 * @return The nick.
	 */
	public String getNick() {
		return nick;
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
	 * Player's loop, trys to receive a message until {@link Player#reading}
	 * parameter is true.
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (reading) {
			Object o = null;
			try {
				o = input.readObject();
			} catch (IOException | ClassNotFoundException e) {}

			if (o == null) {
				reading = false;
				game.removePlayer(nick);
			} else if (o instanceof int[])
				game.makeMove(this, (int[]) o);
			else if (o instanceof String) {
				String s = (String) o;

				if ("table".equals(s))
					game.requestScores(this);
				else if ("QUIT".equals(s))
					game.removePlayer(nick);
			}
		}
		closeAll();
	}

	/**
	 * Used for sending some data from this player to Server.
	 * 
	 * @param o
	 *            Object to send.
	 */
	public void send(Object o) {
		try {
			output.writeObject(o);
		} catch (IOException ex) {}
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
