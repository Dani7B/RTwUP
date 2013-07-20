package view.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import storage.URLMap;


/**
 * Graphic User Interface to display the output of RTwUP
 * 
 * @author Daniele Morgantini
 */
public class GUI {

	private JFrame frmRtwup;
	private AutoUpdateJPanel panel;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmRtwup.setVisible(true);
					window.frmRtwup.setSize(600, 500);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
		Timer timer = new Timer(5000, panel);
		timer.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmRtwup = new JFrame();
		frmRtwup.setTitle("RTwUP");
		frmRtwup.getContentPane().setFont(new Font("Segoe Print", Font.BOLD, 11));
		frmRtwup.getContentPane().setBackground(new Color(255, 255, 255));
		
		JLabel lblRtwupRealtime = new JLabel("RTwUP - Realtime Twitter Url Popularity");
		lblRtwupRealtime.setFont(new Font("Segoe Print", Font.BOLD, 11));
		lblRtwupRealtime.setForeground(new Color(30, 144, 255));
		
		panel = new AutoUpdateJPanel(URLMap.getInstance());
		GroupLayout groupLayout = new GroupLayout(frmRtwup.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
						.addComponent(lblRtwupRealtime, GroupLayout.PREFERRED_SIZE, 428, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(6)
					.addComponent(lblRtwupRealtime)
					.addGap(31)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
					.addGap(3))
		);
	
		/*btnAdd.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event)
            {
				String toAdd = textField.getText();
				Integer k = mappa.get(toAdd);
				if(k == null)
					mappa.put(toAdd, 1);
				else
					mappa.put(toAdd, k+1);
            }
		});*/
		frmRtwup.getContentPane().setLayout(groupLayout);
		frmRtwup.setBounds(100, 100, 450, 300);
		frmRtwup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

