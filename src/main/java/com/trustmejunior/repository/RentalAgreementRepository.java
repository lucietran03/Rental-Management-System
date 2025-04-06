package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;

import com.trustmejunior.model.Enum.RentalPeriod;
import com.trustmejunior.model.Enum.RentalStatus;
import com.trustmejunior.model.RentalEntity.RentalAgreement;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class RentalAgreementRepository {
    private Connection connection;

    public RentalAgreementRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to get a rental agreement by its ID
    public RentalAgreement getRentalAgreementById(int id) {
        RentalAgreement agreement = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch rental agreement by ID
            String sql = "SELECT * FROM rental_agreement WHERE ra_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the rental agreement ID
            resultSet = statement.executeQuery();

            // If agreement is found, create and return RentalAgreement object
            if (resultSet.next()) {
                int rentalAgreementId = resultSet.getInt("ra_id");
                double fee = resultSet.getDouble("fee");
                Date startDate = new Date(resultSet.getDate("start_date").getTime());
                Date endDate = new Date(resultSet.getDate("end_date").getTime());
                RentalPeriod period = RentalPeriod.valueOf(resultSet.getString("period"));
                RentalStatus status = RentalStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                int propertyId = resultSet.getInt("property_id");

                agreement = new RentalAgreement(rentalAgreementId, fee, startDate, endDate, period, status, ownerId,
                        propertyId);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rental agreement: " + e.getMessage());
        } finally {
            // Clean up resources
            try {
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

        return agreement; // Return the found agreement or null
    }

    // Method to get all rental agreements
    public List<RentalAgreement> getAllRentalAgreements() {
        List<RentalAgreement> agreements = new ArrayList<RentalAgreement>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch all rental agreements
            String sql = "SELECT * FROM rental_agreement";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            // Iterate through the results and add to list
            while (resultSet.next()) {
                int rentalAgreementId = resultSet.getInt("ra_id");
                double fee = resultSet.getDouble("fee");

                Date startDate = null;
                if (resultSet.getDate("start_date") != null) {
                    startDate = new Date(resultSet.getDate("start_date").getTime());
                }

                Date endDate = null;
                if (resultSet.getDate("end_date") != null) {
                    endDate = new Date(resultSet.getDate("end_date").getTime());
                }

                RentalPeriod period = RentalPeriod.valueOf(resultSet.getString("period"));
                RentalStatus status = RentalStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                int propertyId = resultSet.getInt("property_id");

                RentalAgreement agreement = new RentalAgreement(rentalAgreementId, fee, startDate, endDate, period,
                        status, ownerId, propertyId);
                agreements.add(agreement); // Add agreement to the list
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rental agreements: " + e.getMessage());
        } finally {
            // Clean up resources
            try {
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

        return agreements; // Return the list of rental agreements
    }

    // Method to get rental agreements by host ID
    public List<RentalAgreement> getRentalAgreementsByHostId(int id) {
        List<RentalAgreement> agreements = new ArrayList<RentalAgreement>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch rental agreements by host ID
            String sql = "SELECT ra.* FROM rental_agreement ra JOIN host_rental_agreement hra ON ra.ra_id = hra.ra_id WHERE hra.host_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the host ID
            resultSet = statement.executeQuery();

            // Iterate through the results and add to list
            while (resultSet.next()) {
                int rentalAgreementId = resultSet.getInt("ra_id");
                double fee = resultSet.getDouble("fee");

                Date startDate = null;
                if (resultSet.getDate("start_date") != null) {
                    startDate = new Date(resultSet.getDate("start_date").getTime());
                }

                Date endDate = null;
                if (resultSet.getDate("end_date") != null) {
                    endDate = new Date(resultSet.getDate("end_date").getTime());
                }

                RentalPeriod period = RentalPeriod.valueOf(resultSet.getString("period"));
                RentalStatus status = RentalStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                int propertyId = resultSet.getInt("property_id");

                RentalAgreement agreement = new RentalAgreement(rentalAgreementId, fee, startDate, endDate, period,
                        status, ownerId, propertyId);
                agreements.add(agreement); // Add agreement to the list
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rental agreements: " + e.getMessage());
        } finally {
            // Clean up resources
            try {
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

        return agreements; // Return the list of rental agreements for the specified host
    }

    // Method to get a list of rental agreements by the owner's ID
    public List<RentalAgreement> getRentalAgreementsByOwnerId(int id) {
        List<RentalAgreement> agreements = new ArrayList<RentalAgreement>(); // Initialize list to hold rental
                                                                             // agreements
        PreparedStatement statement = null; // Prepare the statement for executing the query
        ResultSet resultSet = null; // ResultSet to hold the data returned from the database

        try {
            // SQL query to fetch all rental agreements associated with the given owner ID
            String sql = "SELECT * FROM rental_agreement WHERE owner_id = ?";
            statement = connection.prepareStatement(sql); // Prepare the SQL statement
            statement.setInt(1, id); // Set the owner ID parameter in the query
            resultSet = statement.executeQuery(); // Execute the query

            // Process the result set to extract each rental agreement
            while (resultSet.next()) {
                int rentalAgreementId = resultSet.getInt("ra_id"); // Get rental agreement ID
                double fee = resultSet.getDouble("fee"); // Get the rental fee

                // Check and get the start date from the result set
                Date startDate = null;
                if (resultSet.getDate("start_date") != null) {
                    startDate = new Date(resultSet.getDate("start_date").getTime());
                }

                // Check and get the end date from the result set
                Date endDate = null;
                if (resultSet.getDate("end_date") != null) {
                    endDate = new Date(resultSet.getDate("end_date").getTime());
                }

                // Get the rental period and status, converting them from String to Enum
                RentalPeriod period = RentalPeriod.valueOf(resultSet.getString("period"));
                RentalStatus status = RentalStatus.valueOf(resultSet.getString("status"));

                int ownerId = resultSet.getInt("owner_id"); // Get the owner ID
                int propertyId = resultSet.getInt("property_id"); // Get the property ID

                // Create a RentalAgreement object and add it to the list
                RentalAgreement agreement = new RentalAgreement(rentalAgreementId, fee, startDate, endDate, period,
                        status, ownerId, propertyId);
                agreements.add(agreement);
            }
        } catch (SQLException e) {
            // Catch any SQL exceptions and print the error message
            System.err.println("Error fetching rental agreements: " + e.getMessage());
        } finally {
            // Close the ResultSet and Statement objects to release resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Print error if closing resources fails
            }
        }

        return agreements; // Return the list of rental agreements
    }

    // Method to get a list of rental agreements by the property's ID
    public List<RentalAgreement> getRentalAgreementsByPropertyId(int id) {
        List<RentalAgreement> agreements = new ArrayList<RentalAgreement>(); // Initialize list to hold rental
                                                                             // agreements
        PreparedStatement statement = null; // Prepare the statement for executing the query
        ResultSet resultSet = null; // ResultSet to hold the data returned from the database

        try {
            // SQL query to fetch all rental agreements associated with the given property
            // ID
            String sql = "SELECT * FROM rental_agreement WHERE property_id = ?";
            statement = connection.prepareStatement(sql); // Prepare the SQL statement
            statement.setInt(1, id); // Set the property ID parameter in the query
            resultSet = statement.executeQuery(); // Execute the query

            // Process the result set to extract each rental agreement
            while (resultSet.next()) {
                int rentalAgreementId = resultSet.getInt("ra_id"); // Get rental agreement ID
                double fee = resultSet.getDouble("fee"); // Get the rental fee

                // Check and get the start date from the result set
                Date startDate = null;
                if (resultSet.getDate("start_date") != null) {
                    startDate = new Date(resultSet.getDate("start_date").getTime());
                }

                // Check and get the end date from the result set
                Date endDate = null;
                if (resultSet.getDate("end_date") != null) {
                    endDate = new Date(resultSet.getDate("end_date").getTime());
                }

                // Get the rental period and status, converting them from String to Enum
                RentalPeriod period = RentalPeriod.valueOf(resultSet.getString("period"));
                RentalStatus status = RentalStatus.valueOf(resultSet.getString("status"));

                int ownerId = resultSet.getInt("owner_id"); // Get the owner ID
                int propertyId = resultSet.getInt("property_id"); // Get the property ID

                // Create a RentalAgreement object and add it to the list
                RentalAgreement agreement = new RentalAgreement(rentalAgreementId, fee, startDate, endDate, period,
                        status, ownerId, propertyId);
                agreements.add(agreement);
            }
        } catch (SQLException e) {
            // Catch any SQL exceptions and print the error message
            System.err.println("Error fetching rental agreements: " + e.getMessage());
        } finally {
            // Close the ResultSet and Statement objects to release resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Print error if closing resources fails
            }
        }

        return agreements; // Return the list of rental agreements
    }

    // Retrieve rental agreements for the main tenant based on tenant ID
    public List<RentalAgreement> getRentalAgreementsByMainTenantId(int id) {
        List<RentalAgreement> agreements = new ArrayList<RentalAgreement>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch rental agreements for the main tenant
            String sql = "SELECT ra.* FROM rental_agreement ra JOIN tenant_rental_agreement tra ON ra.ra_id = tra.ra_id WHERE tra.tenant_id = ? AND tra.role='MAIN'";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set tenant ID in the query
            resultSet = statement.executeQuery();

            // Process the result set and create RentalAgreement objects
            while (resultSet.next()) {
                // Extract the data from the result set
                int rentalAgreementId = resultSet.getInt("ra_id");
                double fee = resultSet.getDouble("fee");

                Date startDate = null;
                if (resultSet.getDate("start_date") != null) {
                    startDate = new Date(resultSet.getDate("start_date").getTime());
                }

                Date endDate = null;
                if (resultSet.getDate("end_date") != null) {
                    endDate = new Date(resultSet.getDate("end_date").getTime());
                }

                // Get the rental period and status
                RentalPeriod period = RentalPeriod.valueOf(resultSet.getString("period"));
                RentalStatus status = RentalStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                int propertyId = resultSet.getInt("property_id");

                // Create a RentalAgreement object and add it to the list
                RentalAgreement agreement = new RentalAgreement(rentalAgreementId, fee, startDate, endDate, period,
                        status, ownerId, propertyId);
                agreements.add(agreement);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rental agreements: " + e.getMessage());
        } finally {
            // Close resources to prevent memory leaks
            try {
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

        return agreements;
    }

    // Retrieve rental agreements for a sub-tenant based on tenant ID
    public List<RentalAgreement> getRentalAgreementsBySubTenantId(int id) {
        List<RentalAgreement> agreements = new ArrayList<RentalAgreement>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch rental agreements for the sub-tenant
            String sql = "SELECT ra.* FROM rental_agreement ra JOIN tenant_rental_agreement tra ON ra.ra_id = tra.ra_id WHERE tra.tenant_id = ? AND tra.role='SUB'";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set tenant ID in the query
            resultSet = statement.executeQuery();

            // Process the result set and create RentalAgreement objects
            while (resultSet.next()) {
                // Extract the data from the result set
                int rentalAgreementId = resultSet.getInt("ra_id");
                double fee = resultSet.getDouble("fee");

                Date startDate = null;
                if (resultSet.getDate("start_date") != null) {
                    startDate = new Date(resultSet.getDate("start_date").getTime());
                }

                Date endDate = null;
                if (resultSet.getDate("end_date") != null) {
                    endDate = new Date(resultSet.getDate("end_date").getTime());
                }

                // Get the rental period and status
                RentalPeriod period = RentalPeriod.valueOf(resultSet.getString("period"));
                RentalStatus status = RentalStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                int propertyId = resultSet.getInt("property_id");

                // Create a RentalAgreement object and add it to the list
                RentalAgreement agreement = new RentalAgreement(rentalAgreementId, fee, startDate, endDate, period,
                        status, ownerId, propertyId);
                agreements.add(agreement);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rental agreements: " + e.getMessage());
        } finally {
            // Close resources to prevent memory leaks
            try {
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

        return agreements;
    }

    // Method to create a new rental agreement
    public RentalAgreement createRentalAgreement(double fee, Date startDate, Date endDate,
            RentalPeriod period, RentalStatus status, int ownerId, int propertyId) {
        RentalAgreement agreement = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Begin a transaction by setting auto-commit to false
            connection.setAutoCommit(false);

            // SQL query to insert a new rental agreement into the database
            String sql = "INSERT INTO rental_agreement (fee, start_date, end_date, period, status, owner_id, property_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set values for the prepared statement
            statement.setDouble(1, fee);
            statement.setDate(2, new java.sql.Date(startDate.getTime()));
            statement.setDate(3, new java.sql.Date(endDate.getTime()));
            statement.setString(4, period.name());
            statement.setString(5, status.name());
            statement.setInt(6, ownerId);
            statement.setInt(7, propertyId);

            // Execute the insert operation
            int rows = statement.executeUpdate();
            if (rows > 0) {
                // If the insertion was successful, retrieve the generated key (ID) for the new
                // rental agreement
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    // Fetch the full rental agreement using the generated ID
                    agreement = getRentalAgreementById(id);
                }
            }

            // Commit the transaction if everything was successful
            connection.commit();
        } catch (SQLException e) {
            // Handle any SQL errors by printing the error message
            System.err.println("Error creating rental agreement: " + e.getMessage());

            try {
                // Rollback the transaction in case of error
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Clean up resources (resultSet and statement) and set auto-commit back to true
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return agreement;
    }

    // Method to update an existing rental agreement
    public RentalAgreement updateRentalAgreement(int rentalAgreementId, double fee, Date startDate, Date endDate,
            RentalPeriod period, RentalStatus status, int ownerId, int propertyId) {
        RentalAgreement agreement = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Begin a transaction by setting auto-commit to false
            connection.setAutoCommit(false);

            // SQL query to update the existing rental agreement in the database
            String sql = "UPDATE rental_agreement SET fee = ?, start_date = ?, end_date = ?, period = ?, status = ?, owner_id = ?, property_id = ? WHERE ra_id = ?";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set values for the prepared statement
            statement.setDouble(1, fee);
            statement.setDate(2, new java.sql.Date(startDate.getTime()));
            statement.setDate(3, new java.sql.Date(endDate.getTime()));
            statement.setString(4, period.name());
            statement.setString(5, status.name());
            statement.setInt(6, ownerId);
            statement.setInt(7, propertyId);
            statement.setInt(8, rentalAgreementId);

            // Execute the update operation
            int rows = statement.executeUpdate();
            if (rows > 0) {
                // If the update was successful, retrieve the generated key (ID) for the updated
                // rental agreement
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    // Fetch the full rental agreement using the generated ID
                    agreement = getRentalAgreementById(id);
                }
            }

            // Commit the transaction if everything was successful
            connection.commit();
        } catch (SQLException e) {
            // Handle any SQL errors by printing the error message
            System.err.println("Error updating rental agreement: " + e.getMessage());

            try {
                // Rollback the transaction in case of error
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Clean up resources (resultSet and statement) and set auto-commit back to true
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return agreement;
    }

    // Delete a rental agreement by its ID
    public void deleteRentalAgreementById(int id) {
        PreparedStatement statement = null;

        try {
            // Disable auto-commit to handle the transaction manually
            connection.setAutoCommit(false);

            // SQL query to delete a rental agreement by its ID
            String sql = "DELETE FROM rental_agreement WHERE ra_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the rental agreement ID in the query

            // Execute the delete query and check the number of affected rows
            int rows = statement.executeUpdate();
            if (rows > 0) {
                System.out.println("Rental agreement deleted successfully");
            }

            // Commit the transaction to apply changes
            connection.commit();
        } catch (SQLException e) {
            System.err.println("Error deleting rental agreement: " + e.getMessage());

            try {
                // Rollback the transaction in case of an error
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                if (statement != null) {
                    statement.close(); // Close the PreparedStatement to release resources
                }

                // Restore the default auto-commit setting
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Get the total number of rental agreements for a specific host
    public int getTotalRentalAgreements(Integer hostId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int countRA = 0;

        // SQL query to count rental agreements for the specified host
        String sql = """
                    SELECT COUNT(ra_id) AS ra_count
                    FROM rental_agreement ra
                    WHERE ra_id IN (
                        SELECT ra_id
                        FROM host_rental_agreement
                        WHERE host_id = COALESCE(?, host_id)
                    )
                """;
        try {
            // Prepare the SQL statement
            statement = connection.prepareStatement(sql);

            // Set the hostId parameter; use null for no specific hostId
            if (hostId != null) {
                statement.setInt(1, hostId);
            } else {
                statement.setNull(1, java.sql.Types.INTEGER);
            }

            // Execute the query to get the result
            resultSet = statement.executeQuery();

            // Fetch the result from the query
            if (resultSet.next()) {
                countRA = resultSet.getInt("ra_count");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rental agreements: " + e.getMessage());
        } finally {
            // Close the ResultSet explicitly
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }

        // Return the count of rental agreements
        return countRA;
    }

    // Method to calculate the total revenue for a host
    public double getTotalRevenue(Integer hostId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        double sumRevenue = 0;

        try {
            // SQL query to calculate total revenue for a host
            String sql = """
                        SELECT
                            SUM(
                                (
                                    EXTRACT(YEAR FROM age(ra.end_date, ra.start_date)) * 12 +
                                    EXTRACT(MONTH FROM age(ra.end_date, ra.start_date))
                                ) * ra.fee
                            ) AS total_revenue
                        FROM rental_agreement ra
                        WHERE
                            ra.status IN ('ACTIVE', 'COMPLETED')
                            AND ra.ra_id IN (
                                SELECT ra_id
                                FROM host_rental_agreement
                                WHERE host_id = COALESCE(?, host_id)
                            )
                    """;

            // Prepare statement and set hostId parameter if available
            statement = connection.prepareStatement(sql);
            if (hostId != null) {
                statement.setInt(1, hostId);
            } else {
                statement.setNull(1, java.sql.Types.INTEGER);
            }

            // Execute query and get the total revenue
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                sumRevenue = resultSet.getDouble("total_revenue");
            }
        } catch (SQLException e) {
            // Print error if something goes wrong
            System.err.println("Error fetching rental agreements revenue: " + e.getMessage());
        }

        return sumRevenue; // Return the total revenue
    }

    public Map<String, Map<String, Double>> getYearlyRevenueTrend(Integer hostId) {
        // Map to store yearly revenue data, where key is year and value is revenue
        // types map
        Map<String, Map<String, Double>> yearlyRevenue = new LinkedHashMap<>(); // Preserve insertion order

        // SQL query to get total revenue for each year
        String sqlTotalRevenue = """
                SELECT
                TO_CHAR(d, 'YYYY') AS year,
                SUM(p.amount) AS total_revenue
                FROM (
                        SELECT generate_series(start_date, end_date, INTERVAL '1 month') AS d, ra.ra_id
                        FROM rental_agreement ra
                        JOIN host_rental_agreement hra ON ra.ra_id = hra.ra_id
                        WHERE ra.status IN ('ACTIVE', 'COMPLETED')
                        AND COALESCE(?, hra.host_id) = hra.host_id
                    ) AS date_series
                JOIN payment p ON p.ra_id = date_series.ra_id
                GROUP BY TO_CHAR(d, 'YYYY')
                ORDER BY TO_CHAR(d, 'YYYY') ASC;
                """;

        // SQL query to get paid revenue for each year
        String sqlPaidRevenue = """
                    SELECT
                        TO_CHAR(d, 'YYYY') AS year,
                        SUM(p.amount) AS total_revenue_paid
                    FROM (
                        SELECT generate_series(start_date, end_date, INTERVAL '1 month') AS d, ra.ra_id
                        FROM rental_agreement ra
                        JOIN host_rental_agreement hra ON ra.ra_id = hra.ra_id
                        WHERE ra.status IN ('ACTIVE', 'COMPLETED')
                          AND COALESCE(?, hra.host_id) = hra.host_id
                    ) AS date_series
                    JOIN payment p ON p.ra_id = date_series.ra_id
                    WHERE p.status = 'PAID'
                    GROUP BY TO_CHAR(d, 'YYYY')
                    ORDER BY TO_CHAR(d, 'YYYY') ASC;
                """;

        try (PreparedStatement preparedStatementTotal = connection.prepareStatement(sqlTotalRevenue);
                PreparedStatement preparedStatementPaid = connection.prepareStatement(sqlPaidRevenue)) {

            // Set hostId parameter for queries if provided
            if (hostId != null) {
                preparedStatementTotal.setObject(1, hostId);
                preparedStatementPaid.setObject(1, hostId);
            } else {
                // Set null if hostId not provided
                preparedStatementTotal.setNull(1, java.sql.Types.INTEGER);
                preparedStatementPaid.setNull(1, java.sql.Types.INTEGER);
            }

            // Execute query for total revenue
            try (ResultSet resultSetTotal = preparedStatementTotal.executeQuery()) {
                while (resultSetTotal.next()) {
                    String year = resultSetTotal.getString("year");
                    double totalRevenue = resultSetTotal.getDouble("total_revenue");
                    // Store total revenue in the map
                    yearlyRevenue.computeIfAbsent(year, k -> new LinkedHashMap<>()).put("total_revenue", totalRevenue);
                }
            }

            // Execute query for paid revenue
            try (ResultSet resultSetPaid = preparedStatementPaid.executeQuery()) {
                while (resultSetPaid.next()) {
                    String year = resultSetPaid.getString("year");
                    double totalPaidRevenue = resultSetPaid.getDouble("total_revenue_paid");
                    // Store paid revenue in the map
                    yearlyRevenue.computeIfAbsent(year, k -> new LinkedHashMap<>()).put("total_revenue_paid",
                            totalPaidRevenue);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching yearly revenue: " + e.getMessage());
        }

        // Return the map containing the yearly revenue data
        return yearlyRevenue;
    }

    // Method to get monthly revenue trend for a given year and host
    public Map<String, Map<String, Double>> getMonthlyRevenueTrend(String selectedYear, Integer hostId) {
        Map<String, Map<String, Double>> monthlyRevenue = new LinkedHashMap<>(); // To preserve month order

        // SQL query to calculate total revenue for each month
        String sqlTotalRevenue = """
                    SELECT
                        TO_CHAR(d, 'YYYY-MM') AS month,
                        SUM(p.amount) AS total_revenue
                    FROM (
                        SELECT generate_series(start_date, end_date, INTERVAL '1 month') AS d, ra.ra_id
                        FROM rental_agreement ra
                        JOIN host_rental_agreement hra ON ra.ra_id = hra.ra_id
                        WHERE ra.status IN ('ACTIVE', 'COMPLETED')
                          AND COALESCE(?, hra.host_id) = hra.host_id
                    ) AS date_series
                    JOIN payment p ON p.ra_id = date_series.ra_id
                    WHERE :yearFilter
                    GROUP BY TO_CHAR(d, 'YYYY-MM')
                    ORDER BY TO_CHAR(d, 'YYYY-MM') ASC;
                """;

        // SQL query to calculate paid revenue for each month
        String sqlPaidRevenue = """
                    SELECT
                        TO_CHAR(d, 'YYYY-MM') AS month,
                        SUM(p.amount) AS total_revenue_paid
                    FROM (
                        SELECT generate_series(start_date, end_date, INTERVAL '1 month') AS d, ra.ra_id
                        FROM rental_agreement ra
                        JOIN host_rental_agreement hra ON ra.ra_id = hra.ra_id
                        WHERE ra.status IN ('ACTIVE', 'COMPLETED')
                          AND COALESCE(?, hra.host_id) = hra.host_id
                    ) AS date_series
                    JOIN payment p ON p.ra_id = date_series.ra_id
                    WHERE p.status = 'PAID'
                      AND :yearFilter
                    GROUP BY TO_CHAR(d, 'YYYY-MM')
                    ORDER BY TO_CHAR(d, 'YYYY-MM') ASC;
                """;

        // Dynamic year filter based on the selected year
        String yearFilter = selectedYear.equals("All") ? "1=1" : "EXTRACT(YEAR FROM d) = ?"; // Handle "All" year option
        sqlTotalRevenue = sqlTotalRevenue.replace(":yearFilter", yearFilter);
        sqlPaidRevenue = sqlPaidRevenue.replace(":yearFilter", yearFilter);

        try (PreparedStatement preparedStatementTotal = connection.prepareStatement(sqlTotalRevenue);
                PreparedStatement preparedStatementPaid = connection.prepareStatement(sqlPaidRevenue)) {

            // Set hostId parameter if provided
            if (hostId != null) {
                preparedStatementTotal.setObject(1, hostId);
                preparedStatementPaid.setObject(1, hostId);
            } else {
                preparedStatementTotal.setNull(1, java.sql.Types.INTEGER);
                preparedStatementPaid.setNull(1, java.sql.Types.INTEGER);
            }

            // Set the year parameter for both queries if not "All"
            if (!selectedYear.equals("All")) {
                preparedStatementTotal.setInt(2, Integer.parseInt(selectedYear));
                preparedStatementPaid.setInt(2, Integer.parseInt(selectedYear));
            }

            // Execute total revenue query and populate the map
            try (ResultSet resultSetTotal = preparedStatementTotal.executeQuery()) {
                while (resultSetTotal.next()) {
                    String month = resultSetTotal.getString("month");
                    double totalRevenue = resultSetTotal.getDouble("total_revenue");
                    // Add total revenue to the map
                    monthlyRevenue.computeIfAbsent(month, k -> new LinkedHashMap<>()).put("total_revenue",
                            totalRevenue);
                }
            }

            // Execute paid revenue query and populate the map
            try (ResultSet resultSetPaid = preparedStatementPaid.executeQuery()) {
                while (resultSetPaid.next()) {
                    String month = resultSetPaid.getString("month");
                    double totalPaidRevenue = resultSetPaid.getDouble("total_revenue_paid");
                    // Add paid revenue to the map
                    monthlyRevenue.computeIfAbsent(month, k -> new LinkedHashMap<>()).put("total_revenue_paid",
                            totalPaidRevenue);
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.err.println("Error fetching monthly revenue: " + e.getMessage());
        } catch (NumberFormatException e) {
            // Handle invalid year format
            System.err.println("Invalid year format: " + e.getMessage());
        }

        // Return the map containing the monthly revenue data
        return monthlyRevenue;
    }

    // Method to retrieve a list of available years from rental agreements
    public List<String> getAvailableYears() {
        List<String> years = new ArrayList<>(); // Initialize a list to store years

        // SQL query to select distinct years from start_date and end_date of rental
        // agreements
        String sql = """
                    SELECT DISTINCT year FROM (
                        SELECT EXTRACT(YEAR FROM start_date) AS year FROM rental_agreement
                        UNION
                        SELECT EXTRACT(YEAR FROM end_date) AS year FROM rental_agreement
                    ) AS combined_years
                    ORDER BY year ASC;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            years.add("All"); // Add "All" option first to allow selection of all years

            // Iterate through the result set and add each year to the list
            while (resultSet.next()) {
                years.add(resultSet.getString("year"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available years: " + e.getMessage()); // Print error if any exception
                                                                                     // occurs
        }

        return years; // Return the list of years (including the "All" option)
    }

    // Method to retrieve the top 5 properties based on total revenue generated
    public Map<String, Double> getTop5PropertiesByRevenue(Integer hostId) {
        Map<String, Double> top5Properties = new LinkedHashMap<>(); // To store top 5 properties by revenue

        // SQL query to calculate the total revenue per property
        String sql = """
                    SELECT
                        p.property_id AS property_id,
                        SUM(
                            (
                                EXTRACT(YEAR FROM age(ra.end_date, ra.start_date)) * 12 +  -- Year difference in months
                                EXTRACT(MONTH FROM age(ra.end_date, ra.start_date))  -- Month difference
                            ) * ra.fee  -- Multiply by the rental fee
                        ) AS total_payment
                    FROM rental_agreement ra
                    JOIN property p ON ra.property_id = p.property_id
                    JOIN host_rental_agreement hra ON ra.ra_id = hra.ra_id
                    WHERE ra.status IN ('ACTIVE', 'COMPLETED')
                        AND hra.host_id = COALESCE(?, hra.host_id)
                    GROUP BY p.property_id
                    ORDER BY total_payment DESC
                    LIMIT 5;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set hostId parameter if provided
            if (hostId != null) {
                preparedStatement.setInt(1, hostId);
            } else {
                preparedStatement.setNull(1, java.sql.Types.INTEGER);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Add top 5 properties to map
                while (resultSet.next()) {
                    String propertyId = "Property ID: " + resultSet.getInt("property_id");
                    double totalPayment = resultSet.getDouble("total_payment");
                    top5Properties.put(propertyId, totalPayment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching top 5 properties by revenue: " + e.getMessage());
        }

        return top5Properties; // Return the top 5 properties with their total revenue
    }

    // Method to get the top 10 rental agreements with the highest number of overdue
    // payments
    public Map<Integer, Integer> getTop10OverduePaymentsByRA(Integer hostId) {
        Map<Integer, Integer> overduePayments = new LinkedHashMap<>(); // To store rental agreement IDs and overdue
                                                                       // counts

        // SQL query to fetch the top 10 rental agreements with the most overdue
        // payments
        String sql = """
                    WITH months AS (
                        SELECT ra.ra_id, ra.start_date, ra.end_date, ra.fee, ra.period,
                            generate_series(
                                ra.start_date,
                                LEAST(ra.end_date, CURRENT_DATE),  -- Stop at the current date
                                '1 month'::interval
                            ) AS contract_month
                        FROM rental_agreement ra
                        WHERE ra.status IN ('ACTIVE', 'COMPLETED')
                    ),
                    overdue AS (
                        SELECT m.ra_id, m.contract_month,
                            CASE
                                WHEN m.period = 'MONTHLY' THEN m.fee
                                WHEN m.period = 'YEARLY' THEN m.fee / 12
                            END AS monthly_fee
                        FROM months m
                        LEFT JOIN payment p
                            ON m.ra_id = p.ra_id
                            AND p.status = 'PAID'
                            AND DATE_TRUNC('month', p.payment_date) = m.contract_month
                        WHERE p.payment_id IS NULL
                            AND m.contract_month < CURRENT_DATE
                    )
                    SELECT
                        ra.ra_id,
                        COALESCE(COUNT(o.contract_month), 0) AS overdue_count
                    FROM rental_agreement ra
                    LEFT JOIN overdue o ON ra.ra_id = o.ra_id
                    JOIN host_rental_agreement hra ON ra.ra_id = hra.ra_id
                    WHERE ra.status IN ('ACTIVE', 'COMPLETED')
                        AND hra.host_id = COALESCE(?, hra.host_id)
                    GROUP BY ra.ra_id
                    ORDER BY overdue_count DESC
                    LIMIT 10;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set hostId parameter if provided
            if (hostId != null) {
                preparedStatement.setInt(1, hostId);
            } else {
                preparedStatement.setNull(1, java.sql.Types.INTEGER);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                // Add rental agreements with the most overdue payments to the map
                while (resultSet.next()) {
                    int raId = resultSet.getInt("ra_id");
                    int overdueCount = resultSet.getInt("overdue_count");
                    overduePayments.put(raId, overdueCount);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching overdue payments: " + e.getMessage());
        }

        return overduePayments; // Return the map of rental agreements with overdue payments
    }

    // Method to retrieve the count of vacant vs rented properties for a given host
    public Map<String, Integer> getVacantVsRentedProperties(Integer hostId) {
        Map<String, Integer> propertyStatusCount = new LinkedHashMap<>();

        // SQL query to count vacant and rented properties
        String sql = """
                    SELECT
                        CASE
                            WHEN ra.property_id IS NULL THEN 'Vacant'
                            ELSE 'Rented'
                        END AS property_status,
                        COUNT(DISTINCT p.property_id) AS property_count
                    FROM property p
                    LEFT JOIN rental_agreement ra ON p.property_id = ra.property_id
                        AND ra.status IN ('ACTIVE', 'COMPLETED')
                    LEFT JOIN host_rental_agreement hra ON ra.ra_id = hra.ra_id
                    WHERE hra.host_id = COALESCE(?, hra.host_id)
                    GROUP BY property_status
                    ORDER BY property_status;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (hostId != null) {
                preparedStatement.setInt(1, hostId);
            } else {
                preparedStatement.setNull(1, java.sql.Types.INTEGER);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String propertyStatus = resultSet.getString("property_status");
                    int propertyCount = resultSet.getInt("property_count");
                    propertyStatusCount.put(propertyStatus, propertyCount);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching vacant vs rented properties: " + e.getMessage());
        }

        return propertyStatusCount;
    }

    // Method to retrieve a list of years from rental agreements
    public List<String> getYears() {
        List<String> years = new ArrayList<>();

        // SQL query to get distinct years
        String sql = """
                    SELECT DISTINCT year FROM (
                        SELECT EXTRACT(YEAR FROM start_date) AS year FROM rental_agreement
                    ) AS combined_years
                    ORDER BY year ASC;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            years.add("All");

            while (resultSet.next()) {
                years.add(resultSet.getString("year"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available years: " + e.getMessage());
        }

        return years;
    }

    // Method to get rental counts for all years, grouped by month
    public Map<Integer, Integer> getRentalCountsForAllYears(Integer hostId) {
        Map<Integer, Integer> rentalCountsByMonth = new LinkedHashMap<>();

        // Initialize map with 0 counts for all months
        for (int month = 1; month <= 12; month++) {
            rentalCountsByMonth.put(month, 0);
        }

        String sql = """
                    SELECT
                        EXTRACT(MONTH FROM ra.start_date) AS month,
                        COUNT(ra.ra_id) AS rental_count
                    FROM rental_agreement ra
                    LEFT JOIN host_rental_agreement hra ON ra.ra_id = hra.ra_id
                    WHERE ra.status IN ('ACTIVE', 'COMPLETED')
                    AND (hra.host_id = COALESCE(?, hra.host_id))
                    GROUP BY EXTRACT(MONTH FROM ra.start_date)
                    ORDER BY month;
                """;

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            if (hostId != null) {
                statement.setInt(1, hostId);
            } else {
                statement.setNull(1, java.sql.Types.INTEGER);
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int month = resultSet.getInt("month");
                int rentalCount = resultSet.getInt("rental_count");
                rentalCountsByMonth.put(month, rentalCount);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rental counts by month for all years: " + e.getMessage());
        }

        return rentalCountsByMonth;
    }

    // Method to get monthly rental counts for a specific host
    public Map<Integer, Integer> getMonthlyRentalCount(Integer hostId) {
        ResultSet resultSet = null;
        Map<Integer, Integer> monthlyRentalCounts = new LinkedHashMap<>();

        // SQL query to get rental counts by month for a specific host
        String sql = """
                    SELECT
                        EXTRACT(MONTH FROM ra.start_date) AS month,
                        COUNT(ra.ra_id) AS rental_count
                    FROM rental_agreement ra
                    WHERE
                        ra.status IN ('ACTIVE', 'COMPLETED')
                        AND ra.ra_id IN (
                            SELECT ra_id
                            FROM host_rental_agreement
                            WHERE host_id = COALESCE(?, host_id)
                        )
                    GROUP BY EXTRACT(MONTH FROM ra.start_date)
                    ORDER BY month;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set the hostId parameter
            if (hostId != null) {
                preparedStatement.setInt(1, hostId);
            } else {
                preparedStatement.setNull(1, java.sql.Types.INTEGER);
            }

            resultSet = preparedStatement.executeQuery();

            // Fetch rental counts for each month
            while (resultSet.next()) {
                int month = resultSet.getInt("month");
                int rentalCount = resultSet.getInt("rental_count");
                monthlyRentalCounts.put(month, rentalCount);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching monthly rental counts: " + e.getMessage());
        } finally {
            // Close ResultSet
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }

        return monthlyRentalCounts;
    }

    // Method to get rental counts by month for a specific year and host
    public Map<Integer, Integer> getRentalCountsByMonth(Integer year, Integer hostId) {
        ResultSet resultSet = null;
        Map<Integer, Integer> monthlyRentalCounts = new LinkedHashMap<>();

        // SQL query to get rental counts by month for a specific year and host
        String sql = """
                    SELECT
                        EXTRACT(MONTH FROM ra.start_date) AS month,
                        COUNT(ra.ra_id) AS rental_count
                    FROM rental_agreement ra
                    WHERE
                        EXTRACT(YEAR FROM ra.start_date) = ?
                        AND ra.status IN ('ACTIVE', 'COMPLETED')
                        AND ra.ra_id IN (
                            SELECT ra_id
                            FROM host_rental_agreement
                            WHERE host_id = COALESCE(?, host_id)
                        )
                    GROUP BY EXTRACT(MONTH FROM ra.start_date)
                    ORDER BY month;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set parameters for year and hostId
            preparedStatement.setInt(1, year); // Set year parameter
            if (hostId != null) {
                preparedStatement.setInt(2, hostId); // Set host ID parameter
            } else {
                preparedStatement.setNull(2, java.sql.Types.INTEGER); // Null for all hosts
            }

            resultSet = preparedStatement.executeQuery();

            // Fetch monthly rental counts
            while (resultSet.next()) {
                int month = resultSet.getInt("month");
                int rentalCount = resultSet.getInt("rental_count");
                monthlyRentalCounts.put(month, rentalCount);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching monthly rental counts by year: " + e.getMessage());
        } finally {
            // Close ResultSet
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }

        return monthlyRentalCounts;
    }
}
