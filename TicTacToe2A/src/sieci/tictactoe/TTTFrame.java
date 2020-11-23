package sieci.tictactoe;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Class that runs the game for a player.
 * 
 * @author Piotr Szerszeń
 * 
 */
public class TTTFrame extends JFrame {
	/**
	 * Small panel class containing components gathering parameter data.
	 * 
	 * @author Piotr Szerszeń
	 * 
	 */
	private class InitPanel extends JPanel {
		private static final long serialVersionUID = -5092830111583195822L;
		/**
		 * Button gathering data and trying to connect with a Server.
		 */
		private JButton connectButton;
		/**
		 * One of components where player is typing parameters.
		 */
		private JTextField ipField, portField, nickField;

		/**
		 * Initializes the panel.
		 */
		public InitPanel() {
			setLayout(new GridLayout(1, 4));

			ipField = new JTextField("127.0.0.1");
			portField = new JTextField("6500");
			nickField = new JTextField("Pseudonim");

			connectButton = new JButton("Połącz");
			connectButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					initGame();
				}
			});

			this.add(ipField);
			this.add(portField);
			this.add(nickField);
			this.add(connectButton);
		}
	}

	/**
	 * The class singleton.
	 */
	private static TTTFrame instance = new TTTFrame();

	private static final long serialVersionUID = -2415843783219495351L;
	/**
	 * The instance of {@link ClientPanel} in the class.
	 */
	private ClientPanel clientPanel;
	/**
	 * The instance of {@link InitPanel} in the class.
	 */
	private InitPanel initPanel;

	/**
	 * Initializes a window with the game;
	 */
	private TTTFrame() {
		super("TIC TAC TOE");

		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		initPanel = new InitPanel();
		add(initPanel, BorderLayout.NORTH);

		pack();
	}

	/**
	 * Gathers data from {@link InitPanel} components and trys to connect to a
	 * Server.
	 */
	private void initGame() {
		ClientGame game;
		try {
			game = new ClientGame(initPanel.ipField.getText(),
					Integer.parseInt(initPanel.portField.getText()),
					initPanel.nickField.getText());
		} catch (TTTException ex) {
			ex.printStackTrace();
			showErrorDialog(ex.getMessage());
			return;
		}

		clientPanel = new ClientPanel(game.getSize(), game, game.getSymbol());
		add(clientPanel, BorderLayout.CENTER);

		game.start();

		pack();
	}

	/**
	 * Disables {@link InitPanel} components so that they cannot be edited
	 * anymore.
	 */
	public void disableInitPanel() {
		initPanel.connectButton.setEnabled(false);
		initPanel.ipField.setEnabled(false);
		initPanel.nickField.setEnabled(false);
		initPanel.portField.setEnabled(false);
	}

	/**
	 * @return the clientPanel
	 */
	public ClientPanel getClientPanel() {
		return clientPanel;
	}

	/**
	 * Displays the {@link JOptionPane} with error message.
	 * 
	 * @param err
	 *            Error message text.
	 */
	public void showErrorDialog(String err) {
		JOptionPane.showMessageDialog(this, err, "ERROR",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Displays {@link JDialog} with score table.
	 * 
	 * @param table
	 *            Scores to display.
	 */
	public void showScoreDialog(String[] table) {
		JDialog dialog = new JDialog(this, "Wyniki", true);
		dialog.setLocationRelativeTo(this);
		dialog.setLayout(new GridLayout());
		JList<String> list = new JList<>(table);
		dialog.add(list);
		dialog.pack();

		dialog.setVisible(true);
	}

	/**
	 * @return the instance
	 */
	public static TTTFrame getInstance() {
		return instance;
	}

	/**
	 * Sets the instance of this class visible.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TTTFrame.getInstance().setVisible(true);
	}
}
