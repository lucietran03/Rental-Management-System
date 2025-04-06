package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;
import com.trustmejunior.model.User.Tenant;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TenantRepository {
    private Connection connection;

    public TenantRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to retrieve a tenant based on the account ID
    public Tenant getTenantByAccountId(int accountId) {
        Tenant tenant = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Prepare the SQL query to join account and tenant tables to retrieve tenant
            // details by account ID
            String sql = "SELECT a.*, t.* FROM account a JOIN tenant t ON a.account_id = t.tenant_id WHERE a.account_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, accountId); // Set the account ID in the query
            resultSet = statement.executeQuery(); // Execute the query

            // If a result is found, map the result to a Tenant object
            if (resultSet.next()) {
                int tenantId = resultSet.getInt("tenant_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());

                tenant = new Tenant(tenantId, username, password, fullName, email, dob); // Create a Tenant object
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving tenant: " + e.getMessage());
        } finally {
            try {
                // Close the ResultSet and PreparedStatement to avoid resource leaks
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

        return tenant; // Return the Tenant object
    }

    // Method to retrieve the main tenant associated with a rental agreement by
    // rental agreement ID
    public Tenant getMainTenantByRentalAgreementId(int id) {
        Tenant tenant = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Prepare the SQL query to join account, tenant, and tenant_rental_agreement
            // tables
            // to retrieve the main tenant associated with a rental agreement
            String sql = "SELECT a.*, t.* FROM account a JOIN tenant t ON a.account_id = t.tenant_id JOIN tenant_rental_agreement tra ON t.tenant_id = tra.tenant_id WHERE tra.ra_id = ? AND tra.role='MAIN'";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the rental agreement ID in the query
            resultSet = statement.executeQuery(); // Execute the query

            // If a result is found, map the result to a Tenant object
            if (resultSet.next()) {
                int tenantId = resultSet.getInt("tenant_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                Date dob = new Date(resultSet.getDate("dob").getTime());

                tenant = new Tenant(tenantId, username, password, fullName, email, dob); // Create a Tenant object
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving tenant: " + e.getMessage());
        } finally {
            try {
                // Close the ResultSet and PreparedStatement to avoid resource leaks
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

        return tenant; // Return the Tenant object
    }

    // Method to retrieve all tenants
    public List<Tenant> getAllTenants() {
        List<Tenant> tenants = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to join account and tenant tables
            String query = "SELECT a.*, t.* FROM account a JOIN tenant t ON a.account_id = t.tenant_id";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            // Create Tenant objects from query results
            while (resultSet.next()) {
                Tenant tenant = new Tenant(
                        resultSet.getInt("tenant_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("full_name"),
                        resultSet.getString("email"),
                        new Date(resultSet.getDate("dob").getTime()));
                tenants.add(tenant);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving tenants: " + e.getMessage());
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

        return tenants;
    }

    // Method to retrieve sub-tenants by rental agreement ID
    public List<Tenant> getSubTenantsByRentalAgreementId(int id) {
        List<Tenant> tenants = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch sub-tenants for a specific rental agreement
            String query = "SELECT a.*, t.* FROM account a JOIN tenant t ON a.account_id = t.tenant_id " +
                    "JOIN tenant_rental_agreement tra ON t.tenant_id = tra.tenant_id WHERE tra.ra_id = ? AND tra.role='SUB'";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            // Create Tenant objects for sub-tenants
            while (resultSet.next()) {
                Tenant tenant = new Tenant(
                        resultSet.getInt("tenant_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("full_name"),
                        resultSet.getString("email"),
                        new Date(resultSet.getDate("dob").getTime()));
                tenants.add(tenant);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving sub-tenants: " + e.getMessage());
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

        return tenants;
    }

    // Creates a new tenant by inserting the tenant_id into the tenant table
    public Tenant createTenant(int accountId) {
        Tenant tenant = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection.setAutoCommit(false);

            // Insert tenant_id into tenant table and retrieve generated keys
            String sql = "INSERT INTO tenant (tenant_id) VALUES (?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, accountId);

            int rows = statement.executeUpdate();
            if (rows > 0) {
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    tenant = getTenantByAccountId(accountId); // Retrieve the tenant object
                }
            }

            connection.commit();
        } catch (SQLException e) {
            System.err.println("Error creating tenant: " + e.getMessage());

            try {
                connection.rollback(); // Rollback transaction if an error occurs
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close resources in finally block to ensure they are always closed
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }

                connection.setAutoCommit(true); // Restore default auto-commit behavior
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return tenant; // Return the created tenant
    }

    // Deletes a tenant based on the provided accountId
    public void deleteTenantByAccountId(int accountId) {
        PreparedStatement statement = null;

        try {
            connection.setAutoCommit(false);

            // Delete tenant with the given tenant_id
            String sql = "DELETE FROM tenant WHERE tenant_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, accountId);

            int rows = statement.executeUpdate();
            if (rows > 0) {
                System.out.println("Tenant deleted successfully");
            }

            connection.commit();
        } catch (SQLException e) {
            System.err.println("Error deleting tenant: " + e.getMessage());

            try {
                connection.rollback(); // Rollback transaction if an error occurs
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close resources in finally block to ensure they are always closed
                if (statement != null) {
                    statement.close();
                }

                connection.setAutoCommit(true); // Restore default auto-commit behavior
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Returns the total number of tenants in the system
    public int getTotalTenants() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int countTenant = 0;

        try {
            // Query to count total tenants in the tenant table
            String sql = "SELECT COUNT(tenant_id) AS tenant_count FROM tenant";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                countTenant = resultSet.getInt("tenant_count"); // Get the count from the result set
            }
        } catch (SQLException e) {
            System.err.println("Error fetching tenants: " + e.getMessage());
        }
        return countTenant; // Return the total number of tenants
    }

    // Returns the total number of rented tenants in the system
    public int getTotalRentedTenants() {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int countTenant = 0;

        try {
            // Query to count total rented tenants in the tenant table
            String sql = "SELECT COUNT(tenant_id) AS tenant_count FROM tenant";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                countTenant = resultSet.getInt("tenant_count"); // Get the count from the result set
            }
        } catch (SQLException e) {
            System.err.println("Error fetching tenants: " + e.getMessage());
        }
        return countTenant; // Return the total number of rented tenants
    }
}
