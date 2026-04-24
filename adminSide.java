package aiven;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

// DIPA TAPOS ITO <<<===============================================================================


public class adminSide extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					adminSide frame = new adminSide();
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
	public adminSide() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 841, 531);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, "Login-Panel"); // PANEL (LOGIN PANEL)
		panel.setLayout(null);
		textField = new JTextField();
		textField.setBounds(274, 148, 187, 25);
		panel.add(textField);
		textField.setColumns(10);
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(274, 134, 187, 15);
		panel.add(lblUsername);
		
		
		passwordField = new JPasswordField();
		passwordField.setBounds(274, 199, 187, 31);
		panel.add(passwordField);
		JLabel passJLabel = new JLabel("Password");
		passJLabel.setBounds(274, 184, 187, 15);
		panel.add(passJLabel);
		
		JButton btnNewButton = new JButton("LOGIN");
		btnNewButton.setBounds(284, 241, 170, 25);
		panel.add(btnNewButton);
		
		btnNewButton.addActionListener(e -> {
			
			String username = textField.getText();
		    String password = new String(passwordField.getPassword());
		    
		    try {
				Connection connection = DatabaseConnection.getConnection();
				PreparedStatement pStatement = connection.prepareStatement(
					    "SELECT * FROM logins WHERE username = ? AND passwords = ?"
					);
					pStatement.setString(1, username);
					pStatement.setString(2, password);

					ResultSet rs = pStatement.executeQuery();

					if (rs.next()) {
					    // Credentials matched in DB
					    CardLayout cl = (CardLayout) contentPane.getLayout();
					    cl.show(contentPane, "Choosing-Panel");
					} else {
					    JOptionPane.showMessageDialog(null, "Invalid credentials!");
					}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		    
		  });
		
		//============== PANEL 1 (CHOOSING PANEL) ================
		
		JPanel panel1 = new JPanel();
		contentPane.add(panel1, "Choosing-Panel");   // PANEL 1 (CHOOSING PANEL)
		panel1.setBackground(Color.gray);
		panel1.setLayout(null);
	
	
	    JButton updateButton = new JButton("UPDATE MENU");
		updateButton.setBounds(30, 11, 178, 33);
		panel1.add(updateButton);
		
		
		JButton ordersButton = new JButton("SHOW ORDERS");
		ordersButton.setBounds(260, 11, 178, 33);
		panel1.add(ordersButton);
		
		updateButton.addActionListener(e1 -> {
			CardLayout cl = (CardLayout) contentPane.getLayout();
		    cl.show(contentPane, "Update Panel");
		});
		
		ordersButton.addActionListener(e2 -> {
			CardLayout cl = (CardLayout) contentPane.getLayout();
		    cl.show(contentPane, "Orders Panel");
		});
		
		CardLayout cardLayout = (CardLayout) contentPane.getLayout();
		UpdatePanel panel2 = new UpdatePanel(cardLayout, contentPane);
		contentPane.add(panel2, "Update Panel");  // ==== panel 2 (update panel) ============================
		panel2.setLayout(null);
		
	
		CardLayout cardLayout1 = (CardLayout) contentPane.getLayout();
		OrderPanel panel3 = new OrderPanel(cardLayout1, contentPane); //====== panel 3 ( order panel )
		contentPane.add(panel3, "Orders Panel");
		panel3.setLayout(null);

	}
}


class OrderPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OrderPanel(CardLayout cardLayout, JPanel contentPane) {
        setLayout(null);
        setBackground(Color.WHITE);

        // BACK BUTTON
        JButton backBtn = new JButton("BACK");
        backBtn.setBounds(10, 10, 80, 25);
        add(backBtn);
        backBtn.addActionListener(e -> cardLayout.show(contentPane, "Choosing-Panel"));

        // TABLE
        String[] columns = {"Order ID", "Product Name", "Quantity", "Price", "Total"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 50, 700, 300);
        add(scrollPane);

