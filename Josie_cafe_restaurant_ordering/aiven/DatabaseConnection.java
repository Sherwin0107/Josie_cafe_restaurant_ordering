

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://mysql-1389c129-sherwincari96-98f0.j.aivencloud.com:24647/Orders_Schema"
            +"?ssl=TRUE"
            + "&sslMode=REQUIRED";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "ask the chosen one for the pass";
    

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public static void main(String[] args) {
    	Connection conn = DriverManager.getConnection(URL)
    }
}
