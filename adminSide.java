package aiven;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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
		
		// ============== PANEL (LOGIN PANEL) ================
		JPanel panel = new JPanel();
		contentPane.add(panel, "Login-Panel"); 
		
		// GRIDBAGLAYOUT FOR RESPONSIVE CENTERING
		panel.setLayout(new GridBagLayout()); 
		
		// CREATE THE GRAY BOX ON LOGIN PAGE
		JPanel loginBox = new JPanel();
		loginBox.setBackground(Color.LIGHT_GRAY); 
		loginBox.setBorder(BorderFactory.createEmptyBorder(50, 70, 50, 70)); // Big padding
		loginBox.setLayout(new GridBagLayout()); 

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5); 
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// TITLE
		JLabel titleLabel = new JLabel("Josie's Cafe Admin", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Serif", Font.ITALIC, 24)); 
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 25, 5); 
		loginBox.add(titleLabel, gbc);

		gbc.insets = new Insets(5, 5, 5, 5);

		// USERNAME LABEL
		JLabel lblUsername = new JLabel("Username");
		gbc.gridx = 0;
		gbc.gridy = 1;
		loginBox.add(lblUsername, gbc);
		
		// USERNAME TEXTFIELD
		textField = new JTextField();
		textField.setColumns(15); 
		gbc.gridx = 0;
		gbc.gridy = 2;
		loginBox.add(textField, gbc);
		
		// PASSWORD LABEL
		JLabel passJLabel = new JLabel("Password");
		gbc.gridx = 0;
		gbc.gridy = 3;
		loginBox.add(passJLabel, gbc);
		
		// PASSWORD TEXTFIELD
		passwordField = new JPasswordField();
		passwordField.setColumns(15);
		gbc.gridx = 0;
		gbc.gridy = 4;
		loginBox.add(passwordField, gbc);
		
		// LOGIN BUTTON
		JButton btnNewButton = new JButton("LOGIN");
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.insets = new Insets(20, 5, 5, 5); 
		gbc.fill = GridBagConstraints.NONE;   
		loginBox.add(btnNewButton, gbc);

		GridBagConstraints gbcMain = new GridBagConstraints();
		gbcMain.gridx = 0;
		gbcMain.gridy = 0;
		panel.add(loginBox, gbcMain);
		
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
		contentPane.add(panel1, "Choosing-Panel");   
		panel1.setBackground(Color.gray);
		
		// GRIDBAGLAYOUT ULIT FOR CENTERING TSAKA RESPONSIVE
		panel1.setLayout(new GridBagLayout());
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
	
		JLabel choosingTitle = new JLabel("Josie's Cafe Admin");
		choosingTitle.setFont(new Font("Serif", Font.ITALIC, 42)); 
		choosingTitle.setForeground(Color.WHITE); 
		
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 0;
		gbcPanel1.insets = new Insets(0, 0, 60, 0); 
		gbcPanel1.anchor = GridBagConstraints.CENTER;
		panel1.add(choosingTitle, gbcPanel1);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false); 
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 0)); 

		JButton updateButton = new JButton("UPDATE MENU");
		updateButton.setPreferredSize(new Dimension(178, 40)); 
		buttonPanel.add(updateButton);
		
		JButton ordersButton = new JButton("SHOW ORDERS");
		ordersButton.setPreferredSize(new Dimension(178, 40));
		buttonPanel.add(ordersButton);
		
		gbcPanel1.gridy = 1;
		gbcPanel1.insets = new Insets(0, 0, 0, 0); 
		panel1.add(buttonPanel, gbcPanel1);
		
		updateButton.addActionListener(e1 -> {
			CardLayout cl = (CardLayout) contentPane.getLayout();
		    cl.show(contentPane, "Update Panel");
		});
		
		ordersButton.addActionListener(e2 -> {
			CardLayout cl = (CardLayout) contentPane.getLayout();
		    cl.show(contentPane, "Orders Panel");
		});
		
		// ADD PANELS TO CARDLAYOUT
		CardLayout cardLayout = (CardLayout) contentPane.getLayout();
		UpdatePanel panel2 = new UpdatePanel(cardLayout, contentPane);
		contentPane.add(panel2, "Update Panel");  
		
		CardLayout cardLayout1 = (CardLayout) contentPane.getLayout();
		OrderPanel panel3 = new OrderPanel(cardLayout1, contentPane); 
		contentPane.add(panel3, "Orders Panel");
	}
}