        // LOAD DATA
        loadOrders(tableModel);

        // REFRESH BUTTON
        JButton refreshBtn = new JButton("REFRESH");
        refreshBtn.setBounds(100, 10, 100, 25);
        add(refreshBtn);

        refreshBtn.addActionListener(e -> loadOrders(tableModel));
    }

    private void loadOrders(DefaultTableModel tableModel) {
        try {
            Connection con = DatabaseConnection.getConnection();
            tableModel.setRowCount(0);

            String query = "SELECT oi.orderId, p.productName, oi.quantity, p.price, (oi.quantity * p.price) AS total " +
                           "FROM ordered_items oi " +
                           "JOIN products p ON oi.productId = p.productId";

            ResultSet rs = con.createStatement().executeQuery(query);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("orderId"),
                    rs.getString("productName"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("total")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }
}



class UpdatePanel extends JPanel {
	private static final long serialVersionUID = 2L;

    public UpdatePanel(CardLayout cardLayout, JPanel contentPane) {
        setLayout(null);
        setBackground(Color.WHITE);

        JButton backBtn = new JButton("BACK");
        backBtn.setBounds(10, 10, 80, 25);
        add(backBtn);
        backBtn.addActionListener(e -> cardLayout.show(contentPane, "Choosing-Panel"));
        
        // Input fields ========================================================================================
		JLabel lblId = new JLabel("ID:");
		lblId.setBounds(10, 45, 50, 25);
		add(lblId);
		JTextField tfId = new JTextField();
		tfId.setBounds(60, 45, 80, 25);
		add(tfId);

		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(150, 45, 50, 25);
		add(lblName);
		JTextField tfName = new JTextField();
		tfName.setBounds(200, 45, 120, 25);
		add(tfName);

		JLabel lblQty = new JLabel("Qty:");
		lblQty.setBounds(330, 45, 40, 25);
		add(lblQty);
		JTextField tfQty = new JTextField();
		tfQty.setBounds(370, 45, 60, 25);
		add(tfQty);

		JLabel lblPrice = new JLabel("Price:");
		lblPrice.setBounds(440, 45, 50, 25);
		add(lblPrice);
		JTextField tfPrice = new JTextField();
		tfPrice.setBounds(490, 45, 80, 25);
		add(tfPrice);
		
		JLabel lblCategory = new JLabel("Category:");
        lblCategory.setBounds(580, 45, 70, 25);
        add(lblCategory);
        JTextField tfCategory = new JTextField();
        tfCategory.setBounds(650, 45, 120, 25);
        add(tfCategory);

		// CRUD Buttons
		JButton btnAdd = new JButton("ADD");
		btnAdd.setBounds(10, 80, 80, 25);
		add(btnAdd);

		JButton btnUpdate = new JButton("UPDATE");
		btnUpdate.setBounds(100, 80, 90, 25);
		add(btnUpdate);

		JButton btnDelete = new JButton("DELETE");
		btnDelete.setBounds(200, 80, 90, 25);
		add(btnDelete);

		JButton btnRefresh = new JButton("REFRESH");
		btnRefresh.setBounds(300, 80, 100, 25);
		add(btnRefresh);

		// Table
		String[] columns = {"ID", "Name", "Quantity", "Price", "Category"};
		DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
		JTable table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 115, 700, 290);
		add(scrollPane);

		// Load data
		
		try {
		    Connection con = DatabaseConnection.getConnection();
		    ResultSet rs = con.createStatement().executeQuery("SELECT * FROM products");
		    while (rs.next()) {
		        tableModel.addRow(new Object[]{
		            rs.getInt("productId"),
		            rs.getString("productName"),
		            rs.getInt("quantity"),
		            rs.getDouble("price"),
		            rs.getString("category")
		        });
		    }
		} catch (SQLException ex) {
		    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
		}
		
	//CRUDS =====================================================================================================
		
		btnAdd.addActionListener(e->{
			 String name = tfName.getText().trim();
	         String qty = tfQty.getText().trim();
	         String price = tfPrice.getText().trim();
	         String category = tfCategory.getText().trim();
	         
	         if (name.isEmpty()||qty.isEmpty()|| price.isEmpty()||category.isEmpty()) {
	        	 JOptionPane.showMessageDialog(null, "Please fill in all fields!");
	                return;
			}
	            
			try {
			    Connection con = DatabaseConnection.getConnection();
			    PreparedStatement ps = con.prepareStatement( "INSERT INTO products (productName, quantity, price, category) VALUES (?, ?, ?, ?)");
			    
			    ps.setString(1, name);
                ps.setInt(2, Integer.parseInt(qty));
                ps.setDouble(3, Double.parseDouble(price));
                ps.setString(4, category);
                ps.executeUpdate();
			   
                tfName.setText("");
                tfQty.setText("");
                tfPrice.setText("");
                tfCategory.setText("");

			    
			} catch (SQLException ex) {
			    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
			}
		});
		
		btnUpdate.addActionListener(e->{
			String id = tfId.getText().trim();
		    String name = tfName.getText().trim();
		    String qty = tfQty.getText().trim();
		    String price = tfPrice.getText().trim();
		    String category = tfCategory.getText().trim();

		    if (id.isEmpty() || name.isEmpty() || qty.isEmpty() || price.isEmpty() || category.isEmpty()) {
		        JOptionPane.showMessageDialog(null, "Please fill in ALL fields including ID!");
		        return;
		    }

		    try {
		        Connection con = DatabaseConnection.getConnection();
		        PreparedStatement ps = con.prepareStatement(
		            "UPDATE products SET productName=?, quantity=?, price=?, category=? WHERE productId=?"
		        );
		        ps.setString(1, name);
		        ps.setInt(2, Integer.parseInt(qty));
		        ps.setDouble(3, Double.parseDouble(price));
		        ps.setString(4, category);
		        ps.setInt(5, Integer.parseInt(id));

		        int rows = ps.executeUpdate();
		        if (rows > 0) {
		            JOptionPane.showMessageDialog(null, "Product updated successfully!");
		            tfId.setText("");
		            tfName.setText("");
		            tfQty.setText("");
		            tfPrice.setText("");
		            tfCategory.setText("");
		        } else {
		            JOptionPane.showMessageDialog(null, "No product found with that ID!");
		        }
		    } catch (SQLException ex) {
		        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
		    }
		});
		
		btnDelete.addActionListener(e->{
			String id = tfId.getText().trim();
			
			if (id.isEmpty()) {
		        JOptionPane.showMessageDialog(null, "Please enter ID!");
		        return;
		    }
		    
		    try {
		    	Connection connection = DatabaseConnection.getConnection();

		    	PreparedStatement deleteOrders = connection.prepareStatement(
		    	    "DELETE FROM ordered_items WHERE productId=?"
		    	);
		    	deleteOrders.setInt(1, Integer.parseInt(id));
		    	deleteOrders.executeUpdate();

		    	PreparedStatement deleteProduct = connection.prepareStatement(
		    	    "DELETE FROM products WHERE productId=?"
		    	);
		    	deleteProduct.setInt(1, Integer.parseInt(id));

		    	int rows = deleteProduct.executeUpdate();

		    	if (rows > 0) {
		    	    JOptionPane.showMessageDialog(null, "Deleted including related orders!");
		    	} else {
		    	    JOptionPane.showMessageDialog(null, "No product found!");
		    	}
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		});
		
		btnRefresh.addActionListener(e -> {
            try { 
                Connection con = DatabaseConnection.getConnection();
                tableModel.setRowCount(0);
                ResultSet rs = con.createStatement().executeQuery("SELECT * FROM products");
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("productId"),
                        rs.getString("productName"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getString("category")
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        });
    }
}



