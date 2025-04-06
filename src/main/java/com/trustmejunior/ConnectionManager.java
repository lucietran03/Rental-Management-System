package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
    private static Connection connection;

    private ConnectionManager() {
        // Private constructor to prevent instantiation
    }

    // Initializes the database connection if not already established.
    public static void initConnection() {
        if (connection == null) {
            try {
                // Load database properties from the configuration file.
                Properties props = new Properties();
                FileInputStream in = new FileInputStream("src/main/resources/com/trustmejunior/config/db.properties");
                props.load(in);
                in.close();

                // Retrieve database connection details.
                String url = props.getProperty("db.url");
                String username = props.getProperty("db.username");
                String password = props.getProperty("db.password");

                // Establish the connection to the database.
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Connected to the database!");
            } catch (SQLException | IOException e) {
                // Handle connection failure and exit the application.
                System.err.println("Failed to connect to the database: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    // Returns the current database connection.
    public static Connection getConnection() {
        return connection;
    }

    // Closes the database connection if it's open.
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Handle closing failure.
                System.err.println("Failed to close the connection: " + e.getMessage());
            }
        }
    }
}
