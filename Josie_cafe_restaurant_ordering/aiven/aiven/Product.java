package aiven;

public class Product {
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