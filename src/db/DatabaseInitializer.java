package db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        String baseHost = System.getProperty("db.host", System.getenv().getOrDefault("DB_HOST", "localhost:3306"));
        String urlNoDb = "jdbc:mysql://" + baseHost + "/?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
        String user = DatabaseConnection.getUser();
        String password = DatabaseConnection.getPassword();

        try (Connection conn = DriverManager.getConnection(urlNoDb, user, password);
             Statement stmt = conn.createStatement()) {

            // The schema.sql contains the database creation and usage statements
            String filePath = "sql/schema.sql";
            StringBuilder sql = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Ignore comments and empty lines
                    if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                        continue;
                    }
                    sql.append(line);
                    if (line.trim().endsWith(";")) {
                        stmt.execute(sql.toString());
                        sql.setLength(0);
                    }
                }
            }
            System.out.println("Database initialized successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        initialize();
    }
}
