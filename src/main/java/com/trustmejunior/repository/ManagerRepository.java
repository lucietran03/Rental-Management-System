package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;
import com.trustmejunior.model.User.Manager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManagerRepository {
    private Connection connection;

    public ManagerRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to get a Manager by their accountId
    public Manager getManagerByAccountId(int accountId) {
        Manager manager = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to join account and manager tables based on account_id and
            // manager_id
            String sql = "SELECT a.*, m.* FROM account a JOIN manager m ON a.account_id = m.manager_id WHERE a.account_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, accountId); // Set accountId parameter in the query
            resultSet = statement.executeQuery(); // Execute the query

            // If a result is found, create a Manager object with the retrieved data
            if (resultSet.next()) {
                int managerId = resultSet.getInt("manager_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());

                // Initialize the Manager object with the retrieved values
                manager = new Manager(managerId, username, password, fullName, email, dob);
            }
        } catch (SQLException e) {
            // Print error message if an exception occurs
            System.err.println("Error retrieving manager: " + e.getMessage());
        } finally {
            try {
                // Close the resultSet and statement to release resources
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Return the Manager object or null if not found
        return manager;
    }

    // Method to get all Managers
    public List<Manager> getAllManagers() {
        List<Manager> managers = new ArrayList<Manager>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to join account and manager tables to get all managers
            String sql = "SELECT a.*, m.* FROM account a JOIN manager m ON a.account_id = m.manager_id";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery(); // Execute the query

            // Iterate through the result set and create Manager objects for each entry
            while (resultSet.next()) {
                int managerId = resultSet.getInt("manager_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = resultSet.getDate("dob");

                // Add each Manager object to the list
                Manager manager = new Manager(managerId, username, password, fullName, email, dob);
                managers.add(manager);
            }
        } catch (SQLException e) {
            // Print error message if an exception occurs
            System.err.println("Error retrieving managers: " + e.getMessage());
        } finally {
            try {
                // Close the resultSet and statement to release resources
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Return the list of managers
        return managers;
    }

    // Method to create a new manager in the database using the account ID
    public Manager createManager(int accountId) {
        Manager manager = null;
        PreparedStatement statement = null;

        try {
            connection.setAutoCommit(false); // Disable auto-commit to manage the transaction manually

            // SQL query to insert a new manager into the manager table using the account ID
            String sql = "INSERT INTO manager (manager_id) VALUES (?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, accountId); // Set the account ID in the query

            // Execute the insert query and check how many rows were affected
            int rows = statement.executeUpdate();
            if (rows > 0) {
                manager = getManagerByAccountId(accountId); // Retrieve the newly created manager if the insertion is
                                                            // successful
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            // Print an error message if there is an SQL exception
            System.err.println("Error creating manager: " + e.getMessage());

            try {
                connection.rollback(); // Rollback the transaction in case of an error
            } catch (SQLException ex) {
                // Print an error message if rolling back the transaction fails
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the statement and re-enable auto-commit for future operations
                if (statement != null) {
                    statement.close();
                }

                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Print the stack trace if there is an error closing the statement or
                // re-enabling auto-commit
                e.printStackTrace();
            }
        }

        return manager; // Return the newly created manager object
    }

    // Method to delete a manager from the database using the account ID
    public void deleteManagerByAccountId(int accountId) {
        PreparedStatement statement = null;

        try {
            connection.setAutoCommit(false); // Disable auto-commit to manage the transaction manually

            // SQL query to delete the manager using the account ID
            String sql = "DELETE FROM manager WHERE manager_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, accountId); // Set the account ID in the query

            // Execute the delete query and check how many rows were affected
            int rows = statement.executeUpdate();
            if (rows > 0) {
                System.out.println("Manager deleted successfully"); // Print a success message if deletion is successful
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            // Print an error message if there is an SQL exception
            System.err.println("Error deleting manager: " + e.getMessage());

            try {
                connection.rollback(); // Rollback the transaction in case of an error
            } catch (SQLException ex) {
                // Print an error message if rolling back the transaction fails
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the statement and re-enable auto-commit for future operations
                if (statement != null) {
                    statement.close();
                }

                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Print the stack trace if there is an error closing the statement or
                // re-enabling auto-commit
                e.printStackTrace();
            }
        }
    }

}