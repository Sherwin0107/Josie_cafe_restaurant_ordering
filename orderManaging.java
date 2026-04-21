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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

        // Left Panel Category Buttons
        Button btnCategory1 = new Button("SHERWIN\nKARRIE");
        Button btnCategory2 = new Button("Pogi Xed");
        Button btnCategory3 = new Button("New button");

        btnCategory1.setPrefSize(100, 53);
        btnCategory2.setPrefSize(100, 53);
        btnCategory3.setPrefSize(100, 53);

        // Action: Load menu and ensure we are in "Menu Mode"
        btnCategory1.setOnAction(e -> loadCategory(0, 9));
        btnCategory2.setOnAction(e -> loadCategory(10, 15));
        btnCategory3.setOnAction(e -> loadCategory(16, 19));

        VBox leftPanel = new VBox(17);
        leftPanel.setPadding(new Insets(34, 5, 5, 10));
        leftPanel.getChildren().addAll(btnCategory1, btnCategory2, btnCategory3);

        // Middle Panel (Menu / Checkout Display)
        itemGrid.setHgap(10);
        itemGrid.setVgap(11);
        itemGrid.setPadding(new Insets(10));
        itemGrid.setPrefWrapLength(220);
        loadCategory(0, 9); // Initial load

        ScrollPane scrollPane = new ScrollPane(itemGrid);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefSize(248, 218);
        scrollPane.setFitToWidth(true);

        // Right Order Panel
        orderArea.setEditable(false);
        orderArea.setPrefSize(200, 400);
        orderArea.setPromptText("ADD ORDERS....");

        // --- SUBMIT BUTTON & BADGE ---
        StackPane submitContainer = new StackPane();
        submitBtn.setPrefWidth(200);
        submitBtn.setPrefHeight(50);
        submitBtn.setStyle("-fx-background-radius: 25; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        badge.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 50; -fx-min-width: 22; -fx-min-height: 22; -fx-alignment: center; -fx-font-size: 11; -fx-font-weight: bold;");
        badge.setTranslateX(5); 
        badge.setTranslateY(-5);
        badge.setVisible(false);
        StackPane.setAlignment(badge, Pos.TOP_RIGHT);
        submitContainer.getChildren().addAll(submitBtn, badge);

        // ACTION: SHOW ORDER SUMMARY IN MENU AREA
        submitBtn.setOnAction(e -> {
            if (currentOrder.isEmpty()) return;
            showCheckoutSummary();
        });

        Button clearBtn = new Button("Clear Orders");
        clearBtn.setPrefWidth(200);
        clearBtn.setOnAction(e -> {
            orderArea.clear();
            currentOrder.clear();
            total = 0;
            refreshUI();
            loadCategory(0, 9); // Go back to menu
        });

        VBox orderPanel = new VBox(15, orderArea, submitContainer, clearBtn);
        orderPanel.setPadding(new Insets(5));

        HBox root = new HBox(10);
        root.setPadding(new Insets(5));
        root.getChildren().addAll(leftPanel, scrollPane, orderPanel);

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Order Managing");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshUI() {
        submitBtn.setText(String.format("COMPLETE ORDER · ₱%.2f", total));
        badge.setText(String.valueOf(currentOrder.size()));
        badge.setVisible(!currentOrder.isEmpty());
    }

    // NEW METHOD: Replaces the menu grid with the list of added items
    private void showCheckoutSummary() {
        itemGrid.getChildren().clear();
        
        VBox summaryBox = new VBox(10);
        summaryBox.setPadding(new Insets(10));
        
        Label title = new Label("ORDER SUMMARY");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        summaryBox.getChildren().add(title);

        for (Product p : currentOrder) {
            Label itemLabel = new Label("• " + p.productName + " - ₱" + p.price);
            summaryBox.getChildren().add(itemLabel);
        }

        Button confirmFinalBtn = new Button("CONFIRM & SAVE TO DB");
        confirmFinalBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 10;");
        confirmFinalBtn.setPrefWidth(200);
        
        confirmFinalBtn.setOnAction(e -> saveOrderToDatabase());
        
        summaryBox.getChildren().add(new Label("----------------------"));
        summaryBox.getChildren().add(confirmFinalBtn);
        
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
                        int qty = 0;
                        for (Product p2 : currentOrder) {
                            if (p2.productId == p.productId) qty++;
                        }
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

            // Success: Reset and go back to menu
            orderArea.clear();
            currentOrder.clear();
            total = 0;
            refreshUI();
            loadCategory(0, 9);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Order #" + orderId + " has been saved!");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            btn.setStyle("-fx-text-alignment: center;");
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

