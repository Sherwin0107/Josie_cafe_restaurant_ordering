package aiven;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/*
THINGS TO DO -
- orderArea only shows productName and price but does NOT store productName in ordered_items
- input for customer name
*/


public class OrderManaging extends Application {

    private FlowPane itemGrid = new FlowPane();
    private TextArea orderArea = new TextArea();
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> currentOrder = new ArrayList<>();
    private double total = 0;

    @Override
    public void start(Stage primaryStage) {

        // Load products FIRST before building UI
        loadProductsFromDB();

        Button btnSherwinKarrie = new Button("SHERWIN\nKARRIE");
        Button btnXedpogi = new Button("Pogi Xed");
        Button btnCategory3 = new Button("New button");

        btnSherwinKarrie.setPrefSize(100, 53);
        btnXedpogi.setPrefSize(100, 53);
        btnCategory3.setPrefSize(100, 53);

        // Each button loads a range of products
        btnSherwinKarrie.setOnAction(e -> loadCategory(0, 9));
        btnXedpogi.setOnAction(e -> loadCategory(10, 15));
        btnCategory3.setOnAction(e -> loadCategory(16, 19));

        VBox leftPanel = new VBox(17);
        leftPanel.setPadding(new Insets(34, 5, 5, 10));
        leftPanel.getChildren().addAll(btnSherwinKarrie, btnXedpogi, btnCategory3);

        itemGrid.setHgap(10);
        itemGrid.setVgap(11);
        itemGrid.setPadding(new Insets(10));
        itemGrid.setPrefWrapLength(220);
        loadCategory(0, 9); // load first group by default

        ScrollPane scrollPane = new ScrollPane(itemGrid);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefSize(248, 218);
        scrollPane.setFitToWidth(true);

        orderArea.setEditable(false);
        orderArea.setWrapText(true);
        orderArea.setPrefSize(200, 400);
        orderArea.setPromptText("ADD ORDERS....");
        
        Button submitBtn = new Button("Complete Order/s");
        submitBtn.setOnAction(e ->{
        	Random random = new Random(); 
        	String customerNumber = "Customer-" + random.nextInt(0, 5000);
        	try {
				Connection connection = database_Connection.getConnection();
				PreparedStatement statement = connection.prepareStatement("INSERT INTO orders (customerName, total, orderDate) values (?,?,NOW());", Statement.RETURN_GENERATED_KEYS);
				statement.setString(1, customerNumber);
				statement.setDouble(2, total);
				statement.executeUpdate();
				
				ResultSet resultSet = statement.getGeneratedKeys();
				 int orderId = -1;
			        if (resultSet.next()) {
			            orderId = resultSet.getInt(1);
			        }
			        
			        String itemSql = "INSERT INTO ordered_items (orderId, productId, quantity, price) VALUES (?, ?, ?, ?)";
			        PreparedStatement itemPs = connection.prepareStatement(itemSql);

			        for (Product p : currentOrder) {
			            itemPs.setInt(1, orderId);
			            itemPs.setInt(2, p.productId);
			            itemPs.setInt(3, 1);       
			            itemPs.setDouble(4, p.price);
			            itemPs.addBatch();            
			        }
			        itemPs.executeBatch();            

			        
			        orderArea.clear();
			        currentOrder.clear();
			        total = 0;

			        System.out.println("Order saved! Order ID: " + orderId);

			} catch (SQLException e2) {
				
			}
        });
        Button clearBtn = new Button("Clear Orders");
        clearBtn.setOnAction(e -> {
            orderArea.clear();
            total = 0; // reset total too
        });
        clearBtn.setPrefWidth(200);

        VBox orderPanel = new VBox(5, orderArea, submitBtn, clearBtn);
        orderPanel.setPadding(new Insets(5));

        HBox root = new HBox(10);
        root.setPadding(new Insets(5));
        root.getChildren().addAll(leftPanel, scrollPane, orderPanel);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Order Managing");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    // ============== PUT ALL PRODUCTS FROM DATABASE TO LIST ===========
    private void loadProductsFromDB() {
        allProducts.clear();
        try (Connection con = database_Connection.getConnection()) {
            String sql = "SELECT productId, productName, price, quantity FROM products";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                allProducts.add(new Product(
                    rs.getInt("productId"),
                    rs.getString("productName"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    	
    //============== CREATE PRODUCT BUTTONS FROM THE LIST ================
    private void loadCategory(int from, int to) {
        itemGrid.getChildren().clear();

        for (int i = from; i <= to && i < allProducts.size(); i++) {
            Product product = allProducts.get(i);
            Button btn = new Button(product.productName);
            btn.setPrefSize(88, 65);

            btn.setOnAction(e -> {
                orderArea.appendText(product.productName + "  ₱" + product.price + "\n");
                total += product.price;
                currentOrder.add(product);
            });

            itemGrid.getChildren().add(btn);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}




class Product {
    public int productId;
    public String productName;
    public double price;
    public int quantity;

    public Product(int productId, String productName, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
}
