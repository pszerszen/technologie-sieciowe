package sieci.tictactoe;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Class that contains all components of GUI and provides interaction with a
 * player.
 * 
 * @author Adam Szerszeń
 * 
 */
public class ClientPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -225396230906488343L;
	/**
	 * Matrix of buttons. Each one represent a cell in game area.
	 */
	private JButton[][] buttons;
	/**
	 * The instance of {@link ClientGame} in the class.
	 */
	private ClientGame game;
	/**
	 * Button invoking displaying of score table.
	 */
	private JButton scores;
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
	public ClientPanel(int n, final ClientGame game, String symbol) {
		setLayout(new GridLayout(n + 1, n, 5, 5));

		buttons = new JButton[n][n];
		this.symbol = symbol;
		this.game = game;
		scores = new JButton("Tabela");
		scores.setActionCommand("scores");
		scores.addActionListener(this);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				buttons[i][j] = new JButton(" ");
				buttons[i][j].setActionCommand(i + 1 + " " + (j + 1));
				buttons[i][j].addActionListener(this);
				this.add(buttons[i][j]);
			}

		this.add(scores);
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!e.getActionCommand().equals("scores")) {
			String button = e.getActionCommand();
			int space = button.indexOf(" ");
			String ii = button.substring(0, space);
			int i = Integer.parseInt(ii) - 1;
			String jj = button.substring(space + 1, button.length());
			int j = Integer.parseInt(jj) - 1;

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
	 * Updates a map visually.
	 * 
	 * @param map
	 *            String matrix source.
	 */
	public void updateMap(String[][] map) {
		for (int i = 0; i < map.length; i++)
			for (int j = 0; j < map.length; j++) {
				buttons[i][j].setText(map[i][j]);
				if (!" ".equals(map[i][j]))
					buttons[i][j].setEnabled(false);
				else
					buttons[i][j].setEnabled(true);
			}
	}
}
