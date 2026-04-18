package aiven;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database_Connection {
    private static final String URL = "jdbc:mysql://mysql-1389c129-sherwincari96-98f0.j.aivencloud.com:24647/Orders_Schema"
            +"?ssl=TRUE"
            + "&sslMode=REQUIRED";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_kVqtt4Tupss6wsBunzK";
    

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
