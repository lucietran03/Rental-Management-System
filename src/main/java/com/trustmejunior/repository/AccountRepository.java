package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;
import com.trustmejunior.model.Enum.AccountRole;
import com.trustmejunior.model.User.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountRepository {
    private Connection connection;

    public AccountRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Fetches an account by username and password
    public Account getAccount(String user, String pass) {
        Account account = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Prepare the SQL query to fetch account with the given username and password
            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, user); // Set username parameter
            statement.setString(2, pass); // Set password parameter
            resultSet = statement.executeQuery();

            // If an account is found, map the result to the Account object
            if (resultSet.next()) {
                int accountId = resultSet.getInt("account_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());
                AccountRole role = AccountRole.valueOf(resultSet.getString("role"));

                account = new Account(accountId, username, password, fullName, email, dob, role);
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions
            System.err.println("Error fetching account: " + e.getMessage());
        } finally {
            // Close the result set and statement to free resources
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return account;
    }

    // Fetches an account by its unique ID
    public Account getAccountById(int id) {
        Account account = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Prepare the SQL query to fetch account by ID
            String sql = "SELECT * FROM account WHERE account_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set account ID parameter
            resultSet = statement.executeQuery();

            // If an account is found, map the result to the Account object
            if (resultSet.next()) {
                int accountId = resultSet.getInt("account_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());
                AccountRole role = AccountRole.valueOf(resultSet.getString("role"));

                account = new Account(accountId, username, password, fullName, email, dob, role);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching account: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return account;
    }

    // Fetches all accounts from the database
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Prepare the SQL query to fetch all accounts
            String sql = "SELECT * FROM account";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            // Map each result row to an Account object and add to the list
            while (resultSet.next()) {
                int accountId = resultSet.getInt("account_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());
                AccountRole role = AccountRole.valueOf(resultSet.getString("role"));

                Account account = new Account(accountId, username, password, fullName, email, dob, role);
                accounts.add(account);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching accounts: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return accounts;
    }

    // Creates a new account in the database
    public Account createAccount(String username, String password, String fullName, String email, Date dob,
            AccountRole role) {
        Account account = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection.setAutoCommit(false); // Begin transaction

            // Insert a new account into the database
            String sql = "INSERT INTO account (username, password, full_name, email, dob, role) VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, fullName);
            statement.setString(4, email);
            statement.setDate(5, new java.sql.Date(dob.getTime()));
            statement.setString(6, role.name());

            int rows = statement.executeUpdate(); // Execute the insert
            if (rows > 0) {
                resultSet = statement.getGeneratedKeys(); // Retrieve the generated ID
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    account = getAccountById(id); // Fetch the newly created account
                }
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
            try {
                connection.rollback(); // Rollback transaction on failure
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
                connection.setAutoCommit(true); // Reset auto-commit to default
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return account;
    }

    // Updates an existing account in the database
    public Account updateAccount(int accountId, String username, String password, String fullName, String email,
            Date dob, AccountRole role) {
        Account account = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection.setAutoCommit(false); // Begin transaction

            // Update account details in the database
            String sql = "UPDATE account SET username = ?, password = ?, full_name = ?, email = ?, dob = ?, role = ? WHERE account_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, fullName);
            statement.setString(4, email);
            statement.setDate(5, new java.sql.Date(dob.getTime()));
            statement.setString(6, role.name());
            statement.setInt(7, accountId);

            int rows = statement.executeUpdate(); // Execute the update
            if (rows > 0) {
                account = getAccountById(accountId); // Fetch the updated account
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            System.err.println("Error updating account: " + e.getMessage());
            try {
                connection.rollback(); // Rollback transaction on failure
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
                connection.setAutoCommit(true); // Reset auto-commit to default
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return account;
    }

    // Deletes an account from the database by ID
    public void deleteAccountById(int id) {
        PreparedStatement statement = null;

        try {
            connection.setAutoCommit(false); // Begin transaction

            // Delete account from the database
            String sql = "DELETE FROM account WHERE account_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            int rows = statement.executeUpdate(); // Execute the delete
            if (rows > 0) {
                System.out.println("Account deleted successfully");
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
            try {
                connection.rollback(); // Rollback transaction on failure
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                if (statement != null)
                    statement.close();
                connection.setAutoCommit(true); // Reset auto-commit to default
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
