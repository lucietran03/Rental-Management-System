package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;
import com.trustmejunior.model.User.Owner;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OwnerRepository {
    private Connection connection;

    public OwnerRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to fetch the owner details based on the account ID
    public Owner getOwnerByAccountId(int accountId) {
        Owner owner = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to join the account and owner tables and select relevant details
            String sql = "SELECT a.*, o.* FROM account a JOIN owner o ON a.account_id = o.owner_id WHERE a.account_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, accountId); // Set the account ID parameter in the query
            resultSet = statement.executeQuery(); // Execute the query

            // If a matching record is found, map the result to an Owner object
            if (resultSet.next()) {
                int ownerId = resultSet.getInt("owner_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());

                // Create a new Owner object with the retrieved data
                owner = new Owner(ownerId, username, password, fullName, email, dob);
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions by printing the error message
            System.err.println("Error retrieving owner: " + e.getMessage());
        } finally {
            try {
                // Close the result set and statement to free up resources
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                // Print any exceptions that occur while closing resources
                e.printStackTrace();
            }
        }

        return owner; // Return the owner object (or null if no owner is found)
    }

    // Method to fetch all owners from the database
    public List<Owner> getAllOwners() {
        List<Owner> owners = new ArrayList<Owner>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to join the account and owner tables and select all relevant
            // details
            String sql = "SELECT a.*, o.* FROM account a JOIN owner o ON a.account_id = o.owner_id";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery(); // Execute the query

            // Loop through all the result set rows
            while (resultSet.next()) {
                // Extract owner details from the result set
                int ownerId = resultSet.getInt("owner_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());

                // Create a new Owner object with the retrieved data
                Owner owner = new Owner(ownerId, username, password, fullName, email, dob);
                // Add the owner to the list of owners
                owners.add(owner);
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions by printing the error message
            System.err.println("Error retrieving owners: " + e.getMessage());
        } finally {
            try {
                // Close the result set and statement to free up resources
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                // Print any exceptions that occur while closing resources
                e.printStackTrace();
            }
        }

        return owners; // Return the list of owners
    }

    // Method to create a new owner for a given account ID
    public Owner createOwner(int accountId) {
        Owner owner = null;
        PreparedStatement statement = null;

        try {
            // Start a transaction by disabling auto-commit
            connection.setAutoCommit(false);

            // SQL query to insert a new owner with the provided account ID
            String sql = "INSERT INTO owner (owner_id) VALUES (?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, accountId);

            // Execute the query and check if any row was affected
            int rows = statement.executeUpdate();
            if (rows > 0) {
                // If the insertion is successful, fetch the newly created owner
                owner = getOwnerByAccountId(accountId);
            }

            // Commit the transaction if everything goes fine
            connection.commit();
        } catch (SQLException e) {
            // If any error occurs, print the error message
            System.err.println("Error creating owner: " + e.getMessage());

            // Rollback the transaction in case of error
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the prepared statement and reset auto-commit
                if (statement != null) {
                    statement.close();
                }

                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return owner;
    }

    // Method to delete an owner using the provided account ID
    public void deleteOwnerByAccountId(int accountId) {
        PreparedStatement statement = null;

        try {
            // Start a transaction by disabling auto-commit
            connection.setAutoCommit(false);

            // SQL query to delete the owner based on the provided account ID
            String sql = "DELETE FROM owner WHERE owner_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, accountId);

            // Execute the query and check if any row was affected
            int rows = statement.executeUpdate();
            if (rows > 0) {
                // If deletion is successful, print a success message
                System.out.println("Owner deleted successfully");
            }

            // Commit the transaction if everything goes fine
            connection.commit();
        } catch (SQLException e) {
            // If any error occurs, print the error message
            System.err.println("Error deleting owner: " + e.getMessage());

            // Rollback the transaction in case of error
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the prepared statement and reset auto-commit
                if (statement != null) {
                    statement.close();
                }

                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
