package sieci.tictactoe;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Provides connection between a pair of players.
 * 
 * @author Piotr Szerszeń
 */
public class TTTServerThread extends Thread {
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
	 * The game arena;
	 */
	private String[][] arena;
	/**
	 * Counter for counting set positions. When reaches its maximum (size^2)
	 * server informs about the DRAW.
	 */
	private int counter;
	/**
	 * One of the game players.
	 */
	private Player player1, player2;
	/**
	 * Game parameters
	 */
	private int size, goal;

	public TTTServerThread(Player player1, Player player2, int size, int goal) {
		this.player1 = player1;
		this.player2 = player2;
		// przekazanie graczom parametru rozmiaru planszy
		this.player1.getPlayerWriter().println(String.valueOf(size));
		this.player2.getPlayerWriter().println(String.valueOf(size));
		// przekazanie graczom parametru wygrywajcej liczby znaków
		this.player1.getPlayerWriter().println(String.valueOf(goal));
		this.player2.getPlayerWriter().println(String.valueOf(goal));
		// przekazanie graczom ich znaków
		this.player1.getPlayerWriter().println("X");
		this.player2.getPlayerWriter().println("O");
		// przekazanie graczom znaków ich przeciwnika
		this.player1.getPlayerWriter().println("O");
		this.player2.getPlayerWriter().println("X");

		this.size = size;
		this.goal = goal;
		arena = new String[size + 1][size + 1];
		for (int i = 0; i <= this.size; i++)
			for (int j = 0; j <= this.size; j++)
				arena[i][j] = " ";
	}

	/**
	 * Called in case {@link SocketTimeoutException}. Sends a question message
	 * to opponent if still plays, in case there is no answer it throws
	 * exception informing about this.
	 * 
	 * @param player
	 *            The player whom the message is to be send to.
	 * @throws TTTException
	 * @throws IOException
	 */
	private void check(Player player) throws TTTException, IOException {
		try {
			player.getCheckWriter().println("Are you alive?");
			String answer = player.getCheckReader().readLine();
			if (answer == null)
				throw new TTTException();
		} catch (SocketTimeoutException e) {
			throw new TTTException();
		}
	}

	/**
	 * Informs the player about end of the game in case player declares to
	 * finish or lost connection.
	 * 
	 * @param player
	 *            The Player to be informed.
	 */
	private void informCheck(Player player) {
		player.getCheckWriter().println("QUIT");
	}

