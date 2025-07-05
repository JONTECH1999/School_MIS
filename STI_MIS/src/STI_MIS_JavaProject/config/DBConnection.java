package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/sti_mis", "root", ""
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Make getConnection() return the valid connection instead of throwing exception
    public static Connection getConnection() {
        return connect();
    }
}
