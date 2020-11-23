package sieci.tictactoe;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Class that contains all components of GUI and provides interaction with a
 * player.
 * 
 * @author Piotr Szerszeń
 * 
 */
public class ClientPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 3904234968508145727L;

	/**
	 * Button invoking displaying of score table.
	 */
	private JButton btnscores;
	/**
	 * Matrix of buttons. Each one represent a cell in game area.
	 */
	private JButton[][] buttons;
	/**
	 * The instance of {@link ClientGame} in the class.
	 */
	private ClientGame game;
	/**
	 * Labels showing who is currently about to make a move.
	 */
	private JLabel lblnick, lblcurrent;
	/**
	 * Player's symbol;
	 */
	@SuppressWarnings("unused")
	private String symbol;

	/**
	 * Initializes all required parameters.
	 * 
	 * @param n
	 *            Size of area.
	 * @param game
	 *            {@link ClientGame} instance.
	 * @param symbol
	 *            Player's symbol.
	 */
	public ClientPanel(int n, ClientGame game, String symbol) {
		setLayout(new GridLayout(n + 1, n, 5, 5));

		buttons = new JButton[n][n];
		this.symbol = symbol;
		this.game = game;
		lblcurrent = new JLabel("Aktualny gracz: ");
		lblnick = new JLabel();
		btnscores = new JButton("Tabela");
		btnscores.setActionCommand("scores");
		btnscores.addActionListener(this);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				buttons[i][j] = new JButton();
				buttons[i][j].setFocusable(false);
				buttons[i][j].setActionCommand(i + 1 + " " + (j + 1));
				buttons[i][j].addActionListener(this);
				add(buttons[i][j]);
			}

		add(lblcurrent);
		add(lblnick);
		add(btnscores);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!e.getActionCommand().equals("scores")) {
			String[] command = e.getActionCommand().split(" ");
			int i = Integer.parseInt(command[0]) - 1;
			int j = Integer.parseInt(command[1]) - 1;
			game.makeMove(new int[] { i, j });
		} else
			game.requestTable();
	}

	/**
	 * Changes sign on selected button with specified symbol.
	 * 
	 * @param x
	 *            Horizontal coordinate.
	 * @param y
	 *            Vertical coordinate.
	 * @param symbol
	 *            New symbol of selected button.
	 */
	public void changeButton(int x, int y, String symbol) {
		buttons[x - 1][y - 1].setText(symbol);
		buttons[x - 1][y - 1].setEnabled(false);
	}

	/**
	 * Quits game.
	 */
	public void quitGame() {
		game.quitGame();
	}

	/**
	 * Sets current player's nick on interface.
	 * 
	 * @param nick
	 *            Current player's nick.
	 */
	public void setCurrentPlayer(String nick) {
		lblnick.setText(nick);
	}

	/**
	 * Updates a map visually.
	 * 
	 * @param map
	 *            String matrix source.
	 */
	public void updadeMap(String[][] map) {
		for (int i = 0; i < map.length; i++)
			for (int j = 0; j < map.length; j++) {
				buttons[i][j].setText(map[i][j]);
				if (map[i][j].equals(" "))
					buttons[i][j].setEnabled(true);
				else
					buttons[i][j].setEnabled(false);
			}
	}
}
