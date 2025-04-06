package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;

import java.sql.*;

public class HostRentalAgreementRepository {
    private Connection connection;

    public HostRentalAgreementRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to link a host to a rental agreement by inserting data into the
    // host_rental_agreement table
    public void linkHostToRentalAgreement(int hostId, int rentalAgreementId) {
        PreparedStatement statement = null;

        try {
            // Disable auto-commit for transaction management
            connection.setAutoCommit(false);

            // Prepare the SQL statement to insert a new record into the
            // host_rental_agreement table
            statement = connection.prepareStatement("INSERT INTO host_rental_agreement (host_id, ra_id) VALUES (?, ?)");

            // Set the hostId and rentalAgreementId parameters in the SQL statement
            statement.setInt(1, hostId);
            statement.setInt(2, rentalAgreementId);

            // Execute the update (inserting the link between the host and rental agreement)
            statement.executeUpdate();

            // Commit the transaction to make the change permanent
            connection.commit();
        } catch (SQLException e) {
            // Handle SQL exception by printing the error message
            System.err.println("Error: " + e.getMessage());

            try {
                // If an error occurs, roll back the transaction to maintain data consistency
                connection.rollback();
            } catch (SQLException ex) {
                // Print error if rollback fails
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the statement to free up resources
                if (statement != null) {
                    statement.close();
                }

                // Re-enable auto-commit after transaction completion
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Handle exception while closing resources
                e.printStackTrace();
            }
        }
    }

    // Method to unlink a host from a rental agreement by removing the association
    // from the database
    public void unlinkHostFromRentalAgreement(int hostId, int rentalAgreementId) {
        PreparedStatement statement = null;

        try {
            // Disable auto-commit to handle the transaction manually
            connection.setAutoCommit(false);

            // SQL query to delete the association between the host and rental agreement
            statement = connection
                    .prepareStatement("DELETE FROM host_rental_agreement WHERE host_id = ? AND ra_id = ?");
            statement.setInt(1, hostId); // Set the host's ID
            statement.setInt(2, rentalAgreementId); // Set the rental agreement's ID
            statement.executeUpdate(); // Execute the query to remove the association

            // Commit the transaction if successful
            connection.commit();
        } catch (SQLException e) {
            // Print error message if an exception occurs
            System.err.println("Error: " + e.getMessage());

            try {
                // Rollback the transaction in case of an error
                connection.rollback();
            } catch (SQLException ex) {
                // Print an error message if rolling back the transaction fails
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
