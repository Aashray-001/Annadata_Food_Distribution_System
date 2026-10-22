package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static String getUrl() {
        return System.getProperty("db.url", 
            System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/food_donation_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC"));
    }

    public static String getUser() {
        return System.getProperty("db.user", System.getenv().getOrDefault("DB_USER", "root"));
    }

    public static String getPassword() {
        return System.getProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", "root"));
    }

    private static Connection connection;

    private DatabaseConnection() {
        // Private constructor for Singleton
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(getUrl(), getUser(), getPassword());
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Error connecting to MySQL database at " + getUrl() + ". Please verify MySQL is running and credentials are correct.", e);
        }
        return connection;
    }
}
