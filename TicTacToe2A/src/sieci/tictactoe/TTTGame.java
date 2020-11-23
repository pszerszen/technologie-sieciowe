package sieci.tictactoe;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeMap;

/**
 * The class representing the game from the perspective of Server.
 * 
 * @author Piotr Szerszeń
 * 
 */
public class TTTGame {
	/**
	 * The class that stores the value of an integer value. Used for counters.
	 * 
	 * @author Piotr Szerszeń
	 * 
	 */
	private class MyInt {
		/**
		 * Value of integer.
		 */
		private int value;

		/**
		 * Simple constructor of class.
		 * 
		 * @param value
		 *            Initial value of the integer.
		 */
		public MyInt(int value) {
			this.value = value;
		}

		/**
		 * Gives the value of value object.
		 * 
		 * @return The value.
		 */
		public int getValue() {
			return value;
		}

		/**
		 * Increases the integer value by 1.
		 */
		public void increase() {
			value++;
		}
	}

	/**
	 * Number of occupied fields on game area.
	 */
	private int counter;
	/**
	 * Current's player's nick's index.
	 */
	private int current;
	/**
	 * Alias of a player that is about to make a move.
	 */
	private String currentPlayer;
	/**
	 * Minimal amount of symbols required for scoring a point.
	 */
	private int goal;
	/**
	 * Map of the game area.
	 */
	private String[][] map;
	/**
	 * A collection containing full list of players in the game. Provides fast
	 * access to each one of them via nick.
	 */
	private TreeMap<String, Player> players;
	/**
	 * Size of the game area.
	 */
	private int size;
	/**
	 * Symbol in usage in game.
	 */
	private LinkedList<String> symbols;
	/**
	 * List of coordinates of the symbols to remove from area.
	 */
	private LinkedList<int[]> symbolsToDelete;
	/**
	 * Nicks in usage in game.
	 */
	private LinkedList<String> usingNicks;

	/**
	 * Initializes the game.
	 * 
	 * @param size
	 *            Requested area size.
	 * @param goal
	 *            Requested minimal number of same symbols to score.
	 */
	public TTTGame(int size, int goal) {
		players = new TreeMap<>();

		symbols = new LinkedList<>();
		usingNicks = new LinkedList<>();
		symbolsToDelete = new LinkedList<>();

		for (char c = 'A'; c <= 'Z'; c++)
			symbols.add(String.valueOf(c));

		this.size = size;
		this.goal = goal;
		current = 0;
		counter = 0;

		map = new String[this.size][this.size];
		for (int i = 0; i < this.size; i++)
			for (int j = 0; j < this.size; j++)
				map[i][j] = " ";
	}