	/**
	 * Checks whether move provided as {@link String} contains a valid data for
	 * the game.
	 * 
	 * @param move
	 *            The move to check.
	 * @return True if move is valid, false otherwise.
	 */
	private boolean isValid(String move) {
		if (move.length() != 2)
			return false;
		char c = move.charAt(0);
		int i;
		try {
			i = Integer.parseInt(String.valueOf(move.charAt(1)));
		} catch (NumberFormatException e) {
			return false;
		}
		if (c >= 'A' && c <= 'A' + (size - 1))
			if (i >= 1 && i <= size) {
				int first = (c) - 64;
				if (" ".equals(arena[i][first]))
					return true;
			}
		return false;
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
	 *            An Integer the counting will be on.
	 */
	private void lookForWin(int x, int y, int direction, MyInt counter) {
		switch (direction) {
		case 1:// NW
			if ((x - 1 >= 1 && y - 1 >= 1)
					&& arena[x][y].equals(arena[x - 1][y - 1])) {
				counter.increase();
				lookForWin(x - 1, y - 1, direction, counter);
			}
			break;
		case 2:// N
			if ((y - 1 >= 1) && arena[x][y].equals(arena[x][y - 1])) {
				counter.increase();
				lookForWin(x, y - 1, direction, counter);
			}
			break;
		case 3:// NE
			if ((x + 1 <= size && y - 1 >= 1)
					&& arena[x][y].equals(arena[x + 1][y - 1])) {
				counter.increase();
				lookForWin(x + 1, y - 1, direction, counter);
			}
			break;
		case 4:// E
			if ((x + 1 <= size) && arena[x][y].equals(arena[x + 1][y])) {
				counter.increase();
				lookForWin(x + 1, y, direction, counter);
			}
			break;
		case 5:// SE
			if ((x + 1 <= size && y + 1 <= size)
					&& arena[x][y].equals(arena[x + 1][y + 1])) {
				counter.increase();
				lookForWin(x + 1, y + 1, direction, counter);
			}
			break;
		case 6:// S
			if ((y + 1 <= size) && arena[x][y].equals(arena[x][y + 1])) {
				counter.increase();
				lookForWin(x, y + 1, direction, counter);
			}
			break;
		case 7:// SW
			if ((x - 1 >= 1 && y + 1 <= size)
					&& arena[x][y].equals(arena[x - 1][y + 1])) {
				counter.increase();
				lookForWin(x - 1, y + 1, direction, counter);
			}
			break;
		case 8:// W
			if ((x - 1 >= 1) && arena[x][y].equals(arena[x - 1][y])) {
				counter.increase();
				lookForWin(x - 1, y, direction, counter);
			}
			break;
		}
	}

	/**
	 * Sets the symbol provided as what on the position provided as where on the
	 * arena game.
	 * 
	 * @param where
	 *            For example A1.
	 * @param what
	 *            X or O.
	 */
	private void putON(String where, String what) {
		int second = (where.charAt(0)) - 64;
		int first = Integer.parseInt(String.valueOf(where.charAt(1)));

		arena[first][second] = what;
	}

	/**
	 * Reads the message and its confirmation.
	 * 
	 * @param player
	 *            The Player whose message is to read.
	 * @return Read message.
	 * @throws TTTException
	 * @throws IOException
	 */
	private String read(Player player) throws TTTException, IOException {
		String signal = "";
		try {
			signal = player.getPlayerReader().readLine();
			if (signal == null)
				throw new TTTException();
		} catch (SocketTimeoutException ex) {
			try {
				check(player);
				signal = read(player);
			} catch (TTTException eex) {
				throw eex;
			}
		}

		return signal;
	}

	/**
	 * Reads the message from player1 and depending on its matter, sends proper
	 * response to player2.
	 * 
	 * @param player1
	 *            Message requester.
	 * @param player2
	 *            Message responder.
	 * @return True if game is on after this turn, false otherwise.
	 */
	private boolean servePlayers(Player player1, Player player2) {
		write("MOVE", player1);
		write("WAIT", player2);

		boolean flag = true;

		String signal = "";

		try {
			signal = read(player1);
		} catch (TTTException | IOException e) {
			write("LOSTCON", player2);
			informCheck(player2);
			flag = false;
		}

		switch (signal) {
		case "QUIT":
			flag = false;
			write("QUIT", player2);
			informCheck(player2);
			informCheck(player1);
			break;
		case "PLACE":
			String position = "";
			String temp = "";
			boolean error = false;
			while (true) {
				try {
					position = read(player1);
				} catch (TTTException | IOException e) {
					player2.getPlayerWriter().println("LOSTCON");
					informCheck(player2);
					System.out.println(e.getMessage());
					error = true;
					break;
				}
				if ("QUIT".equals(position)) {
					flag = false;
					write("QUIT", player2);
					informCheck(player2);
					informCheck(player1);
					error = true;
					break;
				}
				if ("OK".equals(position)) {
					position = temp;
					break;
				}
				temp = position;
				if (isValid(position))
					player1.getPlayerWriter().println("OK");
				else
					player1.getPlayerWriter().println("WM");
			}

			if (error) {
				flag = false;
				break;
			}
			write("WFS", player2);
			write(position, player2);
			putON(position, player1.getSymbol());
			counter++;
			if (won(position)) {
				write("LOST", player2);
				write(position, player2);
				write("WON", player1);
				informCheck(player2);
				informCheck(player1);
				flag = false;
			}
			break;
		}
		return flag;
	}

	/**
	 * Checks if player won the stage after placing his/her symbol on the
	 * specified position.
	 * 
	 * @param position
	 *            The position where symbol has been set last.
	 * @return True if player won, False otherwise.
	 */
	private boolean won(String position) {
		int second = (position.charAt(0)) - 64;
		int first = Integer.parseInt(String.valueOf(position.charAt(1)));
		boolean status = false;
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

		return status;
	}

	/**
	 * Sends the "message" to the "player".
	 * 
	 * @param message
	 * @param player
	 */
	private void write(String message, Player player) {
		player.getPlayerWriter().println(message);
	}

	/**
	 * Games thread. Runs and lets players to do their moves by turns until its
	 * possible.
	 */
	@Override
	public void run() {
		boolean flag = true;
		int max = size * size;
		counter = 0;

		while (flag) {

			flag = servePlayers(player1, player2);

			if (!flag)
				break;

			if (counter == max) {
				player1.getCheckWriter().println("QUIT");
				player2.getCheckWriter().println("QUIT");

				player1.getPlayerWriter().println("DRAW");
				player2.getPlayerWriter().println("DRAW");
				break;
			}

			flag = servePlayers(player2, player1);
		}
		player1.closeAll();
		player2.closeAll();
	}
}
