package bobosinoe;

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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OrderManaging extends Application {

    private FlowPane itemGrid = new FlowPane();
    private TextArea orderArea = new TextArea();
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> currentOrder = new ArrayList<>();
    private double total = 0;

    private Button submitBtn = new Button("COMPLETE ORDER · ₱0.00");
    private Label badge = new Label("0");

    @Override
    public void start(Stage primaryStage) {
        loadProductsFromDB();

        // --- LEFT PANEL (Categories) ---
        Button btnCategory1 = new Button("SHERWIN\nKARRIE");
        Button btnCategory2 = new Button("Pogi Xed");
        Button btnCategory3 = new Button("New button");
        
        // Add specific style class for left buttons
        btnCategory1.getStyleClass().add("left-panel-button");
        btnCategory2.getStyleClass().add("left-panel-button");
        btnCategory3.getStyleClass().add("left-panel-button");

        btnCategory1.setOnAction(e -> loadCategory(0, 9));
        btnCategory2.setOnAction(e -> loadCategory(10, 15));
        btnCategory3.setOnAction(e -> loadCategory(16, 19));

        VBox leftPanel = new VBox(17, btnCategory1, btnCategory2, btnCategory3);
        leftPanel.setPadding(new Insets(34, 5, 5, 10));
        leftPanel.setAlignment(Pos.TOP_LEFT); // Align panel contents to left

        // --- MIDDLE PANEL (Product Grid) ---
        itemGrid.setHgap(10);
        itemGrid.setVgap(11);
        itemGrid.setPadding(new Insets(10));
        itemGrid.setPrefWrapLength(220);
        itemGrid.setAlignment(Pos.TOP_LEFT); // Align grid items to left
        loadCategory(0, 9);

        ScrollPane scrollPane = new ScrollPane(itemGrid);
        scrollPane.setPrefSize(248, 218);
        scrollPane.setFitToWidth(true);

        // --- RIGHT PANEL (Order Details) ---
        orderArea.setEditable(false);
        orderArea.setPrefSize(200, 400);

        StackPane submitContainer = new StackPane();
        submitBtn.setPrefSize(200, 50);
        submitBtn.getStyleClass().add("submit-button");

        badge.getStyleClass().add("order-badge");
        badge.setTranslateX(5); 
        badge.setTranslateY(-5);
        badge.setVisible(false);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        submitContainer.getChildren().addAll(submitBtn, badge);

        submitBtn.setOnAction(e -> {
            if (!currentOrder.isEmpty()) showCheckoutSummary();
        });

        Button clearBtn = new Button("Clear Orders");
        clearBtn.getStyleClass().add("clear-button");
        clearBtn.setPrefWidth(200);
        clearBtn.setOnAction(e -> {
            orderArea.clear();
            currentOrder.clear();
            total = 0;
            refreshUI();
            loadCategory(0, 9);
        });

        VBox orderPanel = new VBox(15, orderArea, submitContainer, clearBtn);
        orderPanel.setPadding(new Insets(5));

        // --- MAIN ROOT LAYOUT ---
        HBox root = new HBox(10, leftPanel, scrollPane, orderPanel);
        root.setPadding(new Insets(5));
        root.setAlignment(Pos.TOP_LEFT); // Ensure everything hugs the left side

        Scene scene = new Scene(root, 900, 600);
        
        // Link the CSS file
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        primaryStage.setTitle("Order Managing");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshUI() {
        submitBtn.setText(String.format("COMPLETE ORDER · ₱%.2f", total));
        badge.setText(String.valueOf(currentOrder.size()));
        badge.setVisible(!currentOrder.isEmpty());
    }

    private void showCheckoutSummary() {
        itemGrid.getChildren().clear();
        VBox summaryBox = new VBox(10);
        summaryBox.setPadding(new Insets(10));
        
        Label title = new Label("ORDER SUMMARY");
        title.getStyleClass().add("summary-title");
        summaryBox.getChildren().add(title);

        for (Product p : currentOrder) {
            summaryBox.getChildren().add(new Label("• " + p.productName + " - ₱" + p.price));
        }

        Button confirmFinalBtn = new Button("CONFIRM & SAVE TO DB");
        confirmFinalBtn.getStyleClass().add("confirm-button");
        confirmFinalBtn.setPrefWidth(200);
        confirmFinalBtn.setOnAction(e -> saveOrderToDatabase());
        
        summaryBox.getChildren().addAll(new Label("----------------------"), confirmFinalBtn);
        itemGrid.getChildren().add(summaryBox);
    }

    private void saveOrderToDatabase() {
        Random random = new Random();
        String customerNumber = "Customer-" + random.nextInt(0, 5000);
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO orders (customerName, total, orderDate) VALUES (?,?,NOW())",
                 Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, customerNumber);
            statement.setDouble(2, total);
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            int orderId = -1;
            if (resultSet.next()) orderId = resultSet.getInt(1);

            String itemSql = "INSERT INTO ordered_items (orderId, productId, quantity, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement itemPs = connection.prepareStatement(itemSql)) {
                List<Integer> added = new ArrayList<>();
                for (Product p : currentOrder) {
                    if (!added.contains(p.productId)) {
                        int qty = (int) currentOrder.stream().filter(pr -> pr.productId == p.productId).count();
                        itemPs.setInt(1, orderId);
                        itemPs.setInt(2, p.productId);
                        itemPs.setInt(3, qty);
                        itemPs.setDouble(4, p.price);
                        itemPs.addBatch();
                        added.add(p.productId);
                    }
                }
                itemPs.executeBatch();
            }

            orderArea.clear();
            currentOrder.clear();
            total = 0;
            refreshUI();
            loadCategory(0, 9);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Order #" + orderId + " saved!");
            alert.showAndWait();

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadProductsFromDB() {
        allProducts.clear();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT productId, productName, price, quantity FROM products");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                allProducts.add(new Product(rs.getInt("productId"), rs.getString("productName"), rs.getDouble("price"), rs.getInt("quantity")));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadCategory(int from, int to) {
        itemGrid.getChildren().clear();
        for (int i = from; i <= to && i < allProducts.size(); i++) {
            Product product = allProducts.get(i);
            Button btn = new Button(product.productName + "\n₱" + product.price);
            btn.setPrefSize(88, 65);
            
            // Add specific style class for middle product buttons
            btn.getStyleClass().add("middle-panel-button"); 
            
            btn.setOnAction(e -> {
                orderArea.appendText(product.productName + "  ₱" + product.price + "\n");
                total += product.price;
                currentOrder.add(product);
                refreshUI();
            });
            itemGrid.getChildren().add(btn);
        }
    }

    public static void main(String[] args) { launch(args); }
}

class Product {
    public int productId; public String productName; public double price; public int quantity;
    public Product(int productId, String productName, double price, int quantity) {
        this.productId = productId; this.productName = productName; this.price = price; this.quantity = quantity;
    }
}