	/**
	 * Cleans up the area from any symbols.
	 */
	private void deleteAllSymbols() {
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++) {
				map[i][j] = " ";
				counter--;
			}
	}

	/**
	 * Removes every symbol from area that matches parameterized.
	 * 
	 * @param symbol
	 *            Symbol to clean up the area from.
	 */
	private void deleteAllSymbols(String symbol) {
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (map[i][j].equals(symbol)) {
					map[i][j] = " ";
					counter--;
				}
	}

	/**
	 * Removes a symbol randomly chosen from {@link TTTGame#symbolsToDelete}
	 */
	private void deleteRandomSymbol() {
		if (symbolsToDelete.size() == 0)
			return;
		synchronized (this) {
			try {
				Random rand = new Random();
				int chose = rand.nextInt(symbolsToDelete.size());
				int tab[] = symbolsToDelete.remove(chose);
				map[tab[0]][tab[1]] = " ";
				counter--;
			} catch (Exception e) {}
		}
	}

	/**
	 * Searches for winning combination of symbol set on position specified by x
	 * and y. It goes in direction specified by contractual integer and counts
	 * number of same symbols in line with specified counter as Integer.
	 * 
	 * @param x
	 *            Horizontal position.
	 * @param y
	 *            Vertical position.
	 * @param direction
	 *            Contractual (number - like geographical direction): <br>
	 *            1 - NW <br>
	 *            2 - N <br>
	 *            3 - NE <br>
	 *            4 - E <br>
	 *            5 - SE <br>
	 *            6 - S <br>
	 *            7 - SW <br>
	 *            8 - W
	 * @param counter
	 *            A MyInt object the counting will be on.
	 */
	private void lookForWin(int x, int y, int direction, MyInt counter) {
		switch (direction) {
		case 1:
			if ((x - 1 >= 0 && y - 1 >= 0)
					&& map[x][y].equals(map[x - 1][y - 1])) {
				counter.increase();
				lookForWin(x - 1, y - 1, direction, counter);
			}
			break;
		case 2:
			if ((y - 1 >= 0) && map[x][y].equals(map[x][y - 1])) {
				counter.increase();
				lookForWin(x, y - 1, direction, counter);
			}
			break;
		case 3:
			if ((x + 1 < size && y - 1 >= 0)
					&& map[x][y].equals(map[x + 1][y - 1])) {
				counter.increase();
				lookForWin(x + 1, y - 1, direction, counter);
			}
			break;
		case 4:
			if ((x + 1 < size) && map[x][y].equals(map[x + 1][y])) {
				counter.increase();
				lookForWin(x + 1, y, direction, counter);
			}
			break;
		case 5:
			if ((x + 1 < size && y + 1 < size)
					&& map[x][y].equals(map[x + 1][y + 1])) {
				counter.increase();
				lookForWin(x + 1, y + 1, direction, counter);
			}
			break;
		case 6:
			if ((y + 1 < size) && map[x][y].equals(map[x][y + 1])) {
				counter.increase();
				lookForWin(x, y + 1, direction, counter);
			}
			break;
		case 7:
			if ((x - 1 >= 0 && y + 1 < size)
					&& map[x][y].equals(map[x - 1][y + 1])) {
				counter.increase();
				lookForWin(x - 1, y + 1, direction, counter);
			}
			break;
		case 8:
			if ((x - 1 >= 0) && map[x][y].equals(map[x - 1][y])) {
				counter.increase();
				lookForWin(x - 1, y, direction, counter);
			}
			break;
		}
	}

	/**
	 * Changes all specified symbols to lower case, and adds it coordinates to
	 * {@link TTTGame#symbolsToDelete} list.
	 * 
	 * @param symbol
	 *            Symbol to get rid of the area.
	 */
	private void lowerSymbols(String symbol) {
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (map[i][j].equals(symbol)) {
					map[i][j] = map[i][j].toLowerCase();
					symbolsToDelete.add(new int[] { i, j });
				}
	}

	/**
	 * Sorts players by number of scored points and saves them in "results.xml"
	 * file.
	 */
	private void saveScores() {
		Collection<Player> currents = players.values();
		LinkedList<Player> ps = new LinkedList<>(currents);
		Collections.sort(ps, new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				int i1 = o1.getScore();
				int i2 = o2.getScore();

				if (i1 > i2)
					return -1;
				else
					return 1;
			}
		});

		String[] results = new String[ps.size()];
		int i = 0;
		for (Player p : ps)
			results[i++] = p.getNick() + " - " + p.getScore();

		try {
			FileOutputStream fos = new FileOutputStream("results.xml");
			XMLEncoder e = new XMLEncoder(new BufferedOutputStream(fos));
			e.writeObject(results);
			e.close();
		} catch (IOException e) {}
	}

	/**
	 * Sends info to every player in the game about which player is about to
	 * make his/her move currently.
	 * 
	 * @param nick
	 *            Player's nick.
	 */
	private void sendInfoToEveryone(String nick) {
		Collection<Player> players = this.players.values();
		for (Player p : players)
			p.send(nick);
	}

	/**
	 * Sends info to every player in the game about current status of game area.
	 */
	private void sendMapToEveryone() {
		Collection<Player> players = this.players.values();

		String[][] mapa = new String[size][size];
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				mapa[i][j] = map[i][j];
		for (Player p : players)
			p.send(mapa);
	}

	/**
	 * Checks if player won the stage after placing his/her symbol on the
	 * specified position.
	 * 
	 * @param position
	 *            The position where symbol has been set last.
	 * @return True if player won, False otherwise.
	 */
	private boolean won(int[] where) {
		int second = where[1];
		int first = where[0];
		boolean result = false;
		MyInt vcount, hcount, lcount, rcount;
		vcount = new MyInt(1);
		hcount = new MyInt(1);
		lcount = new MyInt(1);
		rcount = new MyInt(1);

		lookForWin(first, second, 4, hcount);
		lookForWin(first, second, 8, hcount);
		if (hcount.getValue() >= goal)
			return true;

		lookForWin(first, second, 2, vcount);
		lookForWin(first, second, 6, vcount);
		if (vcount.getValue() >= goal)
			return true;

		lookForWin(first, second, 1, lcount);
		lookForWin(first, second, 5, lcount);
		if (lcount.getValue() >= goal)
			return true;

		lookForWin(first, second, 3, rcount);
		lookForWin(first, second, 7, rcount);
		if (rcount.getValue() >= goal)
			return true;

		return result;
	}

	/**
	 * Adds new player to the game and includes him in score board.
	 * 
	 * @param player
	 *            Player to add.
	 * @param nick
	 *            Player's proposed alias.
	 * @return True if everything went well, false if specified nick is already
	 *         in use.
	 */
	public boolean addPlayer(Player player, String nick) {
		if (players.containsKey(nick) || symbols.size() == 0)
			return false;
		else {
			player.setSymbol(symbols.pollFirst());
			players.put(nick, player);

			System.out.println("Dodano gracza: " + nick);
			usingNicks.add(nick);

			if (players.size() == 1) {
				currentPlayer = nick;
				current = 0;
			}
			saveScores();
			player.start();
			return true;
		}
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
	 * Marks specified field as specified player's.
	 * 
	 * @param who
	 *            Player who makes his/her move.
	 * @param where
	 *            The coordinates of move being taken.
	 */
	public void makeMove(Player who, int[] where) {
		if (!who.getNick().equals(currentPlayer))
			return;
		System.out.println("Ktoś chce wykonać ruch " + where[0] + " "
				+ where[1]);
		int x = where[0];
		int y = where[1];
		if (" ".equals(map[x][y])) {
			map[x][y] = who.getSymbol();
			counter++;
			System.out.println(map[x][y]);

			if (won(where)) {
				who.increaseScore();
				this.deleteAllSymbols(who.getSymbol());
				saveScores();
			}
			deleteRandomSymbol();
			if (counter == (size * size))
				this.deleteAllSymbols();

			sendMapToEveryone();
		}

		current++;
		try {
			currentPlayer = usingNicks.get(current);
		} catch (IndexOutOfBoundsException e) {
			currentPlayer = usingNicks.get(0);
			current = 0;
		}
		sendInfoToEveryone(currentPlayer);
	}

	/**
	 * Removes player with requested alias.
	 * 
	 * @param nick
	 *            Nick of Player to remove.
	 */
	public void removePlayer(String nick) {
		usingNicks.remove(nick);
		try {
			lowerSymbols(players.get(nick).getSymbol());
		} catch (Exception e) {
			return;
		}
		// this.deleteAllSymbols(this.players.get(nick).getSymbol());
		symbols.addLast(players.get(nick).getSymbol());
		players.remove(nick);
		current++;
		try {
			currentPlayer = usingNicks.get(current);
		} catch (IndexOutOfBoundsException e) {
			try {
				currentPlayer = usingNicks.get(0);
				current = 0;
			} catch (IndexOutOfBoundsException ex) {
				currentPlayer = "NONE";
			}
		}
		sendMapToEveryone();
		sendInfoToEveryone(currentPlayer);
	}

	/**
	 * Sends object of current Player.
	 * 
	 * @param player
	 *            Player to send.
	 */
	public void requestCurrentPlayer(Player player) {
		player.send(currentPlayer);
	}

	/**
	 * Sends object of player's game area (String[][]).
	 * 
	 * @param player
	 *            Player whom game area will be send.
	 */
	public void requestMap(Player player) {
		player.send(map);
	}

	/**
	 * Sends object containing current scores of the game.
	 * 
	 * @param player
	 *            Player whom game's scores will be send from.
	 */
	public void requestScores(Player player) {
		String[] table = null;
		try {
			FileInputStream fis = new FileInputStream("results.xml");
			XMLDecoder d = new XMLDecoder(new BufferedInputStream(fis));
			table = (String[]) d.readObject();
			d.close();
		} catch (IOException e) {}

		player.send(table);
	}
}
