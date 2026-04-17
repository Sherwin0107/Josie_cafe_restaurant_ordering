package bobosinoe;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OrderManaging extends Application {

    // Store current selected grid
    private FlowPane itemGrid = new FlowPane();

    // Define items per category
    private String[][] categoryItems = {
        {"Burger", "Fries", "Coke", "Shake", "Hotdog", "Pizza", "Pasta", "Rice", "Soup", "Tea"},
        {"Xed Item 1", "Xed Item 2", "Xed Item 3", "Xed Item 4", "Xed Item 5", "Xed Item 6"},
        {"Cat3 A", "Cat3 B", "Cat3 C", "Cat3 D"}
    };

    @Override
    public void start(Stage primaryStage) {

        // Left panel buttons
        Button btnSherwinKarrie = new Button("SHERWIN\nKARRIE");
        Button btnXedpogi = new Button("Pogi Xed");
        Button btnCategory3 = new Button("New button");

        btnSherwinKarrie.setPrefSize(100, 53);
        btnXedpogi.setPrefSize(100, 53);
        btnCategory3.setPrefSize(100, 53);

        // Set actions to switch grid content
        btnSherwinKarrie.setOnAction(e -> loadCategory(0));
        btnXedpogi.setOnAction(e -> loadCategory(1));
        btnCategory3.setOnAction(e -> loadCategory(2));

        VBox leftPanel = new VBox(17);
        leftPanel.setPadding(new Insets(34, 5, 5, 10));
        leftPanel.getChildren().addAll(btnSherwinKarrie, btnXedpogi, btnCategory3);

        // Setup item grid
        itemGrid.setHgap(10);
        itemGrid.setVgap(11);
        itemGrid.setPadding(new Insets(10));
        itemGrid.setPrefWrapLength(220);

        // Load first category by default
        loadCategory(0);

        ScrollPane scrollPane = new ScrollPane(itemGrid);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefSize(248, 218);
        scrollPane.setFitToWidth(true);

        // Main layout
        HBox root = new HBox(10);
        root.setPadding(new Insets(5));
        root.getChildren().addAll(leftPanel, scrollPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Order Managing");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Clears the grid and loads buttons for the selected category
    private void loadCategory(int categoryIndex) {
        itemGrid.getChildren().clear();

        for (String label : categoryItems[categoryIndex]) {
            Button btn = new Button(label);
            btn.setPrefSize(88, 65);
            itemGrid.getChildren().add(btn);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
