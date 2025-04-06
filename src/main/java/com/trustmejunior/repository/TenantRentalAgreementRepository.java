package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;

import java.sql.*;

public class TenantRentalAgreementRepository {
    private Connection connection;

    public TenantRentalAgreementRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to link a tenant to a rental agreement with a specified role
    public void linkTenantToRentalAgreement(int tenantId, int rentalAgreementId, String role) {
        PreparedStatement statement = null;

        try {
            // Start a transaction
            connection.setAutoCommit(false);

            // Prepare the SQL statement to insert the tenant and rental agreement into the
            // tenant_rental_agreement table
            statement = connection
                    .prepareStatement("INSERT INTO tenant_rental_agreement (tenant_id, ra_id, role) VALUES (?, ?, ?)");
            statement.setInt(1, tenantId); // Set tenantId in the query
            statement.setInt(2, rentalAgreementId); // Set rentalAgreementId in the query
            statement.setString(3, role); // Set role in the query
            statement.executeUpdate(); // Execute the update

            // Commit the transaction
            connection.commit();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());

            try {
                // If an error occurs, rollback the transaction
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the statement and set auto-commit back to true
                if (statement != null) {
                    statement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to unlink a tenant from a rental agreement based on the specified role
    public void unlinkTenantFromRentalAgreement(int tenantId, int rentalAgreementId, String role) {
        PreparedStatement statement = null;

        try {
            // Start transaction
            connection.setAutoCommit(false);

            // Prepare and execute delete query
            statement = connection.prepareStatement(
                    "DELETE FROM tenant_rental_agreement WHERE tenant_id = ? AND ra_id = ? AND role = ?");
            statement.setInt(1, tenantId);
            statement.setInt(2, rentalAgreementId);
            statement.setString(3, role);
            statement.executeUpdate();

            // Commit transaction
            connection.commit();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            try {
                // Rollback on error
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close resources and reset auto-commit
                if (statement != null)
                    statement.close();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
