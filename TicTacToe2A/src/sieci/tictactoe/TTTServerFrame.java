package sieci.tictactoe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The class that displays a simple window which from user chooses game
 * parameters and sets up the Server for the game.
 * 
 * @author Piotr Szerszeń
 * 
 */
public class TTTServerFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = -6311384235254078815L;
	/**
	 * The component that lets a choice for a minimal amount of symbols required
	 * for scoring a point. It can be set as a number from 3 to 32.
	 */
	private JComboBox<String> minWygrana;
	/**
	 * The number of a port where the game is set.
	 */
	private JTextField port;
	/**
	 * The component that lets a choice for a size of game area. It can be set
	 * as a number from 3 to 32 and the area is a square.
	 */
	private JComboBox<String> rozmiarPlanszy;
	/**
	 * Button that is supposed to set up the Server and let player connect to
	 * the play.
	 */
	private JButton StartSerwer;

	/**
	 * Constructor sets up all the components in a window.
	 */
	public TTTServerFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(450, 203));
		setTitle("TIC TAC TOE");

		JPanel serwerPanel = new JPanel();
		getContentPane().add(serwerPanel, BorderLayout.NORTH);
		serwerPanel.setLayout(null);

		JLabel lblRozmiarPlanszy = new JLabel("Rozmiar planszy:");
		lblRozmiarPlanszy.setBounds(10, 11, 209, 14);
		serwerPanel.add(lblRozmiarPlanszy);

		JLabel lblWygrywajacaLiczbaSymboli = new JLabel(
				"Wygrywajaca liczba symboli:");
		lblWygrywajacaLiczbaSymboli.setBounds(10, 61, 209, 14);
		serwerPanel.add(lblWygrywajacaLiczbaSymboli);

		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(10, 125, 46, 14);
		serwerPanel.add(lblPort);

		rozmiarPlanszy = new JComboBox<>();
		rozmiarPlanszy.setModel(new DefaultComboBoxModel<>(new String[] { "3",
				"4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
				"15", "16", "17", "18", "19", "20", "21", "22", "23", "24",
				"25", "26", "27", "28", "29", "30", "31", "32" }));
		rozmiarPlanszy.setSelectedIndex(0);
		rozmiarPlanszy.setBounds(10, 25, 46, 20);
		serwerPanel.add(rozmiarPlanszy);

		minWygrana = new JComboBox<>();
		minWygrana.setModel(new DefaultComboBoxModel<>(new String[] { "3", "4",
				"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
				"16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
				"26", "27", "28", "29", "30", "31", "32" }));
		minWygrana.setSelectedIndex(0);
		minWygrana.setBounds(10, 86, 46, 20);
		serwerPanel.add(minWygrana);

		port = new JTextField();
		port.setBounds(39, 122, 86, 20);
		serwerPanel.add(port);
		port.setColumns(10);

		StartSerwer = new JButton("Start!");
		StartSerwer.addActionListener(this);
		StartSerwer.setActionCommand("StartSerwer");
		StartSerwer.setBounds(259, 70, 89, 23);
		serwerPanel.add(StartSerwer);

		getContentPane().add(serwerPanel, BorderLayout.CENTER);
		setVisible(true);
	}

	/**
	 * It serves just a one button ({@link TTTServerFrame#StartSerwer}). Button
	 * gathers parameters and starts the game. Immune for stupidly given data.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("StartSerwer"))
			try {
				int port = Integer.parseInt(this.port.getText());
				int size = Integer.parseInt(rozmiarPlanszy.getSelectedItem()
						.toString());
				int goal = Integer.parseInt(minWygrana.getSelectedItem()
						.toString());
				if (goal > size)
					throw new TTTException("Złe parametry!");

				new TTTServer(port, size, goal);
			} catch (NumberFormatException | IOException ex) {
				ex.printStackTrace();
			} catch (TTTException ex) {
				System.out.println(ex.getMessage());
			}
	}

	/**
	 * Creates new instance of this class.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new TTTServerFrame();
	}
}
