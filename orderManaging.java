package aiven;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class orderManaging extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					orderManaging frame = new orderManaging();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public orderManaging() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 34, 74, 218);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JButton btnNewButton = new JButton("SHERWIN KARRIE");
		btnNewButton.setBounds(0, 0, 74, 53);
		panel.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("New button");
		btnNewButton_1.setBounds(0, 70, 74, 53);
		panel.add(btnNewButton_1);
		
		JButton btnNewButton_1_1 = new JButton("New button");
		btnNewButton_1_1.setBounds(0, 139, 74, 53);
		panel.add(btnNewButton_1_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(97, 34, 228, 218);
		contentPane.add(scrollPane);
		
		
		JPanel panel_1 = new JPanel();
		scrollPane.setViewportView(panel_1);
		panel_1.setLayout(null);
		panel_1.setPreferredSize(new java.awt.Dimension(220, 400)); 
		
		JButton btnNewButton_2 = new JButton("New button");
		btnNewButton_2.setBounds(10, 11, 88, 65);
		panel_1.add(btnNewButton_2);
		
		JButton btnNewButton_2_1 = new JButton("New button");
		btnNewButton_2_1.setBounds(108, 11, 88, 65);
		panel_1.add(btnNewButton_2_1);
		
		JButton btnNewButton_2_1_1 = new JButton("New button");
		btnNewButton_2_1_1.setBounds(10, 87, 88, 65);
		panel_1.add(btnNewButton_2_1_1);
		
		JButton btnNewButton_2_1_1_1 = new JButton("New button");
		btnNewButton_2_1_1_1.setBounds(108, 87, 88, 65);
		panel_1.add(btnNewButton_2_1_1_1);
		
		JButton btnNewButton_2_1_1_1_1 = new JButton("New button");
		btnNewButton_2_1_1_1_1.setBounds(10, 163, 88, 65);
		panel_1.add(btnNewButton_2_1_1_1_1);
		
		JButton btnNewButton_2_1_1_1_1_1 = new JButton("New button");
		btnNewButton_2_1_1_1_1_1.setBounds(108, 163, 88, 65);
		panel_1.add(btnNewButton_2_1_1_1_1_1);
		
		JButton btnNewButton_2_1_1_1_1_1_1 = new JButton("New button");
		btnNewButton_2_1_1_1_1_1_1.setBounds(10, 239, 88, 65);
		panel_1.add(btnNewButton_2_1_1_1_1_1_1);
		
		JButton btnNewButton_2_1_1_1_1_1_1_1 = new JButton("New button");
		btnNewButton_2_1_1_1_1_1_1_1.setBounds(108, 239, 88, 65);
		panel_1.add(btnNewButton_2_1_1_1_1_1_1_1);
		
		JButton btnNewButton_2_1_1_1_1_1_1_1_1 = new JButton("New button");
		btnNewButton_2_1_1_1_1_1_1_1_1.setBounds(10, 315, 88, 65);
		panel_1.add(btnNewButton_2_1_1_1_1_1_1_1_1);
		
		JButton btnNewButton_2_1_1_1_1_1_1_1_1_1 = new JButton("New button");
		btnNewButton_2_1_1_1_1_1_1_1_1_1.setBounds(108, 315, 88, 65);
		panel_1.add(btnNewButton_2_1_1_1_1_1_1_1_1_1);

	}
}
