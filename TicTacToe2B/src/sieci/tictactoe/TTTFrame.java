package sieci.tictactoe;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
 * @author Adam Szerszeń
 * 
 */
public class TTTFrame extends JFrame implements WindowListener {
	/**
	 * Small panel class containing components gathering parameter data.
	 * 
	 * @author Adam Szerszeń
	 * 
	 */
	private class InitPanel extends JPanel {
		private static final long serialVersionUID = -7435009299698859652L;
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
	private static final long serialVersionUID = -4111434384662129680L;
	/**
	 * The instance of {@link InitPanel} in the class.
	 */
	private InitPanel initPanel;
	/**
	 * The instance of {@link ClientPanel} in the class.
	 */
	private ClientPanel panel;

	/**
	 * Initializes a window with the game;
	 */
	public TTTFrame() {
		super("Kółko i krzyżyk");
		setLayout(new BorderLayout());

		setLocationRelativeTo(null);
		addWindowListener(this);

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
		} catch (TTTException e) {
			showErrorDialog(e.getMessage());
			return;
		}

		panel = new ClientPanel(game.getSize(), game, game.getSymbol());
		this.add(panel, BorderLayout.CENTER);

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
	 * Gives the value of panel object.
	 * 
	 * @return The panel.
	 */
	public ClientPanel getPanel() {
		return panel;
	}

	/**
	 * Displays the {@link JOptionPane} with error message.
	 * 
	 * @param error
	 *            Error message text.
	 */
	public void showErrorDialog(String error) {
		JOptionPane.showMessageDialog(this, error, "ERROR",
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
		// JScrollPane scroll = new
		dialog.add(list);
		dialog.pack();

		dialog.setVisible(true);
	}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	/**
	 * Calls {@link ClientPanel#quitGame()} before closing the application.
	 * 
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		try {
			panel.quitGame();
		} catch (Exception ex) {}
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	/**
	 * Gives the value of instance object.
	 * 
	 * @return The instance.
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
		getInstance().setVisible(true);
	}
}
