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
 * @author Adam Szerszeń
 * 
 */
public class TTTGame {
	/**
	 * The class that stores the value of an integer value. Used for counters.
	 * 
	 * @author Adam Szerszeń
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
			return this.value;
		}

		/**
		 * Increases the integer value by 1.
		 */
		public void increase() {
			this.value++;
		}
	}

	/**
	 * An util class for easier score handling.
	 * 
	 * @author Adam Szerszeń
	 * 
	 */
	private class SymbolScore {
		/**
		 * Points of the symbol.
		 */
		private int score;
		/**
		 * The symbol.
		 */
		private String symbol;

		/**
		 * Initializes fields.
		 * 
		 * @param symbol
		 *            The symbol value.
		 */
		public SymbolScore(String symbol) {
			this.symbol = symbol;
			score = 0;
		}

		/*
		 * public void increase() { score++; }
		 */
	}

	/**
	 * Number of occupied fields on game area.
	 */
	private int counter;
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
	private Symbol[][] map;
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
	 * Symbols in usage in game.
	 */
	private LinkedList<String> symbols;
	/**
	 * The list, scores are taken from.
	 */
	private LinkedList<SymbolScore> symbolScores;
	/**
	 * List of coordinates of the symbols to remove from area.
	 */
	private LinkedList<int[]> symbolsToDelete;
	/**
	 * Each symbols lifetime.
	 */
	private int time;
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
	 * @param time
	 *            Each symbols lifetime.
	 */
	public TTTGame(int size, int goal, int time) {
		players = new TreeMap<>();

		symbols = new LinkedList<>();
		usingNicks = new LinkedList<>();
		symbolsToDelete = new LinkedList<>();
		symbolScores = new LinkedList<>();

		for (char c = 'A'; c <= 'Z'; c++) {
			symbols.add(String.valueOf(c));
			symbolScores.add(new SymbolScore(String.valueOf(c)));
		}

		this.size = size;
		this.goal = goal;
		this.time = time;
		counter = 0;

		map = new Symbol[this.size][this.size];
		for (int i = 0; i < this.size; i++)
			for (int j = 0; j < this.size; j++)
				map[i][j] = new Symbol(i, j);
	}

	/**
	 * Cleans up the area from any symbols.
	 */
	private void deleteAllSymbols() {
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++) {
				map[i][j].setSymbol(" ");
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
				if (map[i][j].getSymbol().equals(symbol)) {
					map[i][j].setSymbol(" ");
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
				map[tab[0]][tab[1]].setSymbol(" ");
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
					&& map[x][y].getSymbol().equals(
							map[x - 1][y - 1].getSymbol())) {
				counter.increase();
				lookForWin(x - 1, y - 1, direction, counter);
			}
			break;
		case 2:
			if ((y - 1 >= 0)
					&& map[x][y].getSymbol().equals(map[x][y - 1].getSymbol())) {
				counter.increase();
				lookForWin(x, y - 1, direction, counter);
			}
			break;
		case 3:
			if ((x + 1 < size && y - 1 >= 0)
					&& map[x][y].getSymbol().equals(
							map[x + 1][y - 1].getSymbol())) {
				counter.increase();
				lookForWin(x + 1, y - 1, direction, counter);
			}
			break;
		case 4:
			if ((x + 1 < size)
					&& map[x][y].getSymbol().equals(map[x + 1][y].getSymbol())) {
				counter.increase();
				lookForWin(x + 1, y, direction, counter);
			}
			break;
		case 5:
			if ((x + 1 < size && y + 1 < size)
					&& map[x][y].getSymbol().equals(
							map[x + 1][y + 1].getSymbol())) {
				counter.increase();
				lookForWin(x + 1, y + 1, direction, counter);
			}
			break;
		case 6:
			if ((y + 1 < size)
					&& map[x][y].getSymbol().equals(map[x][y + 1].getSymbol())) {
				counter.increase();
				lookForWin(x, y + 1, direction, counter);
			}
			break;
		case 7:
			if ((x - 1 >= 0 && y + 1 < size)
					&& map[x][y].getSymbol().equals(
							map[x - 1][y + 1].getSymbol())) {
				counter.increase();
				lookForWin(x - 1, y + 1, direction, counter);
			}
			break;
		case 8:
			if ((x - 1 >= 0)
					&& map[x][y].getSymbol().equals(map[x - 1][y].getSymbol())) {
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
				if (map[i][j].getSymbol().equals(symbol)) {
					map[i][j].lowerSymbol();
					symbolsToDelete.add(new int[] { i, j });
				}
	}

	/**
	 * Shortens each symbols time by 1.
	 */
	private void processSymbols() {
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				map[i][j].lowerT();
	}

	/**
	 * Sorts players by number of scored points and saves them in "results.xml"
	 * file.
	 */
	private void saveScores() {
		LinkedList<SymbolScore> result = symbolScores;
		Collections.sort(result, new Comparator<SymbolScore>() {

			@Override
			public int compare(SymbolScore o1, SymbolScore o2) {

				if (o1.score > o2.score)
					return -1;
				else
					return 1;
			}
		});

		String[] results = new String[result.size()];
		int i = 0;
		for (SymbolScore p : result)
			results[i++] = p.symbol + " - " + p.score;

		try {
			FileOutputStream fos = new FileOutputStream("results.xml");
			XMLEncoder e = new XMLEncoder(new BufferedOutputStream(fos));
			e.writeObject(results);
			e.close();
		} catch (IOException e) {}
	}

	/**
	 * Sends info to every player in the game about current status of game area.
	 */
	private void sendMapToEveryone() {
		Collection<Player> players = this.players.values();

		String[][] mapa = new String[size][size];
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				mapa[i][j] = map[i][j].getSymbol();
		for (Player p : players)
			p.send(mapa);
	}

	/**
	 * Checks if player won the stage after placing his/her symbol on the
	 * specified position.
	 * 
	 * @param where
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

		this.lookForWin(first, second, 4, hcount);
		this.lookForWin(first, second, 8, hcount);
		if (hcount.getValue() == this.goal) {
			return true;
		}

		this.lookForWin(first, second, 2, vcount);
		this.lookForWin(first, second, 6, vcount);
		if (vcount.getValue() == this.goal) {
			return true;
		}

		this.lookForWin(first, second, 1, lcount);
		this.lookForWin(first, second, 5, lcount);
		if (lcount.getValue() == this.goal) {
			return true;
		}

		this.lookForWin(first, second, 3, rcount);
		this.lookForWin(first, second, 7, rcount);
		if (rcount.getValue() == this.goal) {
			return true;
		}

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

			usingNicks.add(nick);

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
	public synchronized void makeMove(Player who, int[] where) {
		int x = where[0];
		int y = where[1];
		if (" ".equals(map[x][y].getSymbol())) {
			processSymbols();
			map[x][y].setSymbol(who.getSymbol());
			map[x][y].setT(time);
			counter++;

			if (won(where)) {
				symbolScores.get(who.getSymbol().charAt(0) - 'A').score++;
				this.deleteAllSymbols(who.getSymbol());
				saveScores();
			}
			deleteRandomSymbol();
			if (counter == (size * size))
				this.deleteAllSymbols();

			sendMapToEveryone();
		}
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

		symbols.addLast(players.get(nick).getSymbol());
		players.remove(nick);

		sendMapToEveryone();
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
		String[][] mapa = new String[size][size];
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				mapa[i][j] = map[i][j].getSymbol();
		player.send(mapa);
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
