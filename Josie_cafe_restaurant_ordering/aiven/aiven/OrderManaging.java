package aiven;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/*
THINGS TO DO -
- orderArea only shows productName and price but does NOT store productName in ordered_items
- input for customer name ✅ DONE
*/

public class OrderManaging extends JFrame {

    private JPanel itemGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    private JTextArea orderArea = new JTextArea();
    private JTextField customerNameField = new JTextField();   //update: customer name field
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> currentOrder = new ArrayList<>();
    private double total = 0;
    private OrderRepository orderRepository = new OrderRepository(); // handles all DB logic

    public OrderManaging() {
        setTitle("Order Managing");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Load products FIRST before building UI
        loadProductsFromDB();
        buildUI();
    }

    private void buildUI() {

        // ========== LEFT PANEL — category buttons ==========
        JButton btnSherwinKarrie = new JButton("<html><center>SHERWIN<br>KARRIE</center></html>");
        JButton btnXedpogi = new JButton("Pogi Xed");
        JButton btnCategory3 = new JButton("New button");

        btnSherwinKarrie.setPreferredSize(new Dimension(100, 53));
        btnXedpogi.setPreferredSize(new Dimension(100, 53));
        btnCategory3.setPreferredSize(new Dimension(100, 53));

        // Each button loads a range of products
        btnSherwinKarrie.addActionListener(e -> loadCategory(0, 9));
        btnXedpogi.addActionListener(e -> loadCategory(10, 15));
        btnCategory3.addActionListener(e -> loadCategory(16, 19));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(34, 10, 5, 5));
        leftPanel.add(btnSherwinKarrie);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btnXedpogi);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(btnCategory3);

        // ========== CENTER PANEL — product grid ==========
        loadCategory(0, 9); // load first group by default
        JScrollPane scrollPane = new JScrollPane(itemGrid);
        scrollPane.setPreferredSize(new Dimension(248, 218));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // ========== RIGHT PANEL — order panel ==========
        orderArea.setEditable(false);
        orderArea.setLineWrap(true);
        orderArea.setWrapStyleWord(true);
        JScrollPane orderScroll = new JScrollPane(orderArea);
        orderScroll.setPreferredSize(new Dimension(200, 400));

        customerNameField.setPreferredSize(new Dimension(200, 30));
        customerNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton submitBtn = new JButton("Complete Order/s");
        submitBtn.addActionListener(e -> {
            String customerName = customerNameField.getText().trim(); //object customer name field
            if (customerName.isEmpty()) {							//catch if user is not putting customer name	
                JOptionPane.showMessageDialog(this,
                    "Please Enter your Name Tanga",
                    "Missing Customer Name",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int orderId = orderRepository.saveOrder(customerName, total, currentOrder);
                orderArea.setText("");
                currentOrder.clear();
                total = 0;
                customerNameField.setText("");
                System.out.println("Order saved! Order ID: " + orderId);
                JOptionPane.showMessageDialog(this,
                    "Order saved! ID: " + orderId,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e2) {
                e2.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    e2.getMessage(),
                    "Order Failed (Bobo mo Sher)",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton clearBtn = new JButton("Clear Orders");
        clearBtn.addActionListener(e -> {
            orderArea.setText("");
            currentOrder.clear(); // BUG FIX #1 — also clears the order list
            total = 0;
        });

        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel customerLabel = new JLabel("Customer Name:");
        customerLabel.setAlignmentX(LEFT_ALIGNMENT);
        customerNameField.setAlignmentX(LEFT_ALIGNMENT);
        orderPanel.add(customerLabel);
        orderPanel.add(customerNameField);
        
        orderPanel.add(Box.createVerticalStrut(5));
        orderPanel.add(orderScroll);
        orderPanel.add(Box.createVerticalStrut(5));
        orderPanel.add(submitBtn);
        orderPanel.add(Box.createVerticalStrut(5));
        orderPanel.add(clearBtn);

        // ========== ROOT LAYOUT ==========
        JPanel root = new JPanel(new BorderLayout(10, 0));
        root.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        root.add(leftPanel, BorderLayout.WEST);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(orderPanel, BorderLayout.EAST);

        add(root);
    }

    // ============== PUT ALL PRODUCTS FROM DATABASE TO LIST ===========
    private void loadProductsFromDB() {
        allProducts.clear();
        allProducts.addAll(orderRepository.getAllProducts()); // delegates to OrderRepository
    }

    // ============== CREATE PRODUCT BUTTONS FROM THE LIST ================
    private void loadCategory(int from, int to) {
        itemGrid.removeAll();

        for (int i = from; i <= to && i < allProducts.size(); i++) {
            Product product = allProducts.get(i);
            JButton btn = new JButton("<html><center>" + product.productName + "</center></html>");
            btn.setPreferredSize(new Dimension(88, 65));

            btn.addActionListener(e -> {
                orderArea.append(product.productName + "  ₱" + product.price + "\n");
                total += product.price;
                currentOrder.add(product);
            });

            itemGrid.add(btn);
        }

        itemGrid.revalidate();
        itemGrid.repaint();
    }
    
    // ========MAIN METHOD========

    public static void main(String[] args) { 
        SwingUtilities.invokeLater(() -> {
            OrderManaging app = new OrderManaging();
            app.setVisible(true);
        });
    }
}