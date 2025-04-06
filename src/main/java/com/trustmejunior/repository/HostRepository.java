package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;
import com.trustmejunior.model.User.Host;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HostRepository {
    private Connection connection;

    public HostRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to get a Host by their accountId
    public Host getHostByAccountId(int accountId) {
        Host host = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to join account and host tables based on account_id and host_id
            String sql = "SELECT a.*, h.* FROM account a JOIN host h ON a.account_id = h.host_id WHERE a.account_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, accountId); // Set accountId parameter in the query
            resultSet = statement.executeQuery(); // Execute the query

            // If a result is found, create a Host object with the retrieved data
            if (resultSet.next()) {
                int hostId = resultSet.getInt("host_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());

                // Initialize the Host object with the retrieved values
                host = new Host(hostId, username, password, fullName, email, dob);
            }
        } catch (SQLException e) {
            // Print error message if an exception occurs
            System.err.println("Error retrieving host: " + e.getMessage());
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

        // Return the Host object or null if not found
        return host;
    }

    // Method to get all Hosts
    public List<Host> getAllHosts() {
        List<Host> hosts = new ArrayList<Host>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to join account and host tables to get all hosts
            String sql = "SELECT a.*, h.* FROM account a JOIN host h ON a.account_id = h.host_id";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery(); // Execute the query

            // Iterate through the result set and create Host objects for each entry
            while (resultSet.next()) {
                int hostId = resultSet.getInt("host_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());

                // Add each Host object to the list
                Host host = new Host(hostId, username, password, fullName, email, dob);
                hosts.add(host);
            }
        } catch (SQLException e) {
            // Print error message if an exception occurs
            System.err.println("Error retrieving Hosts: " + e.getMessage());
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

        // Return the list of hosts
        return hosts;
    }

    // Method to get Hosts by rentalAgreementId
    public List<Host> getHostsByRentalAgreementId(int id) {
        List<Host> hosts = new ArrayList<Host>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to join account, host, and host_rental_agreement tables to get
            // hosts by rental agreement ID
            String sql = "SELECT a.*, h.* FROM account a JOIN host h ON a.account_id = h.host_id JOIN host_rental_agreement hra ON h.host_id = hra.host_id WHERE hra.ra_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set rental agreement ID parameter in the query
            resultSet = statement.executeQuery(); // Execute the query

            // Iterate through the result set and create Host objects for each entry
            while (resultSet.next()) {
                int hostId = resultSet.getInt("host_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());

                // Add each Host object to the list
                Host host = new Host(hostId, username, password, fullName, email, dob);
                hosts.add(host);
            }
        } catch (SQLException e) {
            // Print error message if an exception occurs
            System.err.println("Error retrieving Hosts: " + e.getMessage());
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

        // Return the list of hosts
        return hosts;
    }

    // Method to retrieve a list of hosts associated with a given property ID
    public List<Host> getHostsByPropertyId(int id) {
        List<Host> hosts = new ArrayList<Host>(); // Initialize an empty list to store the hosts
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to join account, host, and host_property tables
            String sql = "SELECT a.*, h.* FROM account a JOIN host h ON a.account_id = h.host_id JOIN host_property hp ON h.host_id = hp.host_id WHERE hp.property_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the property ID in the query
            resultSet = statement.executeQuery(); // Execute the query and get the result set

            // Loop through the result set and create Host objects
            while (resultSet.next()) {
                int hostId = resultSet.getInt("host_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());

                Host host = new Host(hostId, username, password, fullName, email, dob); // Create a Host object
                hosts.add(host); // Add the Host object to the list
            }
        } catch (SQLException e) {
            // Print an error message if there is an SQL exception
            System.err.println("Error retrieving Hosts: " + e.getMessage());
        } finally {
            try {
                // Close the result set and statement in the finally block to release resources
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                // Print the stack trace if there is an error closing resources
                e.printStackTrace();
            }
        }

        return hosts; // Return the list of hosts
    }

    // Method to create a new host by inserting it into the host table
    public Host createHost(int accountId) {
        Host host = null;
        PreparedStatement statement = null;

        try {
            connection.setAutoCommit(false); // Disable auto-commit to handle the transaction manually

            // SQL query to insert a new host into the host table
            String sql = "INSERT INTO host (host_id) VALUES (?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, accountId); // Set the account ID in the query

            // Execute the query and check how many rows were affected
            int rows = statement.executeUpdate();
            if (rows > 0) {
                host = getHostByAccountId(accountId); // Retrieve the host after insertion
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            // Print an error message if there is an SQL exception
            System.err.println("Error creating host: " + e.getMessage());

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

        return host; // Return the newly created host
    }

    // Method to delete a host by its account ID
    public void deleteHostByAccountId(int accountId) {
        PreparedStatement statement = null;

        try {
            connection.setAutoCommit(false); // Disable auto-commit to handle the transaction manually

            // SQL query to delete the host by account ID
            String sql = "DELETE FROM host WHERE host_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, accountId); // Set the account ID in the query

            // Execute the query and check how many rows were affected
            int rows = statement.executeUpdate();
            if (rows > 0) {
                System.out.println("Host deleted successfully"); // Print a success message if deletion is successful
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            // Print an error message if there is an SQL exception
            System.err.println("Error deleting host: " + e.getMessage());

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