// ========================== ORDER PANEL CLASS =================================
class OrderPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JPanel ordersCardsPanel;
    private JTextField totalField;
    private JComboBox<String> dateDropdown;

    public OrderPanel(CardLayout cardLayout, JPanel contentPane) {
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY);

        // TOP PANEL, HEADER AND BACK BUTTON
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton backBtn = new JButton("BACK");
        backBtn.setPreferredSize(new Dimension(100, 30));
        backBtn.addActionListener(e -> cardLayout.show(contentPane, "Choosing-Panel"));
        topPanel.add(backBtn, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Show Orders", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.ITALIC, 32));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 30));
        spacer.setOpaque(false);
        topPanel.add(spacer, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);


        // CENTER PANEL, CARDS
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        scrollPane.getViewport().setBackground(Color.LIGHT_GRAY);
        add(scrollPane, BorderLayout.CENTER);

        // Store reference for later updates
        ordersCardsPanel = cardsPanel;

        // BOTTOM PANEL, CONTROLS
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottomPanel.setBackground(Color.LIGHT_GRAY);
        
        
        JLabel totalOrder = new JLabel("total pending Orders:");
        totalOrder.setFont(new Font("Serif", Font.BOLD, 16));
        bottomPanel.add(totalOrder);
        totalField = new JTextField(4);
        totalField.setEditable(false);
        bottomPanel.add(totalField);
        
        JLabel filterLabel = new JLabel("Filter by Date:");
        filterLabel.setFont(new Font("Serif", Font.BOLD, 16));
        bottomPanel.add(filterLabel);

        dateDropdown = new JComboBox<>();
        dateDropdown.setPreferredSize(new Dimension(150, 30));
        dateDropdown.addItem("All Dates");
        bottomPanel.add(dateDropdown);

        JButton refreshBtn = new JButton("REFRESH");
        refreshBtn.setPreferredSize(new Dimension(100, 30));
        bottomPanel.add(refreshBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // ACTION LISTENERS FOR DROPDOWNS
        dateDropdown.addActionListener(e -> {
            String selected = (String) dateDropdown.getSelectedItem();
            if (selected != null) {
                loadOrders(selected);
            }
        });

        refreshBtn.addActionListener(e -> {
            String selected = (String) dateDropdown.getSelectedItem();
            if (selected != null) {
                loadOrders(selected);
            }
        });

        // INITIALIZE DATA
        loadDatesIntoDropdown();
        loadOrders("All Dates");
    }

    private void loadDatesIntoDropdown() {
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT DATE(orderDate) AS orderDay FROM orders ORDER BY orderDay DESC")) {
            while (rs.next()) {
                String orderDay = rs.getString("orderDay");
                if (orderDay != null) {
                    dateDropdown.addItem(orderDay);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadOrders(String dateFilter) {
        ordersCardsPanel.removeAll();
        
        String countQuery = "SELECT COUNT(o.orderId) as totalCount " +
                            "FROM orders o WHERE orderStatus = 'pending' ";
        
        if (!dateFilter.equals("All Dates")) {
            countQuery += "AND DATE(o.orderDate) = ? ";
        }
        
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement countPs = con.prepareStatement(countQuery)) {
            
            if (!dateFilter.equals("All Dates")) {
                countPs.setString(1, dateFilter);
            }
            
            try (ResultSet countRs = countPs.executeQuery()) {
                if (countRs.next()) {
                    totalField.setText(String.valueOf(countRs.getInt("totalCount")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        
        //for cards 
        String query = "SELECT DISTINCT o.orderId, o.orderDate, o.orderStatus " +
                       "FROM orders o " +
                       "JOIN ordered_items oi ON o.orderId = oi.orderId WHERE orderStatus = 'pending' ";
        


        if (!dateFilter.equals("All Dates")) {
            query += "WHERE DATE(o.orderDate) = ? ";
        }
        query += "ORDER BY o.orderDate DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            
            if (!dateFilter.equals("All Dates")) {
                ps.setString(1, dateFilter);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("orderId");
                    String orderDate = rs.getString("orderDate");
                    String orderStatus = rs.getString("orderStatus");
                    
                    // Create main order box
                    JPanel orderBox = new JPanel();
                    orderBox.setLayout(new BoxLayout(orderBox, BoxLayout.Y_AXIS));
                    orderBox.setBackground(Color.WHITE);
                    orderBox.setBorder(BorderFactory.createCompoundBorder(
                    	    BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                    	    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    	));
                    
                    // Order header
                    JLabel orderHeader = new JLabel("Order #" + orderId + " - " + orderDate);
                    orderHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
                    orderBox.add(orderHeader);
                    
                    orderBox.add(Box.createVerticalStrut(8));
                    
                    // Get all items for this order
                    String itemQuery = "SELECT p.productName, oi.quantity, p.price, (oi.quantity * p.price) AS total " +
                                       "FROM ordered_items oi " +
                                       "JOIN products p ON oi.productId = p.productId " +
                                       "WHERE oi.orderId = ?";
                    
                    try (PreparedStatement itemPs = con.prepareStatement(itemQuery)) {
                        itemPs.setInt(1, orderId);
                        try (ResultSet itemRs = itemPs.executeQuery()) {
                            double orderTotal = 0;
                            while (itemRs.next()) {
                                String productName = itemRs.getString("productName");
                                int quantity = itemRs.getInt("quantity");
                                double price = itemRs.getDouble("price");
                                double itemTotal = itemRs.getDouble("total");
                                orderTotal += itemTotal;
                                
                                // Item line
                                JPanel itemLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
                                itemLine.setOpaque(false);
                                itemLine.add(new JLabel(productName));
                                itemLine.add(new JLabel("x" + quantity));
                                itemLine.add(new JLabel("$" + String.format("%.2f", price)));
                                itemLine.add(new JLabel("= $" + String.format("%.2f", itemTotal)));
                                
                                orderBox.add(itemLine);
                            }
                            
                            orderBox.add(Box.createVerticalStrut(8));
                            
                            // Order total
                            JLabel totalLabel = new JLabel("Order Total: $" + String.format("%.2f", orderTotal));
                            totalLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
                            orderBox.add(totalLabel);
                            JLabel statusLabel = new JLabel("Order Status:" + orderStatus);
                            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
                            orderBox.add(statusLabel);
                            
                            JButton acceptJButton = new JButton("Complete");
                            acceptJButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                            orderBox.add(acceptJButton);
                            
                            acceptJButton.addActionListener(e ->{
                            	completeOrder(orderId);
                            });
                        }
                    }
                    
                    ordersCardsPanel.add(orderBox);
                    ordersCardsPanel.add(Box.createVerticalStrut(15));
                }
            }
            ordersCardsPanel.revalidate();
            ordersCardsPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    public void completeOrder(int orderId) {
    	String query = "UPDATE orders SET orderStatus = ? where orderId = ?";
    	try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
    		
    		ps.setString(1, "completed");
    		ps.setInt(2, orderId);
    		ps.executeUpdate();
			
    		String selected = (String) dateDropdown.getSelectedItem();
            loadOrders(selected);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

    


// ========================= UPDATE PANEL CLASS =================================
class UpdatePanel extends JPanel {
    private static final long serialVersionUID = 2L;

    public UpdatePanel(CardLayout cardLayout, JPanel contentPane) {
        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY);

        // TOP PANEL, BUTTONS, HEADER
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(Color.LIGHT_GRAY);

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JButton backBtn = new JButton("BACK");
        backBtn.setPreferredSize(new Dimension(100, 30));
        backBtn.addActionListener(e -> cardLayout.show(contentPane, "Choosing-Panel"));
        headerPanel.add(backBtn, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Update Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.ITALIC, 32));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(100, 30));
        spacer.setOpaque(false);
        headerPanel.add(spacer, BorderLayout.EAST);

        topContainer.add(headerPanel);

        // INPUT PANEL
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(Color.LIGHT_GRAY);

        inputPanel.add(new JLabel("ID:"));
        JTextField tfId = new JTextField(4);
        inputPanel.add(tfId);

        inputPanel.add(new JLabel("Name:"));
        JTextField tfName = new JTextField(12);
        inputPanel.add(tfName);

        inputPanel.add(new JLabel("Qty:"));
        JTextField tfQty = new JTextField(4);
        inputPanel.add(tfQty);

        inputPanel.add(new JLabel("Price:"));
        JTextField tfPrice = new JTextField(6);
        inputPanel.add(tfPrice);

        inputPanel.add(new JLabel("Category:"));
        JTextField tfCategory = new JTextField(12);
        inputPanel.add(tfCategory);

        topContainer.add(inputPanel);

        // BUTTON PANEL
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JButton btnAdd = new JButton("ADD");
        btnAdd.setPreferredSize(new Dimension(90, 30));
        buttonPanel.add(btnAdd);

        JButton btnUpdate = new JButton("UPDATE");
        btnUpdate.setPreferredSize(new Dimension(90, 30));
        buttonPanel.add(btnUpdate);

        JButton btnDelete = new JButton("DELETE");
        btnDelete.setPreferredSize(new Dimension(90, 30));
        buttonPanel.add(btnDelete);

        JButton btnRefresh = new JButton("REFRESH");
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(btnRefresh);

        topContainer.add(buttonPanel);
        add(topContainer, BorderLayout.NORTH);

        // CENTER PANEL, TABLE
        String[] columns = {"ID", "Name", "Quantity", "Price", "Category"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // DATABASE AND ACTIONLISTENERS,
        Runnable loadData = () -> {
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
        };

        // INITIALIZED DATA
        loadData.run();

        btnAdd.addActionListener(e -> {
             String name = tfName.getText().trim();
             String qty = tfQty.getText().trim();
             String price = tfPrice.getText().trim();
             String category = tfCategory.getText().trim();
             
             if (name.isEmpty() || qty.isEmpty() || price.isEmpty() || category.isEmpty()) {
                 JOptionPane.showMessageDialog(null, "Please fill in all fields!");
                 return;
             }
                
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("INSERT INTO products (productName, quantity, price, category) VALUES (?, ?, ?, ?)");
                
                ps.setString(1, name);
                ps.setInt(2, Integer.parseInt(qty));
                ps.setDouble(3, Double.parseDouble(price));
                ps.setString(4, category);
                ps.executeUpdate();
               
                tfName.setText("");
                tfQty.setText("");
                tfPrice.setText("");
                tfCategory.setText("");

                loadData.run();
                JOptionPane.showMessageDialog(null, "Product added successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        });
        
        btnUpdate.addActionListener(e -> {
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
                    loadData.run();
                } else {
                    JOptionPane.showMessageDialog(null, "No product found with that ID!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        });
        
        btnDelete.addActionListener(e -> {
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
                    JOptionPane.showMessageDialog(null, "Deleted successfully!");
                    tfId.setText(""); 
                    loadData.run(); 
                } else {
                    JOptionPane.showMessageDialog(null, "No product found!");
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        });
        
        btnRefresh.addActionListener(e -> loadData.run());
    }
}
