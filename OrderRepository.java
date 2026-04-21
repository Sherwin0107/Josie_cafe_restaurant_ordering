package aiven;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    // ============== GET ALL PRODUCTS FROM DATABASE ==============
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT productId, productName, price, quantity FROM products");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("productId"),
                    rs.getString("productName"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    // ============== SAVE ORDER TO DATABASE ==============
    public int saveOrder(String customerName, double total, List<Product> items) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO orders (customerName, total, orderDate) VALUES (?,?,NOW())",
                 Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, customerName);
            statement.setDouble(2, total);
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            int orderId = -1;
            if (resultSet.next()) {
                orderId = resultSet.getInt(1);
            }

            String itemSql = "INSERT INTO ordered_items (orderId, productId, quantity, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement itemPs = connection.prepareStatement(itemSql)) {
                for (Product p : items) {
                    itemPs.setInt(1, orderId);
                    itemPs.setInt(2, p.productId);
                    itemPs.setInt(3, 1);
                    itemPs.setDouble(4, p.price);
                    itemPs.addBatch();
                }
                itemPs.executeBatch();
            }

            return orderId;
        }
    }
}