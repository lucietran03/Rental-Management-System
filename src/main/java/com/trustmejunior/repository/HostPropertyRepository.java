package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;

import java.sql.*;

public class HostPropertyRepository {
    private Connection connection;

    public HostPropertyRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to associate a host with a property in the database
    public void linkHostToProperty(int hostId, int propertyId) {
        PreparedStatement statement = null;

        try {
            // Disable auto-commit to allow manual transaction handling
            connection.setAutoCommit(false);

            // Prepare SQL query to insert a new record into the host_property table
            statement = connection.prepareStatement("INSERT INTO host_property (host_id, property_id) VALUES (?, ?)");
            statement.setInt(1, hostId); // Set the host_id parameter
            statement.setInt(2, propertyId); // Set the property_id parameter
            statement.executeUpdate(); // Execute the query

            // Commit the transaction if successful
            connection.commit();
        } catch (SQLException e) {
            // Print the error message if an exception occurs
            System.err.println("Error: " + e.getMessage());

            try {
                // Rollback the transaction in case of an error
                connection.rollback();
            } catch (SQLException ex) {
                // Print an error message if rollback fails
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the PreparedStatement if it is not null
                if (statement != null) {
                    statement.close();
                }

                // Re-enable auto-commit mode for future operations
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Print stack trace if closing the statement or re-enabling auto-commit fails
                e.printStackTrace();
            }
        }
    }

    // Method to remove the association between a host and a property in the
    // database
    public void unlinkHostFromProperty(int hostId, int propertyId) {
        PreparedStatement statement = null;

        try {
            // Disable auto-commit to allow manual transaction handling
            connection.setAutoCommit(false);

            // Prepare SQL query to delete the specified record from the host_property table
            statement = connection.prepareStatement("DELETE FROM host_property WHERE host_id = ? AND property_id = ?");
            statement.setInt(1, hostId); // Set the host_id parameter
            statement.setInt(2, propertyId); // Set the property_id parameter
            statement.executeUpdate(); // Execute the query

            // Commit the transaction if successful
            connection.commit();
        } catch (SQLException e) {
            // Print the error message if an exception occurs
            System.err.println("Error: " + e.getMessage());

            try {
                // Rollback the transaction in case of an error
                connection.rollback();
            } catch (SQLException ex) {
                // Print an error message if rollback fails
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the PreparedStatement if it is not null
                if (statement != null) {
                    statement.close();
                }

                // Re-enable auto-commit mode for future operations
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Print stack trace if closing the statement or re-enabling auto-commit fails
                e.printStackTrace();
            }
        }
    }
}
